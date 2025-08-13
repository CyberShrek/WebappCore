<script lang="ts">
    import Section from "../common/form/Section.svelte"
    import CommonForm from "./CommonForm.svelte"
    import {DepsValues} from "./valueTypes"
    import Field from "../common/form/Field.svelte"
    import Select from "../common/input/Select.svelte"

    let values: DepsValues,
        submitIsTouched: boolean,
        isWrong: boolean

    let trainCategoryOptions = { 7: "Скорый пригородный поезд без предоставления мест", "О": "Пригородный пассажирский поезд", "С": "Скорый пригородный поезд типа Спутник", "А": "Рельсовый автобус 6000-ой нумерации", "Б": "Рельсовый автобус 7000-ой нумерации", "Г": "Городские линии", "Л": "Скоростной пригородный поезд" }

</script>

<CommonForm
        dativeTitle="отправления"
        bind:values
        bind:isWrong
        bind:submitIsTouched
        on:submit>
    {#if values.additional}
        <Section>
            <Field title="Категория поезда">
                <Select pickAllCheckbox
                        options={trainCategoryOptions}
                        bind:value={values.trainCategories}/>
            </Field>
        </Section>
    {/if}
</CommonForm>