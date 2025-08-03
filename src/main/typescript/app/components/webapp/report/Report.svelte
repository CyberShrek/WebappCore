<script lang="ts">

    import Content from "./content/Content.svelte";
    import ReportHeader from "./ReportHeader.svelte";
    import {resolveStyle} from "../../../common/util/resolver";
    import {QueryPerformer} from "../../../common/wizard/QueryPerformer";
    import ActionMove from "../../../apps/webapp_editor/components/actions/ActionMove.svelte";
    import ContentConfigurator
        from "../../../apps/webapp_editor/components/configurator/templates/report/content/ContentConfigurator.svelte";

    resolveStyle("report")

    export let
        config: ReportConfig = null,
        formValue: FormValue = null,
        editable             = false,
        loaded               = false

    let reportRetriever: QueryPerformer = editable ? null : new QueryPerformer(config.query),
        mainSet: SimpleSet

    let reportRootElement: HTMLDivElement,
        collapsed  = false,
        fullScreen = false,
        showCharts = false,
        nickChildren: NickReportConfig[]

    $: nickChildren = config?.children?.map(child => {
        return {
            id: child.id,
            title: child.title,
            icon: child.icon
        }
    }) ?? []

    // $: if(!editable && config?.query)
    //     reportRetriever = new QueryPerformer(config.query)

    $: if(!editable && formValue && reportRetriever?.checkForChanges(formValue))
        reportRetriever.retrieve()
            .then(simpleSet => {
                mainSet = simpleSet
                loaded = true
            })

    $: if(editable && config.debugSet)
        mainSet = config.debugSet

</script>

<div class="report"
     class:editable
     bind:this={reportRootElement}>

    <ReportHeader {config}
                  {reportRootElement}
                  {editable}
                  {loaded}
                  bind:collapsed
                  bind:fullScreen/>

    {#if formValue || editable}
        <div class="body">
            {#each config.content ?? [] as content, contentIndex}
                <Content bind:config={content}
                         {formValue}
                         {mainSet}
                         {nickChildren}
                         {editable}
                         on:report={(ev) => console.log(ev.detail)}>

                    {#if editable}
                        <ActionMove axis="y"
                                    index={contentIndex}
                                    bind:array={config.content}/>
                        <ContentConfigurator role="editor"
                                             bind:config={content}
                                             index={contentIndex}
                                             bind:array={config.content}/>
                    {/if}
                </Content>
            {/each}
            {#if editable}
                <ContentConfigurator role="creator"
                                     bind:array={config.content}/>
            {/if}

        </div>
    {/if}
</div>

{#if !editable}
    {#each config?.children ?? [] as child}
        <svelte:self config={child}
                     {formValue}
                     {editable}/>
    {/each}
{/if}

<!--{JSON.stringify(formValue)}-->
