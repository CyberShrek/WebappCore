import {resolveModule, resolveStyle} from "../util/resolver"
import {virtualSelectProperties} from "../properties"
import {equal} from "../util/data"
import {InputModule} from "./abstract/InputModule"

export type Option = {
    label: string
    value: string
    alias: string
    description: string
}

const modulePromise = Promise.all([
    resolveStyle("third-party/virtual-select"),
    resolveModule("third-party/virtual-select.min")
])

export class VirtualSelectModule extends InputModule<string[]>{

    private options: Option[] = []
    private readonly mountPromise: Promise<void>

    constructor(private rootElement: HTMLElement,
                readonly config: {
                    multiple?:         boolean
                    search?:           boolean
                    showCodes?:        boolean
                    placeholder?:      string
                    disableSelectAll?: boolean
                    maxValues?:        number
                    default?:          string
                }) {

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

    override async setValue(optionKeys: string[]){
        await this.mountPromise
        if(equal(this.value, optionKeys)) return
        super.setValue(optionKeys, true)
    }

    setOptions(newOptions: Option[]) {
        return this.mountPromise.then(() => {
            if (!equal(this.options, newOptions)) {
                if (newOptions && newOptions.length > 0) {
                    this.rootElement// @ts-ignore Resolved by module import
                        .enable()
                    this.rootElement // @ts-ignore Resolved by module import
                        .setOptions(newOptions)
                } else {
                    this.rootElement // @ts-ignore Resolved by module import
                        .disable()
                    this.rootElement // @ts-ignore Resolved by module import
                        .reset()
                    this.rootElement.blur()
                }
                this.options = newOptions
            }
        })
    }

    open(){
        this.rootElement // @ts-ignore Resolved by module import
            ?.open()
    }
    close(){
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