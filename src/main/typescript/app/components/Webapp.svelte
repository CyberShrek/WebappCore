<script lang="ts">
    import Form from "./form/Form.svelte"
    import {FormObservatory} from "./form/FormObservatory"
    import Header from "./webapp/navigation/Header.svelte"
    import {resolveStyles} from "../util/resolver"
    import Loading from "../../common/components/formation/Loading.svelte"
    import Tabs from "../../common/components/formation/Tabs.svelte"

    export let webapp: WebappDetails

    $: forms = webapp?.frontend?.forms

    let scrollY: number,
        innerHeight: number,
        outerHeight: number,
        observatory: FormObservatory

    let pickedFormIndex = 0

</script>

{#await resolveStyles("global", "header", "inputs", "states", "formation")}
    <Loading/>
{:then _}
    <Header info={webapp.info}/>

    {#if forms}
        {#if forms.length > 1}
            <Tabs names={forms.map((form, index) => form.title ?? `Форма ${index + 1}`)}
                  bind:pickedIndex={pickedFormIndex}>
                <Form config={forms[pickedFormIndex]}
                      bind:observatory/>
            </Tabs>
        {:else}
            <Form config={forms[0] ?? {}}
                  bind:observatory/>
        {/if}
    {/if}

    <!--{#if scrollY > 100}-->
    <!--    <Fix right={true}-->
    <!--         bottom={true}>-->
    <!--        <ToTopButton/>-->
    <!--    </Fix>-->
    <!--{/if}-->
{/await}
<svelte:window bind:scrollY
               bind:innerHeight
               bind:outerHeight/>