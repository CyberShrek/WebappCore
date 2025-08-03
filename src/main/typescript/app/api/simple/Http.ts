import {addCursorLoader, removeCursorLoader} from "../util/dom/common"
import {popupError} from "../util/alert"
import {serverLocations, serverErrors} from "../properties"

export type Method = "GET" | "POST" | "PUT" | "PATCH" | "DELETE"
export type ResourceType = "json" | "text" | "blob"

export abstract class Http {

    protected static get     = (url: string,
                                headers?: object,
                                type?: ResourceType) => Http.send(url,"GET", headers, undefined, type)

    protected static put     = (url: string,
                         headers?: object,
                         payload?: object,
                                type?: ResourceType) => Http.send(url,"PUT", headers, payload,        type)

    protected static post    = (url: string,
                         headers?: object,
                         payload?: object,
                                type?: ResourceType) => Http.send(url,"POST", headers, payload,        type)

    protected static delete  = (url: string,
                         headers?: object,
                         payload?: object,
                                type?: ResourceType) => Http.send(url,"DELETE", headers, payload,        type)

    protected static async send(url: string,
                       method: Method,
                       headers?: object,
                       payload?: object,
                                type: ResourceType = "json")
    {
        addCursorLoader()
        let response = await fetch(
            url, {
                method,
                headers: prepareHeaders(headers ?? {}),
                body: payload ? JSON.stringify(payload) : undefined
            })
            .finally(() => removeCursorLoader())
        if(response.ok) {
            return await response[type]()
        }
        else {
            const error = new Error(await response.text())
            error.name = `${response.status}: ${response.statusText}`
            await popupError(error, serverErrors[Object.keys(serverLocations).find(key => serverLocations[key] === url) ?? -1])
            throw error
        }
    }
}

async function getContentLength(response: Response): Promise<number> {
    const responseClone = response.clone();
    const reader = responseClone.body.getReader();
    let contentLength = 0;
    while (true) {
        const { done, value } = await reader.read();
        if (done) break;
        contentLength += value.length;
    }
    return contentLength
}

function prepareHeaders(headers: object) {
    Object.keys(headers)
        .forEach(key => headers[key] = encodeURIComponent(headers[key]))

    return {
        "Content-Type"  : "application/json;charset=UTF-8",
        // "Code"          :  appConfig.code ?? "",
        ...headers
    }
}