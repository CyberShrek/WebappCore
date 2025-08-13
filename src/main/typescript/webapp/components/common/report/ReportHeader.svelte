<script lang="ts">

    import {exportAsJpeg, getFullscreenElement, toggleFullscreen} from "../../../util/dom"
    import {popupRadioAction} from "../../../util/alert"
    import {ExportableReport} from "../../../model/export/ExportableReport"
    import {TableModel} from "../../../model/export/TableModel"
    import {downloadReport} from "../../../api/report"
    import ToTopButton from "../navigation/ToTopButton.svelte"
    import Button from "../input/Button.svelte"
    import Loading from "../misc/Loading.svelte";

    export let
        reportRootElement: HTMLDivElement,
        title: string,
        modal = false,
        allowTablesExport = false,
        allowChartsExport = false,
        isCollapsed  = false,
        isFullScreen = false,
        isLoaded     = null

    window.addEventListener("fullscreenchange", () => {
        isFullScreen = getFullscreenElement() === reportRootElement
    })

    function tryExport() {
        type option = "tables" | "charts"
        const radios: { [key in option]?: string } = {}
        if(allowTablesExport)
            radios.tables = "Таблицы"
        if(allowChartsExport)
            radios.charts = "Графики"

        if(Object.keys(radios).length > 0)
            popupRadioAction("Экспорт отчёта", "Выберите вариант", radios, "Получить", (value: option) => {
                switch (value) {
                    case "tables": exportTables(); break
                    case "charts": exportCharts()
                }
            })
    }

    function exportTables() {
        reportRootElement?.querySelectorAll(".body .content table")?.forEach((tableBlock: HTMLTableElement, i) => {
            const report = new ExportableReport(title)
            report.addTable(new TableModel("Таблица", tableBlock))
            downloadReport(report.toJson())
        })
    }
    function exportCharts() {
        reportRootElement?.querySelectorAll(".body .content .charts")?.forEach((chartsBlock, i) => {
            exportAsJpeg(chartsBlock.closest(".content"), "Графики")
        })
    }

</script>

<div class="header">
    <h2>
        {title}
    </h2>
    {#if isLoaded === false}
        <Loading/>
    {:else if isLoaded === true}
        {#if !modal}
            <ToTopButton/>
        {/if}
        <!--{#if report.hasCharts && report.hasTables}-->
        <!--    <Button image="graph.svg"-->
        <!--            disabled={collapsed}-->
        <!--            hint="Графическое представление"-->
        <!--            on:click={() => showCharts = !showCharts}/>-->
        <!--{/if}-->
        <Button image="download.svg"
                hint="Экспортировать"
                on:click={tryExport}/>
        <!--{#if !modal}-->
        <!--    <Button image="collapse.svg"-->
        <!--            hint={isCollapsed ? "Развернуть" : "Свернуть"}-->
        <!--            on:click={() => isCollapsed = !isCollapsed}/>-->
        <!--{/if}-->
        <Button image={isFullScreen ? "restore.svg" : "expand.svg"}
                hint={isFullScreen ? "Нормальный вид" : "На весь экран"}
                on:click={() => toggleFullscreen(reportRootElement)}/>
        {#if modal}
            <Button image="close.svg"
                    hint="Закрыть"
                    on:click={() => window.dispatchEvent(new KeyboardEvent("keydown",{ key: "Escape" }))}/>
        {/if}
    {/if}
</div>