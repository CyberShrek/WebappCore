<script lang="ts">

    import Image from "../misc/Image.svelte"

    export let
        disabled      = false,
        active        = false,

        frameless     = false,
        submit        = false,
        cancel        = false,
        size: "small" | "medium" | "large" = "medium",

        hint          = "",
        text          = "",
        image         = "",
        imageLocation = undefined,
        root: HTMLButtonElement = null

</script>

<button class:disabled
        class:frameless
        class:submit
        class:cancel
        class:active
        class={size}
        title={hint}
        bind:this={root}
        on:click={event => event.preventDefault()}
        on:click
        on:mouseenter
        on:mousedown
        on:mouseup>

    {#if image && image.length > 0}
        <Image {hint}
               name={image}
               location={imageLocation}/>
    {/if}

    {text}

    <slot/>

</button>