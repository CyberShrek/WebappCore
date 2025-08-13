<script lang="ts">

    import Button from "./Button.svelte"
    import {tick} from "svelte"
    import {EasepickModule} from "../../../third-party/EasepickModule"

    export let
        value: string[],
        range: number = null

    let rootElement: HTMLInputElement,
        module: EasepickModule

    $: if(rootElement)
        recreate()

    $: if(value == null && module)
        setValue(module.getValue())

    function recreate(){
        module?.destroy()
        module = new EasepickModule(rootElement, {
            range: range != null,
            maxDays: range
        })
        module.onChange(setValue)

        if(!value)
            setValue(module.getValue())
    }

    async function setValue(newValue: string[]){
        await tick()
        value = newValue
    }

</script>

<div class="datepicker">
    <Button image="calendar.svg" on:click={() => rootElement.click()}/>
    <input bind:this={rootElement}>
</div>