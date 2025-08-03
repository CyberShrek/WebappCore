<script lang="ts">

    import Tile from "./Tile.svelte"
    import {TileContent, TilesColumn} from "./TilesColumn"
    import {createEventDispatcher} from "svelte"
    import {ReportSocket} from "../../../../../common/wizard/ReportSocketCalculator";
    import {resolveStyle} from "../../../../../common/util/resolver";
    import Grid from "../../../../../common/components/formation/Grid.svelte";
    import ActionMove from "../../../../../apps/webapp_editor/components/actions/ActionMove.svelte";
    import TileConfigurator
        from "../../../../../apps/webapp_editor/components/configurator/templates/report/content/TileConfigurator.svelte";

    export let
        config: TilesBlockConfig,
        socket: ReportSocket,
        editable = false

    const dispatch = createEventDispatcher()

    let content: TileContent[] = []

    $: if(config && config.tiles == null)
        config.tiles = []

    $: if(config && socket)
        updateContent()

    async function updateContent() {
        content = []
        for (const tile of config.tiles ?? [])
            content.push((await new TilesColumn(tile, socket)?.content)?.[0])

        content = content
    }

    function dispatchReport(reportId: string) {
        dispatch('report', {
            id: reportId,
            data: socket?.report?.data
        } as ReportCall)
    }

    resolveStyle("tiles")

</script>

{#if config}
    <Grid className="tiles"
          bind:size={config.size}
          editable={editable}>
        <slot slot="header">
            <slot/>
        </slot>
        {#each config.tiles as tile, index}
            <Tile config={tile}
                  content={content?.[index]}
                  on:click={() => dispatchReport(content?.[index]?.childReportId)}>
                {#if editable}
                    <div class="actions">
                        <ActionMove axis="x"
                                    index={index}
                                    bind:array={config.tiles}/>
                        <TileConfigurator role="editor"
                                          bind:config={tile}
                                          {index}
                                          bind:array={config.tiles}/>
                    </div>
                {/if}
            </Tile>
        {/each}
        {#if editable}
            <TileConfigurator role="creator"
                              bind:array={config.tiles}/>
        {/if}
    </Grid>
{/if}