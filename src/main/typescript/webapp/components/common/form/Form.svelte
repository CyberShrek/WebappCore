<script lang="ts">
    import Section from "./Section.svelte"
    import {createEventDispatcher, onMount, tick} from "svelte"
    import {fade} from "svelte/transition"
    import Report from "../report/Report.svelte"
    import {resolveStyle} from "../../../util/resolver";
    import Loading from "../misc/Loading.svelte";
    import Button from "../input/Button.svelte";

    const dispatch = createEventDispatcher(),
        SUBMIT_EVENT = "submit"

    export let
        values: {[fieldId: string]: any},
        submitIsTouched = false,
        isWrong         = false


    async function handleSubmit(){
        dispatch(SUBMIT_EVENT, {formValues: values})
    }

</script>

{#await resolveStyle("form")}
    <Loading/>
{:then _}
    <form in:fade={{duration: 200}}>

        <slot/>

        <div class="buttons">
            <Button submit
                    text={"Подтвердить"}
                    disabled={submitIsTouched && isWrong}
                    on:click={() => handleSubmit()}
                    on:mouseenter={() => submitIsTouched = true}/>
        </div>
    </form>
    <!--{JSON.stringify(value)}-->
{/await}