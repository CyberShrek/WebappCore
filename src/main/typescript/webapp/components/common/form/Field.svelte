<script lang="ts">

    import {slide, fade} from "svelte/transition"
    import {onMount} from "svelte"

    export let
        title      = "",
        info       = "",
        message    = "",
        isWrong    = false,
        isReady    = false
 
    onMount(() => {
        isReady = true
    })

</script>

<div class="field"
     {title}
     class:wrong={isWrong}
     transition:fade>

    <div class="header">
        <p>
            {title}
        </p>
        {#if info}
            <span title="{info}">
                🛈
            </span>
        {/if}
    </div>

    <slot/>

    {#if message?.length > 0}
        <span class="message"
              class:wrong={isWrong}
              transition:slide>
            {message}
        </span>
    {/if}
</div>