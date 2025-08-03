<script lang="ts">


    import Loading from "./Loading.svelte";
    import {resolveStyle} from "../../util/resolver";

    export let
        names: string[] = [],
        pickedIndex = 0

    $: indices = Array.from(Array(names.length).keys()).map(index => Number(index))
</script>

{#await resolveStyle("tabs")}
    <Loading/>
{:then _}
    <div class="tabs">
        <div class="header">
            {#each indices as index}
                <button class="tab"
                        class:active={pickedIndex === index}
                        on:click={() => pickedIndex = index}>{names[index]}</button>
            {/each}
        </div>

        <div class="content">
            <slot/>
        </div>
    </div>
{/await}