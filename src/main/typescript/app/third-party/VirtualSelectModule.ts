import {resolveModule, resolveStyle} from "../util/resolver"
import {virtualSelectProperties} from "../properties"
import {equal, compareMaps, mapToJson, mapToVirtualSelectOptions} from "../util/data"
import {InputModule} from "./abstract/InputModule";
const modulePromise = Promise.all([
    resolveStyle("third-party/virtual-select"),
    resolveModule("third-party/virtual-select.min")
])

export class VirtualSelectModule extends InputModule<OptionKey[]>{

    private options: OptionsMap = new Map<string, string>()
    private readonly mountPromise: Promise<void>

    constructor(private rootElement: HTMLElement,
                readonly config: SelectConfig) {

        super((newKeys: string[]) => {
            rootElement
                // @ts-ignore Resolved by module import
                ?.setValue?.(newKeys)
        })

        this.value = []

        this.mountPromise = new Promise(resolve => {
            modulePromise.then(() => {
                // @ts-ignore Resolved by module import
                VirtualSelect.init({
                    ...virtualSelectProperties,
                    ele: rootElement,
                    multiple: !!config.multiple,
                    search: !!config.search,
                    placeholder: config.placeholder ?? "",
                    hasOptionDescription: !!config.showCodes,
                    disableSelectAll: !!config.disableSelectAll,
                    maxValues: config.maxValues,
                    searchPlaceholderText: (config.multiple && !config.disableSelectAll)
                        ? virtualSelectProperties.selectAllText
                        : virtualSelectProperties.searchPlaceholderText,
                    autofocus: false
                })
                rootElement.addEventListener("change", event => {
                    const newValue = event.currentTarget
                        // @ts-ignore Resolved by module import
                        .value
                    super.setValue(newValue.length > 0 ? (typeof newValue === "object" ? newValue : [newValue]) : [], false)
                })
                resolve()
            })
        })
    }

    override async setValue(optionKeys: OptionKey[]){
        await this.mountPromise
        if(equal(this.value, optionKeys)) return
        super.setValue(optionKeys, true)
    }

    getDetailedValue(): DetailedSelectValue{
        return {
            options: mapToJson(this.findOptions(this.value)),
            all: this.value.length === this.options.size
        }
    }

    setOptions(newOptions: OptionsMap) {
        return this.mountPromise.then(() => {
            if (!compareMaps(this.options, newOptions)) {
                if (newOptions && newOptions.size > 0) {
                    this.rootElement// @ts-ignore Resolved by module import
                        .enable()
                    this.rootElement // @ts-ignore Resolved by module import
                        .setOptions(mapToVirtualSelectOptions(newOptions))
                } else {
                    this.rootElement // @ts-ignore Resolved by module import
                        .disable()
                    this.rootElement // @ts-ignore Resolved by module import
                        .reset()
                    this.rootElement.blur()
                }
                this.options = new Map(newOptions)
            }
        })
    }

    findOptions=(keys: OptionKey[]): OptionsMap => {
        const result: OptionsMap = new Map()
        keys.forEach(key => {
            if (this.options.has(key))
                result.set(key, this.options.get(key))
        })
        return result
    }

    open(){
        console.log("open")
        this.rootElement // @ts-ignore Resolved by module import
            ?.open()
    }
    close(){
        console.log("close")
        this.rootElement // @ts-ignore Resolved by module import
            ?.close()
    }
    focus(){
        this.rootElement // @ts-ignore Resolved by module import
            ?.focus()
    }
    enable(){
        this.rootElement // @ts-ignore Resolved by module import
            ?.enable()
    }
    disable(){
        this.rootElement // @ts-ignore Resolved by module import
            ?.disable()
    }
    override destroy() {
        try{this.rootElement // @ts-ignore Resolved by module import
            ?.destroy()}
        catch{}
    }
}