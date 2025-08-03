<script lang="ts">

    import {exportAsJpeg, getFullscreenElement, toggleFullscreen} from "../../../common/util/dom/common";
    import {popupRadioAction} from "../../../common/util/alert";
    import {ExportableReport} from "../../model/export/ExportableReport";
    import {TableModel} from "../../model/export/TableModel";
    import {downloadReport} from "../../api/crud";
    import ToTopButton from "../../../common/components/misc/ToTopButton.svelte";
    import Button from "../../../common/components/input/Button.svelte";

    export let
        config: ReportConfig,
        reportRootElement: HTMLDivElement,
        collapsed  = false,
        fullScreen = false,
        loaded     = false,
        editable   = false

    window.addEventListener("fullscreenchange", () => {
        fullScreen = getFullscreenElement() === reportRootElement
    })

    function tryExport() {
        type option = "tables" | "charts"
        const radios: { [key in option]?: string } = {}
        if(config.content.find(content => !content.modal && content.type === "table"))
            radios.tables = "Таблицы"
        if(config.content.find(content => !content.modal && content.type === "charts"))
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
        const titles: string[] = getTitles("table")
        reportRootElement?.querySelectorAll(".body .content table")?.forEach((tableBlock: HTMLTableElement, i) => {
            const report = new ExportableReport(config.title)
            report.addTable(new TableModel(titles[i] ?? "Таблица", tableBlock))
            downloadReport(report.toJson())
        })
    }
    function exportCharts() {
        const titles: string[] = getTitles("charts")
        reportRootElement?.querySelectorAll(".body .content .charts")?.forEach((chartsBlock, i) => {
            exportAsJpeg(chartsBlock.closest(".content"), titles[i] ?? "Графики")
        })
    }
    function getTitles(type: typeof config.content[number]["type"]) {
        return config.content.filter(item => item.type === type).map(item => item.title)
    }

</script>

<div class="header">
    {#if config.icon}
        <img src={config.icon} alt="⛝"/>
    {/if}
    <h2>
        {config.title ?? "Отчёт"}
    </h2>
    {#if loaded}
        {#if !config.isModal}
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
        {#if !config.isModal}
            <Button image="collapse.svg"
                    hint={collapsed ? "Развернуть" : "Свернуть"}
                    on:click={() => collapsed = !collapsed}/>
        {/if}
        <Button image={fullScreen ? "restore.svg" : "expand.svg"}
                hint={fullScreen ? "Нормальный вид" : "На весь экран"}
                on:click={() => toggleFullscreen(reportRootElement)}/>
        {#if config.isModal}
            <Button image="close.svg"
                    hint="Закрыть"
                    on:click={() => window.dispatchEvent(new KeyboardEvent("keydown",{ key: "Escape" }))}/>
        {/if}
    {/if}
</div>