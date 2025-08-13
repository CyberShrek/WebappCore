<script lang="ts">

    import Loading from "./Loading.svelte"
    import {resolveStyle} from "../../../util/resolver"

    export let
        tabs: string[] = [],
        pickedTab: string

    $: indices = Array.from(Array(tabs.length).keys()).map(index => Number(index))
</script>

{#await resolveStyle("tabs")}
    <Loading/>
{:then _}
    <div class="tabs">
        <div class="header">
            {#each indices as index}
                <button class="tab"
                        class:active={pickedTab === tabs[index]}
                        on:click={() => pickedTab = tabs[index]}>{tabs[index]}</button>
            {/each}
        </div>

        <div class="content">
            <slot/>
        </div>
    </div>
{/await}