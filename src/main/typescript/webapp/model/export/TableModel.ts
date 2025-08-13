
const NULL_CELL_CLASSES = ['collapsed'];
const IGNORED_CELL_CLASSES = ['checkbox'];
const IGNORED_ROW_CLASSES = ['operations', 'tool-bar'];

// Типы данных
export type ColumnType = "text" | "number" | "boolean";
export type CellData = string | number | boolean;

// Основная модель таблицы
export class TableModel {
    head: RowModel[] = [];
    body: RowModel[] = [];

    constructor(public readonly  title: string,
                private readonly htmlTable: HTMLTableElement) {
        this.parseTable();
    }

    toJson(): any {
        return {
            title: this.title,
            head:  this.head.map(row => row.toJson()),
            body:  this.body.map(row => row.toJson())
        };
    }

    private parseTable() {
        // Обработка заголовка (thead)
        if (this.htmlTable.tHead) {
            for (let i = 0; i < this.htmlTable.tHead.rows.length; i++) {
                const row = this.htmlTable.tHead.rows[i];
                if (!this.isRowIgnored(row)) {
                    this.head.push(new RowModel(row));
                }
            }
        }

        // Обработка тела (tbody)
        const tBodies = this.htmlTable.tBodies;
        for (let i = 0; i < tBodies.length; i++) {
            for (let j = 0; j < tBodies[i].rows.length; j++) {
                const row = tBodies[i].rows[j];
                if (!this.isRowIgnored(row)) {
                    this.body.push(new RowModel(row));
                }
            }
        }

        // Обработка подвала (tfoot)
        if (this.htmlTable.tFoot) {
            for (let i = 0; i < this.htmlTable.tFoot.rows.length; i++) {
                const row = this.htmlTable.tFoot.rows[i];
                if (!this.isRowIgnored(row)) {
                    this.body.push(new RowModel(row));
                }
            }
        }
    }

    private isRowIgnored(row: HTMLTableRowElement): boolean {
        return IGNORED_ROW_CLASSES.some(cls => row.classList.contains(cls));
    }
}

// Модель строки
export class RowModel {
    cells: CellModel[] = [];

    constructor(private readonly htmlTableRow: HTMLTableRowElement) {
        this.parseRow();
    }

    toJson(): any {
        return {
            cells: this.cells.map(cell => cell ? cell.toJson() : null)
        };
    }

    private parseRow() {
        const cells = this.htmlTableRow.cells;
        for (let i = 0; i < cells.length; i++) {
            const cell = cells[i];

            // Пропуск скрытых и игнорируемых ячеек
            if (cell.style.display === "none" || IGNORED_CELL_CLASSES.some(cls => cell.classList.contains(cls)))
                continue;

            // Обработка null-ячеек
            if (NULL_CELL_CLASSES.some(cls => cell.classList.contains(cls))) {
                this.cells.push(null);
            } else {
                this.cells.push(new CellModel(cell));
            }
        }
    }
}

// Модель ячейки
export class CellModel {
    value: CellData;
    colspan: number;
    rowspan: number;
    total: boolean;
    type: ColumnType;

    constructor(htmlTableCell: HTMLTableCellElement) {
        // Определение типа данных
        this.type = "text";
        if      (htmlTableCell.classList.contains("number"))  this.type = "number";
        else if (htmlTableCell.classList.contains("boolean")) this.type = "boolean";

        // Извлечение значения
        this.value = this.convertValue(
            htmlTableCell.hasAttribute("value")
                ? htmlTableCell.getAttribute("value")
                : getDirectText(htmlTableCell),
            this.type);

        // Атрибуты объединения ячеек
        this.colspan = htmlTableCell.colSpan;
        this.rowspan = htmlTableCell.rowSpan;

        // Проверка на total-ячейку
        this.total = htmlTableCell.classList.contains("total");
    }

    toJson(): any {
        console.log(this.value);
        return {
            value:   this.value,
            type:    this.type,
            colspan: this.colspan,
            rowspan: this.rowspan,
            total:   this.total
        };
    }

    private convertValue(text: string, type: ColumnType): CellData {
        switch (type) {
            case "number":
                const num = parseFloat(text);
                return isNaN(num) ? 0 : num;
            case "boolean":
                const normalized = text.trim().toLowerCase();
                return normalized === "true" || normalized === "1";
            default:
                return text;
        }
    }
}

// Вспомогательная функция для извлечения текста
function getDirectText(element: HTMLElement): string {
    let result = '';
    for (let i = 0; i < element.childNodes.length; i++) {
        const node = element.childNodes[i];
        if (node.nodeType === Node.TEXT_NODE) {
            result += node.textContent;
        }
    }
    return result;
}