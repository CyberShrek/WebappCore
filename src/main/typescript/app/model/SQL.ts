interface SimpleSet {
    columnNames: string[],
    data: SqlCell[][]
}

type SqlCell = string|number|boolean

type SqlType = "text" | "number" | "boolean"