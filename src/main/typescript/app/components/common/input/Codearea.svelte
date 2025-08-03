<script lang="ts">
    import {CodeMirrorModule} from "../../../app/third-party/CodeMirrorModule"
    import {onDestroy, onMount} from "svelte"
    import Button from "./Button.svelte";
    import {popupError, popupTextInput} from "../../util/alert";
    import {SimpleHttp} from "../../../app/api/././simple/SimpleHttp";

    export let
        config: CodeareaConfig,
        value: string

    let rootElement: HTMLDivElement,
        module: CodeMirrorModule

    $: value = (value != null && typeof value === "object") ? JSON.stringify(value, null, 4) : value

    $: if(value != null)
        module?.setValue(value)

    function generateCode() {
        popupTextInput(
            "AI",
            "Введите запрос для генерации кода",
            "Подтвердить",
            async (text) => {
                value = (await SimpleHttp
                    .withHeaders()
                    .andBody({
                        system: config.instructAI,
                        prompt: text
                    })
                    .post("/visualforge/ai")
                    .text())
                    .replaceAll("```", '')
                    .trim()
            }
        )
    }

    onMount(() => {
        module = new CodeMirrorModule(rootElement, config.lang)
        module.onChange(newValue => value = newValue)
        module.setValue(value)
    })

    onDestroy(() => module?.destroy())

</script>

<div class="codearea" bind:this={rootElement}>
</div>

{#if config?.instructAI}
    <Button text="Генератор кода"
            on:click={generateCode}
            frameless={true}/>
{/if}