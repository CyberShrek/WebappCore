import {equal, deepCopyOf} from "../../../app/util/data"
import {Observable} from "./Observable"
import {regexp} from "../../../app/util/string";

export class FormObservatory implements Observable {

    constructor(protected readonly codeWithFieldInjections: string) {
        this.triggerFields = new Map(
            codeWithFieldInjections.match(regexp.injectedField)
                ?.map(field => [field, null])
        )
    }

    public observedFormValue: FormValue
    private triggerFields: Map<string, any> // fieldId, fieldValue

    checkForChanges(formValue: FormValue): boolean {
        console.log("checkForChanges", JSON.stringify(formValue), JSON.stringify(this.observedFormValue))
        if (equal(formValue, this.observedFormValue)) return false
        this.observedFormValue = deepCopyOf(formValue)
        return true

        // let triggered = false
        // this.triggerFields.forEach((value, key, that) => {
        //     if (!compare(formValue?.[key], value)) {
        //         that.set(key, deepCopyOf(formValue[key]))
        //         triggered = true
        //     }
        // })
        // return triggered
    }

    // private remapFormValue(formValue: FormValue): typeof formValue {
    //     const remapped: typeof formValue = {}
    //     this.triggerFields.forEach((value, key) => {
    //         if(formValue?.[key] != null)
    //             remapped[key] = formValue[key]
    //     })
    //     return remapped
    // }
}