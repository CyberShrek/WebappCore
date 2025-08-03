import {Retrievable} from "./abstract/Retrievable"
import {SimpleHttp} from "../../app/api/././simple/SimpleHttp"
import {serverLocations} from "../../app/properties"
import {FormObservatory} from "./abstract/FormObservatory"

export class QueryPerformer extends FormObservatory implements Retrievable<SimpleSet> {
    constructor(readonly sql: string) {
        super(sql)
    }

    retrieve(): Promise<SimpleSet> {
        return SimpleHttp
            .withHeaders({"Query": this.sql})
            .andBody(this.observedFormValue)
            .post(serverLocations.query)
            .json<SimpleSet>()
    }
}