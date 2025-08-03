<script lang="ts">

    import Bunch from "./Bunch.svelte";
    import {onMount} from "svelte";
    import {TileContent, TilesColumn} from "../tiles/TilesColumn";

    export let
        config: TableConfig,
        socket: ReportSocket,
        data: {
            clientRow: SqlCell[]
            serverIndex: number
            clientIndex?: number
        }[],
        // Provides
        scrolledRowIndex: number = 0,
        checkedArray: boolean[] = []

    $: if(data) {
        setupDataClientIndices()
        updateDataTree()
        updateTilesData()
        updateCheckedArray()
    }

    let collapsed = false,
        tBodyElement: HTMLTableSectionElement,
        dataTree: ({
            clientRow: SqlCell[]
            serverIndex: number
            clientIndex?: number
        } | {
            dataTree: typeof dataTree
        })[] = [],
        tilesData: TileContent[][] = []

    function updateDataTree() {
        let maxNesting = determineMaxNesting()
        dataTree = getDataTree()
        function getDataTree(nesting = 0,
                      nestedData = data): typeof dataTree {
            const nestedDataTree: typeof dataTree = [],
                nestedDataSplit: (typeof nestedData)[] = []

            nestedData?.forEach(item => {
                const lastNestedData = nestedDataSplit[nestedDataSplit.length - 1]
                if(lastNestedData != null && lastNestedData[0]?.clientRow?.[nesting] === item.clientRow?.[nesting])
                    lastNestedData.push(item)
                else
                    nestedDataSplit.push([item])
            })
            nestedDataSplit.forEach(nestedData => {
                if(nestedData.length > 1 && nesting <= maxNesting)
                    nestedDataTree.push({dataTree: getDataTree(nesting + 1, nestedData)})
                else
                    nestedDataTree.push(...nestedData)
            })
            return nestedDataTree
        }
    }

    async function updateTilesData() {
        tilesData = []
        for (const columnConfig of config.columns) {
            const columnIndex = config.columns.indexOf(columnConfig);
            (await new TilesColumn(columnConfig, socket)?.content)?.forEach(
                (content, rowIndex) => {
                    if(tilesData[rowIndex] == null) tilesData[rowIndex] = []
                    tilesData[rowIndex][columnIndex] = content
                })
        }
        tilesData = tilesData
    }

    function updateCheckedArray() {
        checkedArray = data.map(() => false)
    }

    function setupDataClientIndices() {
        data = data?.map((item, index) => {return {...item, clientIndex: index}})
    }

    function determineMaxNesting(): number {
        let maxNesting = 0
        for (const column of config?.columns ?? []) {
            if (column.type === "text")
                maxNesting++
            else
                break
        }
        return maxNesting
    }

    onMount(() => document.body.addEventListener("scroll", () => scrolledRowIndex = tBodyElement?.scrollTop))

</script>

<tbody bind:this={tBodyElement}>
    <Bunch {config}
           {socket}
           {dataTree}
           {tilesData}
           {collapsed}
           bind:checkedArray
           on:report/>
</tbody>