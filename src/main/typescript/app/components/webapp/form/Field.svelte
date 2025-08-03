<script lang="ts">

    import {slide, fade} from "svelte/transition";
    import {OptionsProvider} from "./OptionsProvider";
    import {FormObservatory} from "./FormObservatory";
    import {onMount} from "svelte";
    import FormSubscriber from "./FormSubscriber.svelte";
    import {equal} from "../../../common/util/data";
    import Switch from "../../../common/components/input/Switch.svelte";
    import Calendar from "../../../common/components/input/Calendar.svelte";
    import Select from "../../../common/components/input/Select.svelte";
    import Text from "../../../common/components/input/Text.svelte";
    import Codearea from "../../../common/components/input/Codearea.svelte";
    import Number from "../../../common/components/input/Number.svelte";
    import Color from "../../../common/components/input/Color.svelte";

    export let
        config: SwitchConfig | CalendarConfig | SelectConfig | TextConfig | CodeareaConfig | NumberConfig | ColorConfig,
        observatory: FormObservatory = undefined,
        editable: boolean = false,
        value: any = editable ? null : observatory?.formValue?.[config.id] ?? config.default,
        statement: FieldStatementFrame = null,
        options: OptionsMap = null,
        focused: boolean = false

    let valueUpdateTimeout: NodeJS.Timeout = setTimeout(() => {}),
        valueUpdatePromise: Promise<any>,
        optionsProvider: OptionsProvider

    $: if (statement == null)
        statement = {
            create: editable || !config.formulaCreate,
            isReady: false
        }

    $: if (observatory && !equal(value, observatory.formValue[config.id])) {
        provideValue()
    }

    $: wrong = statement.showWrong === true && statement.correct === false

    async function provideValue() {
        statement.isReady = false
        await valueUpdatePromise
        clearTimeout(valueUpdateTimeout)
        valueUpdateTimeout = setTimeout(() => {
            valueUpdatePromise = observatory.setField(config.id, statement.create ? value : null)
            statement.isReady = true
        }, 100)
    }

    async function handleFormChanges (newForm: FormValue) {
        if (statement.create) try {
            statement.isReady = false
            clearTimeout(valueUpdateTimeout)
            if (config.type === "select")
                options = optionsProvider ? await optionsProvider.retrieve(newForm) : new Map()

            const outerValue = newForm[config.id]
            if (!focused && !equal(value, outerValue)) {
                value = outerValue
            }
        }
        catch (_) {
            statement = {
                correct: false,
                create: false
            }
        }
        finally {
            statement.isReady = true
        }
    }

    async function installOptionsProvider() {
        if (config.type !== "select")
            return

        const optionsSources: OptionsSources = {}
        if (config.optionsSql?.length > 0) optionsSources.optionsSql = config.optionsSql
        if (config.optionsJson && Object.keys(config.optionsJson)?.length > 0) optionsSources.optionsJson = config.optionsJson
        if (config.serviceBankListName?.length > 0) {
            optionsSources.serviceBankListName = config.serviceBankListName
            optionsSources.serviceBankParams = config.serviceBankParams
            optionsSources.serviceBankResultKey = config.serviceBankResultKey
            optionsSources.serviceBankResultValue = config.serviceBankResultValue
        }
        if (Object.keys(optionsSources).length > 0){
            if (optionsProvider == null || !equal(optionsSources, optionsProvider.sources))
                optionsProvider = new OptionsProvider(optionsSources)
        }
        options = optionsProvider ? await optionsProvider.retrieve(observatory.formValue) : new Map()
    }
 
    onMount(() => {
        if (editable) return

        if (config.type === "select")
            installOptionsProvider()

        statement.isReady = true
    })

</script>

<FormSubscriber {observatory}
                formulaCreate={config.formulaCreate}
                formulaCorrect={config.formulaCorrect}
                formulaMessage={config.formulaMessage}
                bind:create={statement.create}
                bind:correct={statement.correct}
                bind:message={statement.message}
                onCreateChange={provideValue}
                onAnyChange={handleFormChanges}>

    <div class="field"
         title={config.title ? undefined : config.info}
         transition:fade>
        <div class="{config.type}-field field"
             class:wrong>
            {#if config.type !== "switch" && config.title || config.id && editable}
                <div class="header">
                    <p>
                        {config.type !== "switch" ? config.title ?? '' : ''}
                        {#if editable}
                            <span>[{config.id}]</span>
                        {/if}
                    </p>
                    {#if config.info}
                        <span title="{config.info}">
                            🛈
                        </span>
                    {/if}
                </div>
            {/if}

            {#if      config.type === "switch"  }
                <Switch {config}
                        bind:value/>
            {:else if config.type === "calendar"}
                <Calendar {config}
                          bind:value/>
            {:else if config.type === "select" && (options != null || editable)}
                <Select {config}
                        {options}
                        bind:value/>
            {:else if config.type === "text"  }
                <Text {config}
                      bind:value
                      bind:focused/>
            {:else if config.type === "codearea"}
                <Codearea {config}
                          bind:value/>
            {:else if config.type === "number"  }
                <Number {config}
                       bind:value/>
            {:else if config.type === "color"  }
                <Color bind:value/>
            {/if}
        </div>

        {#if editable}
            <div class="actions">
                <slot/>
            </div>
        {/if}
        {#if statement.message}
                <span class="message"
                      class:wrong
                      transition:slide>
                    {statement.message}
                </span>
        {/if}
        <!--{JSON.stringify(value)}-->
        <!--{JSON.stringify(statementRetriever?.formulas ?? {})}-->
        <!--{JSON.stringify(optionsRetriever?.sources ?? {})}-->
        <!--{JSON.stringify(options ?? {})}-->
        <!--{JSON.stringify(statement)}-->
        <!--{JSON.stringify(config)}-->
    </div>
</FormSubscriber>