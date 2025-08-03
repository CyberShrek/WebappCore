import {concatMaps, jsonToMap} from "../../../common/util/data";
import {popupError} from "../../../common/util/alert";
import {SimpleHttp} from "../../api/././simple/SimpleHttp";
import {serverLocations} from "../../../common/properties";
import {SqlPerformer} from "../../api/SqlPerformer";
import {getUser} from "../../api/crud";

let user: UserDetails

export class OptionsProvider {

    private staticOptions:      OptionsMap = new Map()
    private sqlOptions:         OptionsMap = new Map()
    private serviceBankOptions: OptionsMap = new Map()

    constructor(readonly sources: OptionsSources) {
        if(sources.optionsJson) {
            try {
                if(typeof sources.optionsJson === "string")
                    sources.optionsJson = JSON.parse(sources.optionsJson)

                this.staticOptions = jsonToMap(sources.optionsJson)
            }
            catch (error) {
                popupError(error, "Ошибка инициализации статических опций")
            }
        }
    }

    async retrieve(form: FormValue): Promise<OptionsMap> {
        if (this.sources.serviceBankListName) {
            await this.tryToRetrieveServiceBankOptions(form)
        }
        if (this.sources.optionsSql) {
            await this.tryToRetrieveSqlOptions(form)
        }
        return concatMaps(this.staticOptions, this.serviceBankOptions, this.sqlOptions)
    }

    private async tryToRetrieveServiceBankOptions(form: FormValue) {
        const body = await this.prepareServiceBankBody(form)
        try {
            this.serviceBankOptions = this.parseServiceBankResponse(
                await SimpleHttp
                    .withHeaders()
                    .andBody(JSON.parse(body))
                    .post(serverLocations.serviceBank)
                    .json<object>())
        } catch (error) {
            await popupError(new Error(error + "\n\n" + body),
                "Не удалось загрузить опции из банка микросервисов")
        }
    }

    private async tryToRetrieveSqlOptions(form: FormValue) {
        try {
            this.sqlOptions = new Map(

                (await SqlPerformer.calculateQuery(this.sources.optionsSql, form))
                    .data
                    .map(row => [String(row[0]), String(row[1])])
            )
        } catch (error) {
            await popupError(new Error(error + "\n\n" + this.sources.optionsSql), "Не удалось загрузить опции")
        }
    }

    private async prepareServiceBankBody(form: FormValue): Promise<string> {
        let body = `{"${this.sources.serviceBankListName}":[${this.sources.serviceBankParams ?? ""}]}`
        if (user == null)
            user = await getUser(sessionStorage.getItem("code"))
        Object.entries(user).forEach(([key, value]) => {
            body = body.replace(new RegExp(`\\$user\\.${key}(?=\\W|$)`, "g"), String(value))
        })
        Object.entries(form).forEach(([key, value]) => {
            body = body.replace(new RegExp(`\\$${key}(?=\\W|$)`, "g"), String(value))
        })
        return body
    }

    private parseServiceBankResponse(response: object) {
        const options: OptionsMap = new Map();
        (response[this.sources.serviceBankListName] as object[]).forEach(item => {
            const key = item[this.sources.serviceBankResultKey] as string
            const value = item[this.sources.serviceBankResultValue] as string
            if (key != null)
                options.set(key, value)
        })
        return options
    }
}