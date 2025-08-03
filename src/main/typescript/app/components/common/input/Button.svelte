<script lang="ts">

    import Image from "../formation/Image.svelte"

    export let
        disabled      = false,
        frameless     = false,
        giant         = false,
        submit        = false,
        cancel        = false,
        active        = false,
        hint          = "",
        text          = "",
        image         = "",
        cursor        = "pointer",
        imageLocation = undefined,
        root: HTMLButtonElement = null

</script>

<button class:disabled
        class:frameless
        class:giant
        class:submit
        class:cancel
        class:active
        title={hint}
        style:cursor={cursor}
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