interface NickReportConfig {
    title?: string
    id?:    string
    icon?:  string
}
interface ReportConfig extends NickReportConfig {
    query?:     string
    context?:   string
    isModal?:  boolean
    content?:  ReportContentConfig[]
    children?: ReportConfig[]
    debugSet?: SimpleSet
    formulaCreate?: boolean
}

interface ReportContentConfig {
    title?: string
    query?: string
    type?: "tiles" | "charts" | "table" | "list"
    modal?: boolean
    formulaCreate?: boolean
}

//////////
// Tile //
//////////
interface TilesBlockConfig extends ReportContentConfig {
    type?: "tiles"
    tiles?: TileConfig[]
    size?: number
}
interface TileConfig {
    title?: string
    html?: string
    background?: string
    color?: string
    childButtonCondition?: string
}

///////////
// CHART //
///////////
interface ChartsBlockConfig extends ReportContentConfig {
    type?: "charts"
    charts?: ChartConfig[]
    size?: number
}
interface ChartConfig {
    title?: string
    formulaLabels?: string
    views?: (LineChartConfig | BarChartConfig | PieChartConfig)[]
}
interface ChartViewConfig {
    type?: "line" | "bar" | "pie"
    label?: string
    formulaData?: string
    color?: string
}
interface LineChartConfig extends ChartViewConfig {
    type?: "line"
}
interface BarChartConfig extends ChartViewConfig {
    type?: "bar"
}
interface PieChartConfig extends ChartViewConfig {
    type?: "pie"
}

///////////
// TABLE //
///////////
interface TableConfig extends ReportContentConfig {
    type?: "table"
    group?: boolean
    columns?: ColumnConfig[]
    childReportIds?: string[]
}

interface ColumnConfig extends TileConfig {
    title?: string
    type?: ColumnType
    formula?: string
    aggregate?: boolean
    formulaAggregate?: string
    tileMode?: boolean
    tileBorder?: boolean
}
type ColumnType = "text" | "number" | "boolean"

//////////
// LIST //
//////////
interface ListConfig extends ReportContentConfig {
    type?: "list"
}

type MatrixData = RowData[]
type RowData = CellData[]
type CellData = string | number | boolean

type ReportCall = {
    id: string,
    data: MatrixData
}