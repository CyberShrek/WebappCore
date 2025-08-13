<script lang="ts">
    import Report from "../common/report/Report.svelte";
    import ContentBlock from "../common/report/content/ContentBlock.svelte";
    import Table from "../common/report/content/table/Table.svelte";
    import {DepsValues} from "../forms/valueTypes";
    import {getReportData} from "../../api/report";

    export let formValues: DepsValues

    let reportData: (string | number | boolean | null)[][] = [],
        isLoaded: boolean

    const basisColumnHeaders = ["Количество пассажиров", "Средняя дальность", "Пассажиро-км"],
        basisColumnTypes: ("number" | "string" | "boolean")[] = ["number", "number", "number"]

    let columnHeaders = basisColumnHeaders,
        columnTypes   = basisColumnTypes

    $: if (formValues) {
        loadReportData()
        columnHeaders = basisColumnHeaders
        columnTypes   = basisColumnTypes

        if (formValues.additional && Object.keys(formValues.trainCategories).length > 0) {
            columnHeaders = ["Категория поезда", ...columnHeaders]
            columnTypes   = ["string", ...columnTypes]
        }

        if (formValues.periodDetailing) {
            columnHeaders = ["Дата продажи", ...columnHeaders]
            columnTypes   = ["string", ...columnTypes]
        }
    }

    function loadReportData() {
        isLoaded = false
        reportData = []
        getReportData("deps", formValues)
            .then(data => {
                reportData = data
            })
            .finally(() => isLoaded = true)
    }

</script>

<Report title="Отчёт по итогам отправлений по пригороду"
        {isLoaded}>
    {#if formValues}
        <ContentBlock>
            <Table matrixData={reportData}
                   {columnHeaders}
                   {columnTypes}/>
        </ContentBlock>
    {/if}
</Report>