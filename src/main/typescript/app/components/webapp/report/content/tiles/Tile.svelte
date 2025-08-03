<script lang="ts">

    import {fade} from "svelte/transition"
    import {TileContent} from "./TilesColumn"

    export let
        config: TileConfig,
        content: TileContent

    let tileElement: HTMLDivElement

    $: if (tileElement && config){
        tileElement.style.background = config.background
        tileElement.style.color = config.color
    }

</script>

<div class="tile-wrapper"
     in:fade>
    {#if config?.title}
        <p>{config?.title}</p>
    {/if}
    {#if content?.childReportId?.length > 0}
        <div class="clickable tile"
             role="button"
             on:click
             bind:this={tileElement}>
            {@html content?.html ?? '...'}
        </div>
    {:else}
        <div class="tile"
             bind:this={tileElement}>
            {@html content?.html ?? '...'}
        </div>
    {/if}
    <slot/>
</div>
