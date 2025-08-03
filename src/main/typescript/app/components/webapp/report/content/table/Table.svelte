<script lang="ts">

    import Head from "./Head.svelte"
    import Body from "./Body.svelte"
    import {createEventDispatcher} from "svelte";
    import ActionMove from "../../../../../apps/webapp_editor/components/actions/ActionMove.svelte";
    import TableColumnConfigurator
        from "../../../../../apps/webapp_editor/components/configurator/templates/report/content/TableColumnConfigurator.svelte";
    import {ReportSocket} from "../../../../../common/wizard/ReportSocketCalculator";

    const dispatch = createEventDispatcher()

    export let
        config:         TableConfig,
        socket:         ReportSocket,
        allNickChildren:   NickReportConfig[],
        loaded   = false,
        editable = false

    // HEAD
    let operations: {
        filter: string
        sort:   "asc" | "desc"
    }[]

    // BODY
    let retrievedMessage: string,
        retrievedData: {
        clientRow:   SqlCell[]
        serverIndex: number
    }[],
        data:         typeof retrievedData,
        checkedArray: boolean[]

    // FOOT
    let totalRow: SqlCell[],
        nickChildren: NickReportConfig[]

    $: nickChildren = allNickChildren?.filter(child => config?.childReportIds?.includes(child.id))
    $: hasChildren = nickChildren?.length > 0
    $: hasCheckeds = checkedArray?.some(checked => checked === true)

    $: if(config.columns == null)
        config.columns = []

    $: if(socket) {
        retrieveData()
    }

    $: if(retrievedData && operations){
        updateData()
        updateTotalRow()
    }

    async function retrieveData() {
        if(config?.columns == null) return
        const calculated = await socket.calculateFormulas(config.columns.map(column => column.formula ?? ''))
        retrievedData  = []
        retrievedMessage = null
        if(calculated == null || typeof calculated === "string")
            retrievedMessage = calculated as string
        else calculated.data.forEach((row, rowI) => {
            retrievedData.push({
                clientRow: row,
                serverIndex: rowI
            })
        })
    }
    async function updateData() {
        data = retrievedData.filter(item => {
            return operations.every((operation, colI) => {
                const itemValue = item.clientRow?.[colI]?.toString(),
                    filterValue = operation.filter

                return filterValue == null || filterValue === "" || itemValue.includes(filterValue);
            })
        })
        operations.forEach((operation, colI) => {
            if(operation.sort == null) return
            data = data.sort((a, b) => {
                const sortedBeforeColI = operations.indexOf(operations.slice(0, colI)
                    ?.findLast(operation => operation.sort != null))

                if(sortedBeforeColI != null && a.clientRow?.[sortedBeforeColI] !== b.clientRow?.[sortedBeforeColI])
                    return

                const
                    type = config.columns[colI].type,
                    aValue = a.clientRow?.[colI],
                    bValue = b.clientRow?.[colI]

                return operation.sort === "asc"
                    ? (type === "number" ? (Number(aValue) - Number(bValue)) : String(aValue).localeCompare(String(bValue)))
                    : (type === "number" ? (Number(bValue) - Number(aValue)) : String(bValue).localeCompare(String(aValue)));
            })
        })
        loaded = true
    }
    async function updateTotalRow() {
        totalRow = config?.columns?.map(_ => "...")
        if (data?.length > 0 && config?.columns?.some(column => column.formulaAggregate != null)) {
            const simpleSet = await socket.calculateFormulas(
                config.columns.map(column => column.formulaAggregate ?? ''),
                data?.map(item => socket.report?.data?.[item.serverIndex]))
            if(typeof simpleSet === "string")
                totalRow = [simpleSet]
            else
                totalRow = simpleSet.data[0]
        } else
            totalRow = null
    }

    function dispatchReport(reportId: string) {
        dispatch('report', {
            id: reportId,
            data: socket?.report?.data?.filter((_, index) => checkedArray[index])
        } as ReportCall)
    }

</script>

<table class:editable>
    <Head {allNickChildren}
          {editable}
          rowsCount={retrievedData?.length}
          bind:config
          bind:operations
          bind:checkedArray>
    </Head>

    <tfoot>
        {#if hasChildren}
            <tr class="children-buttons">
                <td colspan={config.columns.length + 1}
                    class:collapsed={!hasCheckeds}>
                    <div>
                        {#each nickChildren ?? [] as child}
                            <button title={child.title}>
                                {#if child.icon}
                                    <img src={child.icon} alt={child.title}/>
                                {:else}
                                    {child.title}
                                {/if}
                            </button>
                        {/each}
                    </div>
                </td>
            </tr>
        {/if}
        {#if totalRow?.length > 0}
            <tr class="total">
                {#if hasChildren}
                    <td class="checkbox"></td>
                {/if}
                {#each totalRow as cell, cellI}
                    <td class={"total " + config.columns[cellI]?.type}>
                        {cell}
                    </td>
                {/each}
            </tr>
        {/if}
        {#if editable}
            <tr class="operations">
                {#if hasChildren}
                    <td class="checkbox"></td>
                {/if}
                {#each config.columns ?? [] as columnConfig, columnIndex}
                    <td>
                        <div class="actions">
                            <ActionMove axis="x"
                                        index={columnIndex}
                                        bind:array={config.columns}/>
                            <TableColumnConfigurator role="editor"
                                                     bind:config={columnConfig}
                                                     index={columnIndex}
                                                     bind:array={config.columns}/>
                        </div>
                    </td>
                {/each}
                <td>
                    <TableColumnConfigurator role="creator"
                                             bind:array={config.columns}/>
                </td>
            </tr>
        {/if}
    </tfoot>

    {#if retrievedMessage}
        <span>{retrievedMessage}</span>
    {/if}

    <Body {config}
          {socket}
          {data}
          bind:checkedArray
          on:report/>
</table>