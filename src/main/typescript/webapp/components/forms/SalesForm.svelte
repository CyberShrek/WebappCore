<script lang="ts">
    import Section from "../common/form/Section.svelte";
    import CommonForm from "./CommonForm.svelte";
    import {SalesValues} from "./valueTypes";
    import {onMount} from "svelte";
    import {getOptionsFromSB} from "../../api/serviceBank";
    import Field from "../common/form/Field.svelte";
    import Select from "../common/input/Select.svelte";

    let values: SalesValues,
        submitIsTouched: boolean,
        isWrong: boolean

    let regionOptions: {[key: string]: string} = {},
        paymentTypeOptions = { 2: "Наличный", 3: "Банковские карты", 5: "Безналичный (ПЮ)", 6: "Электронный кошелек", 1: "Льготный", 4: "Безналичный (Интернет)" },
        travelTicketTypeOptions = { 1: "Разовый", 2: "Абонементный на количество поездок", 3: "Абонементный билет «ежедневно» месяцы", 4: "Абонементный билет «ежедневно» дни", 5: "Абонементный билет «рабочего дня»", 6: "Абонементный билет «выходного дня»", 7: "Абонементный билет «на определенные даты (четные/нечетные)»" },
        shippingTicketTypeOptions = { 1: "Ручная кладь по весу", 2: "Ручная кладь – телевизор", 3: "Ручная кладь – велосипед", 4: "Ручная кладь – живность", 5: "Сбор за оформление в поезде" },
        operationTypeOptions = { true: "Оформление", false: "Возврат" }

    onMount(async () => {
        regionOptions = {
            "*": "Все субъекты",
            ...(await getOptionsFromSB("sfList", "sf_kod2", "sf_name", {
                data: "2025-01-01"
            }))
        }
    })

</script>

<CommonForm
        dativeTitle="продаж"
        bind:values
        bind:isWrong
        bind:submitIsTouched
        on:submit>
    <Field title="Субъект продажи"
           slot="main section">
        <Select search
                options={regionOptions}
                bind:value={values.regions}/>
    </Field>
    {#if values.additional}
        <Section>
            <Field title="Виды расчёта">
                <Select pickAllCheckbox
                        options={paymentTypeOptions}
                        bind:value={values.paymentTypes}/>
            </Field>
            <Field title="Виды проездного документа">
                <Select pickAllCheckbox
                        options={travelTicketTypeOptions}
                        bind:value={values.travelTicketTypes}/>
            </Field>
            <Field title="Виды перевозочного документа">
                <Select pickAllCheckbox
                        options={shippingTicketTypeOptions}
                        bind:value={values.shippingTicketTypes}/>
            </Field>
            <Field title="Виды операции">
                <Select pickAllCheckbox
                        options={operationTypeOptions}
                        bind:value={values.operationTypes}/>
            </Field>
        </Section>
    {/if}
</CommonForm>