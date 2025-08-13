<script lang="ts">

    import Tabs from "./common/misc/Tabs.svelte"
    import WebappTemplate from "./common/WebappTemplate.svelte"
    import SalesForm from "./forms/SalesForm.svelte"
    import DepsForm from "./forms/DepsForm.svelte"
    import SalesReport from "./reports/SalesReport.svelte";
    import DepsReport from "./reports/DepsReport.svelte";

    let tabs =
            [
                "Продажи",
                "Отправления"
            ],
        pickedTab: typeof tabs[number] = tabs[0]

    let salesFormValues: {[fieldId: string]: any},
        depsFormValues: {[fieldId: string]: any}

    function loadSalesReport(event: CustomEvent){
        salesFormValues = event.detail.formValues
    }
    function loadDepsReport(event: CustomEvent){
        depsFormValues = event.detail.formValues
    }

</script>

<WebappTemplate>
    <Tabs {tabs}
          bind:pickedTab>
        {#if pickedTab === tabs[0]}
            <SalesForm on:submit={loadSalesReport}/>
        {:else if pickedTab === tabs[1]}
            <DepsForm on:submit={loadDepsReport}/>
        {/if}
    </Tabs>
    {#if pickedTab === tabs[0]}
        <SalesReport formValues={salesFormValues}/>
    {:else if pickedTab === tabs[1]}
        <DepsReport formValues={depsFormValues}/>
    {/if}
</WebappTemplate>