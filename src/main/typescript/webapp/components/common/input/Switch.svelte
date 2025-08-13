<script lang="ts">

    export let
        value = false,
        title: string = "",
        type: "checkbox" | "switch" = "switch"

    $: if (value === undefined)
        value = false

</script>

<label class={type}>
    <input type="checkbox" bind:checked={value}>
    {title}
    {#if type === "switch"}
        <span class="slider"></span>
    {/if}
</label>