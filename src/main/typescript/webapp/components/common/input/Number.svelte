<script lang="ts">

    export let
        value: number,
        range: boolean = false,
        min: number = null,
        max: number = null,
        step: number = 1

</script>

<label class={range ? "range" : "number"}>
    <!-- Why not <input type={range ? "range" : "number"}>? -->
    <!-- It is not supported by Svelte. Quote: "'type' attribute cannot be dynamic if input uses two-way binding" -->
    {#if range}
        <input type="range"
               bind:value
               {min}
               {max}
               {step}>
        {value}
    {:else}
        <input type="number"
               bind:value
               {min}
               {max}
               {step}>
    {/if}
</label>
