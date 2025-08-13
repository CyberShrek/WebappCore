<script lang="ts">

    export let
        value: "asc" | "desc" | null = null

</script>

<button class="sort {value ?? ''}"
        title="Сортировка"
        on:click={() => value = value == null ? "asc" : value === "asc" ? "desc" : null}>
    {#if value === "asc"}
        ↑
    {:else if value === "desc"}
        ↓
    {:else}
        ⇅
    {/if}
</button>