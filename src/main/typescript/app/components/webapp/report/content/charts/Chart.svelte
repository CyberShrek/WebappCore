<script lang="ts">

    import {onDestroy} from "svelte";
    import {ReportSocket} from "../../../../../common/wizard/ReportSocketCalculator";
    import {ChartJsModule} from "../../../../third-party/ChartJsModule";
    import ActionMove from "../../../../../apps/webapp_editor/components/actions/ActionMove.svelte";
    import ChartViewConfigurator
        from "../../../../../apps/webapp_editor/components/configurator/templates/report/content/ChartViewConfigurator.svelte";

    export let
        config: ChartConfig,
        socket: ReportSocket,
        editable = false

    let rootCanvas: HTMLCanvasElement,
        chartJS: ChartJsModule,
        data: SimpleSet,
        message: string

    $: if(socket && config && rootCanvas) {
        updateChart()
    }

    async function updateChart() {
        message = ''
        data = {
            columnNames: [],
            data: []
        }

        if (socket) {
            if (config.formulaLabels) {
                const result = await socket.calculateFormulas([config.formulaLabels])
                if (typeof result === "string") message += result
                else data.columnNames = result.data?.map(val => val?.[0]?.toString())
            }

            if (config.views?.find(view => view.formulaData)) {
                const result = await socket.calculateFormulas(config.views.map(view => view.formulaData))
                console.log("result", result)
                if (typeof result === "string") message += result
                else {
                    result.data?.forEach((row, rowI) => {
                        row.forEach((col, colI) => {
                            data.data[colI] ??= []
                            data.data[colI][rowI] = col
                        })
                    })
                }
            }
        }

        if (chartJS)
            chartJS.update(data, config)
        else
            chartJS = new ChartJsModule(data, config, rootCanvas)
    }

    onDestroy(() => chartJS?.destroy())

</script>

{#if config}
    <div class="chart">
        <!--{#await chartPromise}-->
        <!--    <Loading/>-->
        <!--{:then _}-->
        {#if message?.length > 0}
            {message}
        {/if}
        <canvas bind:this={rootCanvas}/>
        <!--{/await}-->

        {#if editable}
            <div class="graph-views">
                {#each config.views ?? [] as graph, index}
                    <div class="graph-view">
                        <ActionMove axis="x"
                                    index={index}
                                    bind:array={config.views}/>
                        <span style:background={graph.color}>
                            {graph.label}
                        </span>

                        <ChartViewConfigurator role="editor"
                                               bind:config={graph}
                                               {index}
                                               bind:array={config.views}
                                               on:apply={updateChart}/>
                    </div>
                {/each}
            </div>
        {/if}
        <slot/>
    </div>
{/if}