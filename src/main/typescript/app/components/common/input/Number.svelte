<script lang="ts">

    export let
        config: NumberConfig,
        value: number

    let inputValue: number

    $: (value || !value) && importValue()

    $: inputValue && handleInput()

    function importValue() {
        if      (value == null)
            inputValue = config.default ?? config.min ?? 0
        else if (value != inputValue)
            inputValue = value
    }

    function handleInput() {
        if      (inputValue > config.max) inputValue = config.max
        else if (inputValue < config.min) inputValue = config.min
        value = inputValue
    }

</script>

<label class={config.range ? "range" : "number"}>
    <!-- Why not <input type={range ? "range" : "number"}>? -->
    <!-- It is not supported by Svelte. Quote: "'type' attribute cannot be dynamic if input uses two-way binding" -->
    {#if config.range}
        <input type="range"
               bind:value={inputValue}
               min={config.min}
               max={config.max}
               step={config.step}
               on:change={handleInput}>
        {inputValue}
    {:else}
        <input type="number"
               bind:value={inputValue}
               min={config.min}
               max={config.max}
               step={config.step}
               on:change={handleInput}>
    {/if}
</label>
