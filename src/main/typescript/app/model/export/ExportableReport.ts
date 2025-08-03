import {TableModel} from "./TableModel";

export class ExportableReport {

    tables: TableModel[] = []

    constructor(public readonly title: string) {
    }

    addTable(table: TableModel) {
        this.tables.push(table)
    }

    toJson(): any {
        return {
            title:  this.title,
            tables: this.tables.map(table => table.toJson())
        }
    }
}