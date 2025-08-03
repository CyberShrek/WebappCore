<script lang="ts">

    import Field from "./Field.svelte"
    import {fade} from "svelte/transition"
    import {FormObservatory} from "./FormObservatory";
    import {onMount} from "svelte";
    import Grid from "../../../common/components/formation/Grid.svelte";
    import ActionMove from "../../../apps/webapp_editor/components/actions/ActionMove.svelte";
    import FieldConfigurator
        from "../../../apps/webapp_editor/components/configurator/templates/form/fields/FieldConfigurator.svelte";

    export let
        config: SectionConfig,
        editable: boolean,
        observatory: FormObservatory,
        statements: {[fieldId: string]: FieldStatementFrame } = {}

    let display = true

    onMount(() => {
        if (!editable && config.formulaCreate) {
            observatory.subscribe(config.formulaCreate, (res) => display = res)
        }
    })

</script>

{#if display}
    <div class="section"
         in:fade>

        <Grid className="fields"
              title={config.title}
              bind:size={config.size}
              {editable}>
            <slot slot="header">
                <slot/>
            </slot>
            {#each config.fields ?? [] as fieldConfig, index}
                <Field bind:config={fieldConfig}
                       bind:statement={statements[fieldConfig.id]}
                       {observatory}
                       {editable}>
                    {#if editable}
                        <ActionMove axis="x"
                                    bind:array={config.fields}
                                    {index}/>
                    {/if}
                    <FieldConfigurator role="editor"
                                       bind:config={fieldConfig}
                                       bind:array={config.fields}
                                       {index}/>
                </Field>
            {/each}
            {#if editable}
                <FieldConfigurator role="creator"
                                   config={{
                                       id: null, type: "select"
                                   }}
                                   bind:array={config.fields}/>
            {/if}
        </Grid>
    </div>
{/if}