<script lang="ts">

    import Modal from "../../misc/Modal.svelte";

    export let
        title = "",
        modal = false

    let show: boolean

</script>

<div class="content">
    {#if modal}
        <a href=""
           on:click={(ev) => {ev.preventDefault() ; show = true}}>
            {title ?? "Показать"}
        </a>
        <Modal bind:show>
            <slot/>
        </Modal>
    {:else}
        {#if title}
            <div class="header">
                <h3>{title}</h3>
            </div>
        {/if}
        <slot/>
    {/if}
</div>
