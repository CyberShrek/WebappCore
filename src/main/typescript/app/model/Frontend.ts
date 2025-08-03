interface FrontendConfig {
    forms?: FormConfig[]
}

interface FormConfig {
    title?: string
    sections?: SectionConfig[]
    submits?: FormSubmitConfig[]
}
interface FormSubmitConfig {
    text?: string
    reports?: ReportConfig[]
    formulaCreate?: string
}
type FormValue = {[fieldId: string]: any }

interface SectionConfig {
    title?: string
    fields?: (SwitchConfig | CalendarConfig | SelectConfig | TextConfig | CodeareaConfig | NumberConfig | ColorConfig)[],
    size?: number
    formulaCreate?: string
}

interface FieldConfig {
    title?: string
    id: string
    type: "switch" | "calendar" | "select" | "text" | "codearea" | "number" | "color"
    info?: string
    default?: any
    formulaCreate?: string
    formulaCorrect?: string
    formulaMessage?: string
}
interface FieldStatementFrame {
    correct?: boolean
    message?: string
    create?: boolean

    isReady?: boolean
    showWrong?: boolean
}

interface SwitchConfig extends FieldConfig{
    type:    "switch"
    basic?:   boolean
    default?: boolean
}

interface CalendarConfig extends FieldConfig {
    type: "calendar"
    minYear?: number
    maxYear?: number
    range?:  boolean
    maxDays?: number
}

interface SelectConfig extends FieldConfig, OptionsSources {
    type:              "select"
    multiple?:         boolean
    search?:           boolean
    showCodes?:        boolean
    placeholder?:       string
    disableSelectAll?: boolean
    maxValues?:         number
    returnType?:       "key" | "value" | "json"
    default?:        OptionKey
}
interface OptionsSources {
    optionsJson?: { [key: OptionKey]: OptionLabel }
    optionsSql?: string

    serviceBankListName?: string
    serviceBankParams?:   string
    serviceBankResultKey?:   string
    serviceBankResultValue?: string
}

interface TextConfig extends FieldConfig {
    type:        "text"
    nullIfEmpty?: boolean
    placeholder?: string
    default?:     string
}

interface CodeareaConfig extends FieldConfig {
    type:        "codearea"
    lang?: "js" | "json" | "sql" | "html"
    instructAI?:   string
    default?:    string
}

interface NumberConfig extends FieldConfig {
    type: "number"
    range?: boolean
    min?: number
    max?: number
    step?: number
    default?: number
}

interface ColorConfig extends FieldConfig {
    type: "color",
    default?: string
}

type EasepickValue = {
    date: string,
    dateEnd?: string
}

type CalendarValue = string | string[]

type SelectValue = // Depending on returnType
    OptionKey | OptionKey[] |
    OptionLabel | OptionLabel[] |
    { [key: OptionKey]: OptionLabel }

type DetailedSelectValue = {
    options: SelectValue,
    all: boolean
}

type SubmitOutput = {
    index: number,
    value: FormValue
}