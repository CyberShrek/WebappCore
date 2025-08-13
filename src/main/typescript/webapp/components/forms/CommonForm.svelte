<script lang="ts">
    import Form from "../common/form/Form.svelte"
    import Section from "../common/form/Section.svelte";
    import Field from "../common/form/Field.svelte";
    import Calendar from "../common/input/Calendar.svelte";
    import Switch from "../common/input/Switch.svelte";
    import Select from "../common/input/Select.svelte";
    import {onMount} from "svelte";
    import {getOptionsFromSB} from "../../api/serviceBank";
    import {Values} from "./valueTypes";

    export let
        dativeTitle: string,
        values: Values = {},
        submitIsTouched: boolean,
        isWrong: boolean

    let carrierOptions: {[key: string]: string} = {},
        roadOptions:    {[key: string]: string} = {}

    $: carriersIsNotPicked = values.carriers && Object.entries(values.carriers).length === 0
    $: roadsIsNotPicked    = values.roads    && Object.entries(values.roads).length === 0

    $: isWrong = !!(carriersIsNotPicked || roadsIsNotPicked);

    onMount(async () => {
        carrierOptions  = await getOptionsFromSB("perList", "skp", "nazvp", {
            data: "2025-01-01",
            gos: 20,
            prpp: 1,
            skp: -1
        })
        roadOptions     = await getOptionsFromSB("dorList", "d_nom3", "d_name", {
            data: "2025-01-01",
            dor:'*',
            gos: 20
        })
    })

</script>

<Form {values}
      {isWrong}
      bind:submitIsTouched
      on:submit>
    <Section>
        <Field title="Период {dativeTitle}">
            <Calendar range={31}
                      bind:value={values.period}/>
        </Field>
    </Section>
    <Section size={1}>
        <Field>
            <Switch title="Детализировать по дате {dativeTitle}"
                    bind:value={values.periodDetailing}/>
        </Field>

    </Section>
    <Section>
        <Field title="Перевозчики"
               isWrong={submitIsTouched && carriersIsNotPicked}>
            <Select search
                    options={carrierOptions}
                    bind:value={values.carriers}/>
        </Field>
        <Field title="Дорога {dativeTitle}"
               isWrong={submitIsTouched && roadsIsNotPicked}>
            <Select search
                    options={roadOptions}
                    bind:value={values.roads}/>
        </Field>

        <slot name="main section"/>
    </Section>
    <Section size={1}>
        <Field>
            <Switch title="Дополнительные параметры"
                    bind:value={values.additional}/>
        </Field>

    </Section>

    <slot/>
</Form>