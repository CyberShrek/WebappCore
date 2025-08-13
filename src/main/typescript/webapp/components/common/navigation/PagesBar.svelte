<script lang="ts">

    import Button from "../input/Button.svelte";

    export let
        itemsCount: number,
        currentItemNumber: number = 0,
        pickedPageNumber: number = 1,
        pageSize = 100

    $: size = Math.ceil(itemsCount / pageSize)

    $: activePageNumber = Math.floor(currentItemNumber / pageSize) + 1

    // $: pageStartI = (activePageNumber - 1) * pageSize
    // $: pageEndI   = Math.min(pageStartI + pageSize, itemsCount)

    $: if(itemsCount)
        activePageNumber = activePageNumber > size ? size : activePageNumber

    function prev() {
        pickedPageNumber--
        activePageNumber--
    }

    function next() {
        pickedPageNumber++
        activePageNumber++
    }

</script>

<div class="nav-pages">
    {#if size > 0}
        <div class="pages-bar">
            <Button disabled={size <= 1 || pickedPageNumber === 1}
                    text='❬'
                    on:click={prev}/>
                <div class="pages">
                    {#each Array(size) as _, i}
                        <Button active={activePageNumber === i + 1} text={String(i + 1)} on:click={() => {pickedPageNumber = i + 1; activePageNumber = i + 1; currentItemNumber = (i * pageSize) + 1}}/>
                    {/each}
                </div>
            <Button disabled={size <= 1 || pickedPageNumber === size}
                    text='❭'
                    on:click={next}/>
        </div>
        <span class="context">
            {#if itemsCount !== -1}
                {currentItemNumber} из {itemsCount}
            {/if}
        </span>
    {/if}

    <slot/>
</div>