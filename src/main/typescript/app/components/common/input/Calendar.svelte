<script lang="ts">

    import Button from "./Button.svelte"
    import {EasepickModule} from "../../../app/third-party/EasepickModule";
    import {tick} from "svelte";

    export let
        config: CalendarConfig,
        value: CalendarValue

    let rootElement: HTMLInputElement,
        module: EasepickModule

    $: if(config && rootElement)
        recreate()

    $: if(value == null && module)
        setValue(module.getValue())

    function recreate(){
        module?.destroy()
        module = new EasepickModule(rootElement, config)
        module.onChange(setValue)

        if(!value)
            setValue(module.getValue())
    }

    async function setValue(newValue: EasepickValue){
        await tick()
        value = config.range ? [newValue.date, newValue.dateEnd] : newValue.date
    }

</script>

<div class="datepicker">
    <Button image="calendar.svg" on:click={() => rootElement.click()}/>
    <input bind:this={rootElement}>
</div>