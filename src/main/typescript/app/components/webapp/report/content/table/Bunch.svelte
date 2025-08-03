<script lang="ts">

    import MainCheckbox from "./MainCheckbox.svelte"
    import {createEventDispatcher, tick} from "svelte"
    import Tile from "../tiles/Tile.svelte"
    import {TileContent} from "../tiles/TilesColumn"
    import Button from "../../../../../common/components/input/Button.svelte";
    import {ReportSocket} from "../../../../../common/wizard/ReportSocketCalculator";
    import {resolveStyle} from "../../../../../common/util/resolver";

    export let
        config: TableConfig,
        socket: ReportSocket,
        dataTree: ({
            // If it is a row
            clientRow?: SqlCell[]
            serverIndex?: number
            clientIndex?: number
            // If it is a bunch
            dataTree?: typeof dataTree
        })[],
        tilesData: TileContent[][],
        nesting = 0,
        collapsed = false,
        checkedArray: boolean[]

    const dispatch = createEventDispatcher()
    


    let flatData: {
            clientRow: SqlCell[]
            serverIndex: number
            clientIndex: number
        }[],
        flatSize: number,
        totalRow: SqlCell[]

    $: group = config.group
        && nesting > 0
        && dataTree?.length > 1

    $: aggregate = group
        && config.columns?.some(column => column.formulaAggregate != null)

    $: if(config.columns?.find(column => column.tileMode != null))
        resolveStyle("tiles")

    $: if(!aggregate) collapsed = false

    $: if(dataTree) {
        updateFlatData()
        updateTotalRow()
    }

    $: if(collapsed && flatData)
        updateTotalRow()

    function updateFlatData() {
        flatData = []
        flatSize = 0
        iterate()
        function iterate(data = dataTree) {
            data.forEach(item => {
                if(item.dataTree) {
                    iterate(item.dataTree)
                    flatSize++
                }
                else flatData.push(item as typeof flatData[number])
            })
        }
        flatSize += flatData.length
    }

    async function updateTotalRow() {
        if(group){
            totalRow = flatData[0]?.clientRow.map((cell, colI) => colI < nesting ? cell : '...')
        } else {
            totalRow = null
            return
        }
        if(aggregate && collapsed) {
            await tick()
            const calculated = await socket.calculateFormulas(
                config.columns.map(column => column.formulaAggregate ?? ''),
                flatData?.map(item => socket.report?.data?.[item.serverIndex])
            )
            if (calculated == null || typeof calculated === "string")
                totalRow = [calculated as string]
            else
                totalRow = (calculated?.data?.[0] ?? []).map((cell, colI) => {
                    if (colI >= nesting)
                        return cell
                    else
                        return flatData?.[0]?.clientRow?.[colI]
                })
        }
    }

    function dispatchReport(reportId: string, rowI: number) {
        dispatch('report', {
            id: reportId,
            data: [socket?.report?.data?.[rowI]]
        } as ReportCall)
    }

</script>

{#if group && totalRow?.length > 0}
    <tr class="total">
        <!-- Checkbox cell -->
        {#if config?.childReportIds?.length > 0}
            <td class="checkbox"
                class:collapsed={!collapsed}>
                <MainCheckbox clientIndices={flatData?.map(item => item.clientIndex)}
                              bind:checkedArray/>
            </td>
        {/if}
        <!-- Primary cell -->
        <td class="total"
            rowspan={flatSize + 1}
            title={String(totalRow[nesting - 1])}>
            {totalRow[nesting - 1]}
            {#if aggregate}
                <Button text={collapsed ? "▼" : "▲"}
                        hint={collapsed ? "Развернуть" : "Свернуть"}
                        on:click={(event) => {
                            event.stopPropagation()
                            collapsed = !collapsed
                        }}/>
            {/if}
        </td>
        {#each totalRow ?? [] as cell, colI}
            {#if colI >= nesting}
                <td class={config.columns[colI]?.type}
                    class:collapsed={!collapsed}>
                    {#if typeof cell === "boolean"}
                        {cell === true ? "✅" : "❌"}
                    {:else}
                        {cell}
                    {/if}
                </td>
            {/if}
        {/each}
    </tr>
{/if}

{#each dataTree ?? [] as item}
    {#if item.dataTree}
        <svelte:self {config}
                     {socket}
                     {collapsed}
                     dataTree={item.dataTree}
                     {tilesData}
                     nesting={nesting + 1}
                     bind:checkedArray
                     on:report/>
    {:else}
        <tr>
            {#each item.clientRow ?? [] as cell, colI}
                {#if colI === 0 && config?.childReportIds?.length > 0}
                    <!-- Checkbox cell -->
                    <td class="checkbox"
                        class:collapsed={collapsed}>
                        <input type="checkbox"
                               bind:checked={checkedArray[item.clientIndex]}/>
                    </td>
                {/if}

                {#if !group || colI >= nesting}
                    <!-- Other cell -->
                    <td title={String(cell)}
                        class={config.columns[colI]?.type}
                        class:collapsed={collapsed}
                        class:tiled={config.columns[colI]?.tileMode === true}>
                        {#if config.columns[colI]?.tileMode === true}
                            <Tile config={{...config.columns[colI], title: null}}
                                  content={tilesData?.[item.serverIndex]?.[colI]}
                                  on:click={() => dispatchReport(tilesData?.[item.serverIndex]?.[colI]?.childReportId, item.serverIndex)}/>
                        {:else}
                            {#if typeof cell === "boolean"}
                                {cell === true ? "✅" : "❌"}
                            {:else}
                                {cell}
                            {/if}
                        {/if}
                    </td>
                {/if}
            {/each}
        </tr>
    {/if}
{/each}