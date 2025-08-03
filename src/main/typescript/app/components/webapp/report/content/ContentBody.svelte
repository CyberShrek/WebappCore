<script lang="ts">

    import Charts from "./charts/Charts.svelte";
    import Tiles from "./tiles/Tiles.svelte";
    import Table from "./table/Table.svelte";
    import {ReportSocket} from "../../../../common/wizard/ReportSocketCalculator";

    export let
        config: TableConfig | ChartsBlockConfig | TilesBlockConfig,
        socket: ReportSocket,
        nickChildren: NickReportConfig[],
        editable = false
</script>

{#if config.type       === "table"}
    <Table bind:config
           {socket}
           {editable}
           allNickChildren={nickChildren}
           on:report/>
{:else if (config.type === "charts")}
    <Charts bind:config
            {socket}
            {editable}/>
{:else if (config.type === "tiles")}
    <Tiles bind:config
           {socket}
           {editable}
           on:report/>
{/if}