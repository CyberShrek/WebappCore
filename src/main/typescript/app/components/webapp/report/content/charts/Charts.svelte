<script lang="ts">

    import Chart from "./Chart.svelte";
    import {resolveStyle} from "../../../../../common/util/resolver";
    import Loading from "../../../../../common/components/formation/Loading.svelte";
    import Grid from "../../../../../common/components/formation/Grid.svelte";
    import ActionMove from "../../../../../apps/webapp_editor/components/actions/ActionMove.svelte";
    import ChartViewConfigurator
        from "../../../../../apps/webapp_editor/components/configurator/templates/report/content/ChartViewConfigurator.svelte";
    import ChartConfigurator
        from "../../../../../apps/webapp_editor/components/configurator/templates/report/content/ChartConfigurator.svelte";
    import {ReportSocket} from "../../../../../common/wizard/ReportSocketCalculator";

    export let
        config: ChartsBlockConfig,
        socket: ReportSocket,
        editable = false

    $: if(config && config.charts == null) {
        config.charts = []
    }

    $: if(config && socket) {
        updateCharts()
    }

    async function updateCharts() {
        // socket.
    }

</script>

{#await resolveStyle("charts")}
    <Loading/>
{:then _}
    {#if config}
        <Grid className="charts"
              {editable}
              maxSize={4}
              bind:size={config.size}>
            <slot slot="header">
                <slot/>
            </slot>
            {#each config.charts ?? [] as chart, index}
                <Chart bind:config={chart}
                       {socket}
                       {editable}>
                    {#if editable}
                        <div class="actions">
                            <ActionMove axis="x"
                                        index={index}
                                        bind:array={config.charts}/>
                            <ChartViewConfigurator role="creator"
                                                   bind:array={chart.views}/>
                            <ChartConfigurator role="editor"
                                               bind:config={chart}
                                               bind:array={config.charts}
                                               {index}/>
                        </div>
                    {/if}
                </Chart>
            {/each}
            {#if editable}
                <ChartConfigurator role="creator"
                                   bind:array={config.charts}/>
            {/if}
        </Grid>
    {/if}
{/await}