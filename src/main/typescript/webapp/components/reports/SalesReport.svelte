<script lang="ts">
    import Report from "../common/report/Report.svelte";
    import ContentBlock from "../common/report/content/ContentBlock.svelte";
    import Table from "../common/report/content/table/Table.svelte";
    import {SalesValues} from "../forms/valueTypes";
    import {getReportData} from "../../api/report";

    export let formValues: SalesValues

    let reportData: (string | number | boolean | null)[][] = [],
        isLoaded: boolean

    const basisColumnHeaders = ["Количество человек или перевозок", "Доходы", "Недополученные доходы", "Сумма сбора", "Доходы за провоз ручной клади"],
          basisColumnTypes: ("number" | "string" | "boolean")[] = ["number", "number", "number", "number", "number"]

    let columnHeaders = basisColumnHeaders,
        columnTypes   = basisColumnTypes

    $: if (formValues) {
        loadReportData()
        columnHeaders = basisColumnHeaders
        columnTypes   = basisColumnTypes

        if (formValues.additional) {
            if (Object.keys(formValues.operationTypes).length > 0) {
                columnHeaders = ["Вид операции", ...columnHeaders]
                columnTypes   = ["string", ...columnTypes]
            }
            if (Object.keys(formValues.shippingTicketTypes).length > 0) {
                columnHeaders = ["Вид перевозочного документа", ...columnHeaders]
                columnTypes   = ["string", ...columnTypes]
            }
            if (Object.keys(formValues.travelTicketTypes).length > 0) {
                columnHeaders = ["Вид проездного документа", ...columnHeaders]
                columnTypes   = ["string", ...columnTypes]
            }
            if (Object.keys(formValues.paymentTypes).length > 0) {
                columnHeaders = ["Вид расчёта", ...columnHeaders]
                columnTypes   = ["string", ...columnTypes]
            }
        }

        if (formValues.periodDetailing) {
            columnHeaders = ["Дата продажи", ...columnHeaders]
            columnTypes   = ["string", ...columnTypes]
        }
    }

    function loadReportData() {
        isLoaded = false
        reportData = []
        getReportData("sales", formValues)
            .then(data => {
                reportData = data
            })
            .finally(() => isLoaded = true)
    }

</script>

<Report title="Отчёт по итогам продаж по пригороду"
        {isLoaded}>

    {#if formValues}
        <ContentBlock>
            <Table matrixData={reportData}
                   {columnHeaders}
                   {columnTypes}/>
        </ContentBlock>
    {/if}
</Report>