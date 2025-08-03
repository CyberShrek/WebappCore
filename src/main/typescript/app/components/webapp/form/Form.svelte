<script lang="ts">
    import Section from "./Section.svelte"
    import {createEventDispatcher, onMount, tick} from "svelte"
    import {fade} from "svelte/transition"
    import {FormObservatory} from "./FormObservatory";
    import Report from "../report/Report.svelte"
    import {resolveStyle} from "../../../common/util/resolver";
    import Loading from "../../../common/components/formation/Loading.svelte";
    import ActionMove from "../../../apps/webapp_editor/components/actions/ActionMove.svelte";
    import SectionConfigurator
        from "../../../apps/webapp_editor/components/configurator/templates/form/SectionConfigurator.svelte";
    import Button from "../../../common/components/input/Button.svelte";

    const dispatch = createEventDispatcher(),
        SUBMIT_EVENT = "submit"

    export let
        config: FormConfig,
        value: FormValue = {},
        allowReset  = true,
        editable = false,
        wrong    = false,
        ready    = false,
        observatory: FormObservatory = editable ? null : new FormObservatory(value, newValue => {
            value = newValue
        })

    let statements: {[fieldId: string]: FieldStatementFrame } = {},
        showDisabledSubmits  = false

    $: {
        wrong = Object.values(statements).some(statement => statement?.correct === false)
        ready = Object.values(statements).every(statement => statement?.isReady === true)
        Object.values(statements).forEach(statement => statement.showWrong = showDisabledSubmits)
        statements = statements
    }

    $: fields = config?.sections?.map(section => section?.fields).flat()

    $: reports = config?.submits?.map(output => output?.reports).flat()
    $: reportsToShow = value ? reports?.map(_ => false) ?? [] : []

    $: collisionedFieldIds = new Set(fields?.map(field => field?.id)
        .filter((value, index, self) => self.indexOf(value) !== index))

    async function handleSubmit(index: number){
        reportsToShow[index] = true
        dispatch(SUBMIT_EVENT, {value, index})
    }

    function reset(){
        const newValue: FormValue = {}
        fields?.forEach(field => {
            if (field?.default !== undefined)
                newValue[field.id] = field.default
        })
        observatory?.update(newValue)
        reportsToShow = reports?.map(_ => null) ?? []
    }

    onMount(async () => {
        if (editable) return
        await tick()
        setTimeout(() => {
            observatory?.forceUpdate()
        }, 300)
        // reset()
    })

    // $: console.log(value)

</script>

{#await resolveStyle("form")}
    <Loading/>
{:then _}
    <form in:fade={{duration: 200}}
          class:editable>
        {#if config.sections}
            {#each config.sections as sectionConfig, sectionIndex}
                <Section bind:config={sectionConfig}
                         bind:statements
                         {observatory}
                         {editable}>
                    {#if editable}
                        <ActionMove axis="y"
                                    bind:array={config.sections}
                                    index={sectionIndex}/>
                        <SectionConfigurator bind:config={sectionConfig}
                                             role="editor"
                                             bind:array={config.sections}
                                             index={sectionIndex}/>
                    {/if}
                </Section>
            {/each}
        {/if}

        {#if collisionedFieldIds.size > 0}
            <span class="wrong">
                Обнаружены повторяющиеся идентификаторы полей: {JSON.stringify(Array.from(collisionedFieldIds))}
            </span>
        {/if}
        {#if editable}
            <SectionConfigurator role="creator"
                                 bind:array={config.sections}/>
        {/if}

        {#if !editable && config.submits}
            <div class="buttons">
                {#if allowReset}
                    <Button cancel
                            text="Сброс"
                            on:click={reset}/>
                {/if}

                {#each config.submits as submit, index}
                    <Button submit
                            text={submit.text ? submit.text : "Подтвердить"}
                            disabled={showDisabledSubmits && (!ready || wrong)}
                            on:click={() => handleSubmit(index)}
                            on:mouseenter={() => showDisabledSubmits = true}/>
                {/each}
            </div>
        {/if}
    </form>
    <!--{JSON.stringify(value)}-->
{/await}

{#if !editable}
    {#each reports ?? [] as report, index}
        {#if report}
            <Report config={report}
                    formValue={reportsToShow[index] ? value : null}/>
        {/if}
    {/each}
{/if}