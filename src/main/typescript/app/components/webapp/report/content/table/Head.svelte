<script lang="ts">

    import Field from "../../../form/Field.svelte";
    import MainCheckbox from "./MainCheckbox.svelte";
    import PagesBar from "../../../navigation/PagesBar.svelte";
    import {scrollIntoElement} from "../../../../../common/util/dom/common";
    import Sort from "../../../../../common/components/input/Sort.svelte";
    import Text from "../../../../../common/components/input/Text.svelte";

    export let
        config: TableConfig,
        allNickChildren: NickReportConfig[],
        editable: boolean = false,
        rowsCount: number,
        checkedArray: boolean[],
        // Provides
        operations: {
            filter: string
            sort: "asc" | "desc"
        }[] = []

    let theadEl: HTMLTableSectionElement,
        tbodyEl: HTMLTableSectionElement,
        columnTitles: string[][],
        spannedColumns: SpannedTitle[][] = [],
        pickedPageNumber: number,
        pointedRowIndex: number = 0,
        pageSize: number

    type SpannedTitle = {
        title: string
        colspan: number
        rowspan: number
    }

    $: if (config){
        updateTitles()
        updateOperations()
    }

    $: if(pickedPageNumber != null && pageSize != null){
        scrollToPage()
    }

    $: tbodyEl = (theadEl?.parentElement as HTMLTableElement)?.tBodies[0]

    $: if(tbodyEl)
        SetupRowsPointing()

    function updateTitles(){
        columnTitles = config.columns.map(column => column.title.split('|'))
        spannedColumns = columnTitles.map(column => spanColumnOfTitles(column))
        spannedColumns.forEach((spannedColumn, colI) => {
            spannedColumn.forEach((spanned, rowI) => {
                if(spanned != null) {
                    let nextSpanned: SpannedTitle
                    do {
                        if(nextSpanned != null)   spannedColumns[colI + spanned.colspan][rowI] = null
                        spanned.colspan++
                        nextSpanned = spannedColumns[colI + spanned.colspan]?.[rowI]
                    } while (
                        nextSpanned != null &&
                        nextSpanned.title === spanned.title &&
                        nextSpanned.rowspan === spanned.rowspan)
                }
            })
        })
    }

    function spanColumnOfTitles(column: string[]): SpannedTitle[]{
        const spannedColumn: SpannedTitle[] = [],
            maxLength = getHeight(columnTitles)
        column.forEach((title, index) => {
            let spannedTitle: SpannedTitle = spannedColumn[spannedColumn.length - 1]

            if(!spannedTitle || spannedTitle.title !== title) {
                spannedColumn.push({title, colspan: 0, rowspan: 0})
                spannedTitle = spannedColumn[spannedColumn.length - 1]
            }
            if(column.length < maxLength && index === column.length - 1)
                spannedTitle.rowspan = maxLength - index
            else if (spannedTitle.title === title)
                spannedTitle.rowspan++
        })
        return spannedColumn
    }

    function updateOperations(){
        operations = config.columns.map(column => {
            return {
                filter: '',
                sort: null
            }
        })
    }

    function getHeight(matrix: any[][]): number{
        return Math.max(0, ...matrix.map(row => row.length))
    }

    function scrollToPage(){
        const targetRow = tbodyEl
            ?.rows
            ?.[(pickedPageNumber - 1) * pageSize]

        if(targetRow)
            scrollIntoElement(targetRow)
    }

    function SetupRowsPointing(){
        tbodyEl?.addEventListener("mousemove", (event: MouseEvent) => {
            const pointedRow = (event.target as HTMLElement)?.closest("tr:not(.total)")
            if(pointedRow) {
                pointedRowIndex = Array.from(tbodyEl.rows)
                    .filter(row => row.className !== "total")
                    .indexOf(pointedRow as HTMLTableRowElement)
            }
        })
    }

    let switchGroupingFieldConfig: SwitchConfig,
        childReportsFieldConfig: SelectConfig

    $: if (editable)
        switchGroupingFieldConfig = {
            id: "group",
            type: "switch",
            title: "Группировать",
            default: !!config.group
        }

    $: if (editable && allNickChildren?.length > 0) {
        childReportsFieldConfig = null
        if(allNickChildren?.length > 0) {
            const options = {}
            allNickChildren.forEach(child => {
                options[child.id] = child.title
            })
            childReportsFieldConfig = {
                id: "childReportIds",
                type: "select",
                title: "Дочерние отчёты",
                multiple: true,
                showCodes: true,
                returnType: "key",
                placeholder: "Отсутствуют",
                optionsJson: options
            }
        }
    }

</script>

<thead bind:this={theadEl}>
    <tr class="tool-bar">
        <td colspan={columnTitles.length + (editable ? 1 : 0) + (allNickChildren?.length > 0 ? 1 : 0)}>
            <div>
                {#if editable}
                    <Field config={switchGroupingFieldConfig}
                           bind:value={config.group}/>

                    {#if childReportsFieldConfig}
                        <Field config={childReportsFieldConfig}
                               bind:value={config.childReportIds}/>
                    {/if}
                {/if}
                {#if rowsCount > 0}
                    <PagesBar itemsCount={rowsCount}
                              currentItemNumber={pointedRowIndex + 1}
                              bind:pickedPageNumber
                              bind:pageSize/>
                {/if}
            </div>
        </td>
    </tr>
    {#each Array(getHeight(spannedColumns)) as _, rowI}
        <tr>
            {#if rowI === 0 && config?.childReportIds?.length > 0}
                <th class="checkbox"
                    rowspan={getHeight(spannedColumns)}>
                    <MainCheckbox bind:checkedArray/>
                </th>
            {/if}
            {#each spannedColumns.map(column => column[rowI]) as spannedTitle}
                {#if spannedTitle != null}
                    <th rowspan={spannedTitle.rowspan}
                        colspan={spannedTitle.colspan}
                        title={spannedTitle.title}>
                        {spannedTitle.title}
                    </th>
                {:else}
                    <th style:display="none"/>
                {/if}
            {/each}
            {#if editable}
                <td/>
            {/if}
        </tr>
    {/each}
    <tr class="operations sort">
        {#if config?.childReportIds?.length > 0}
            <th class="checkbox"></th>
        {/if}
        {#each operations as operation}
            <th>
                <Sort bind:value={operation.sort}/>
            </th>
        {/each}
    </tr>
    <tr class="operations filter">
        {#if config?.childReportIds?.length > 0}
            <th class="checkbox"></th>
        {/if}
        {#each operations as operation, colI}
            <th>
                {#if config.columns[colI]?.type === "text"}
                    <Text bind:value={operation.filter}
                          config={{id: String(colI), type: "text", placeholder: "⌕ Поиск"}}/>
                {/if}
            </th>
        {/each}
    </tr>
</thead>