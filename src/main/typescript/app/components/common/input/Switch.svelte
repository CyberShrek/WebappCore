<script lang="ts">

    export let
        config: SwitchConfig,
        value: boolean = false

    $: if (config && value === undefined)
        value = false

</script>

<label class={config.basic ? "checkbox" : "switch"}>
    <input type="checkbox" bind:checked={value}>
    {config.title ?? ""}
    {#if !config.basic}
        <span class="slider"></span>
    {/if}
</label>