import {serverLocations} from "../properties"
import {SimpleHttp} from "./http/SimpleHttp"

export async function getOptionsFromSB(listName: string,
                                       listKeyName: string,
                                       listValueName: string,
                                       params: { [key: string]: string | number | boolean } = {}): Promise<{ [key: string]: string }> {
    const options: { [key: string]: string } = {}
    const sbResponse = await SimpleHttp
        .withBody(JSON.parse(`{"${listName}":[${JSON.stringify(params)}]}`))
        .post(serverLocations.serviceBank)
        .json<object[]>()

    sbResponse[listName].forEach(item => {
        const key = item[listKeyName] as string
        const value = item[listValueName] as string
        if (key != null)
            options[key] = value
    })
    return options
}