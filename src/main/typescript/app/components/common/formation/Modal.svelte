<script lang="ts">

    import { fade } from 'svelte/transition'
    import {createEventDispatcher, onDestroy} from "svelte"

    const dispatch = createEventDispatcher()

    export let
        show: boolean = false,
        fullSize: boolean = false

    let rootElement: HTMLDivElement

    $: if(rootElement) document.body.appendChild(rootElement)

    // Disable scroll when modal is opened
    $: document.body.style.overflow = show === true ? "hidden" : ""

    $: if (show === false)
        dispatch("close")

    $: if(rootElement) {
        window.addEventListener("keydown", onKeydown)
        rootElement.addEventListener("click", onBackdropClick)
    } else {
        window.removeEventListener("keydown", onKeydown)
    }

    // Close on escape key
    function onKeydown(event: KeyboardEvent) {
        // if (event.key === "Escape") {
        //     show = false
        // }
    }
    // Close on backdrop click
    function onBackdropClick(event: MouseEvent) {
        if (event.target === rootElement) {
            show = false
        }
    }

    onDestroy(() => {
        window.removeEventListener("keydown", onKeydown)
        document.body.style.overflow = ""
    })

</script>
{#if show === true}
    <div bind:this={rootElement}
         class="modal-backdrop"
         role="button"
         tabindex="-1"
         transition:fade={{duration: 50}}>

        <div class="modal"
             class:full-size={fullSize}
             role="none">
            <slot/>
        </div>
    </div>
{/if}