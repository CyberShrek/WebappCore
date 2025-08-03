import {SimpleHttp} from "./././simple/SimpleHttp";
import {serverLocations} from "../properties";

export abstract class SqlPerformer {

    static async calculateFormulas(formulas: string[], form: FormValue = {}): Promise<SqlCell[]> {
        return (await SimpleHttp
            .withHeaders({"Formulas": formulas.join(", ")})
            .andBody(form)
            .post(serverLocations.formulas)
            .json<SimpleSet>())
            .data?.[0]
    }

    static async calculateQuery(query: string, form: FormValue = {}): Promise<SimpleSet> {
        return (await SimpleHttp
            .withHeaders({"Query": query})
            .andBody(form)
            .post(serverLocations.query)
            .json<SimpleSet>())
    }
}