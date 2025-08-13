import {Http, Method, ResourceType} from "./Http"

export class SimpleHttp extends Http {

    static withHeaders = (headers?: object) => { return {
        andBody: (payload?: object) => { return {
            post(url: string) {return SimpleHttp.withAll(url, "POST", headers, payload)},
            put(url: string)  {return SimpleHttp.withAll(url, "PUT",  headers, payload)},
            patch(url: string) {return SimpleHttp.withAll(url, "PATCH", headers, payload)},
            delete(url: string) {return SimpleHttp.withAll(url, "DELETE", headers, payload)}
        }},
        get(url: string) {return SimpleHttp.withAll(url, "GET", headers, undefined)}
    }}

    static withBody = (payload?: object) => SimpleHttp.withHeaders().andBody(payload)

    static justGet = (url: string) => SimpleHttp.withHeaders().get(url)

    private static withAll(url: string,
                           method: Method,
                           headers: object,
                           payload: object) {
        const sendFor = (type: ResourceType) => Http.send(url, method, headers, payload, type)
        return {
            text(): Promise<string> {return sendFor("text")},
            json<T>(): Promise<T> {return sendFor("json")},
            blob(): Promise<Blob>   {return sendFor("blob")}
        }
    }

    protected constructor() {
        super();
    }
}