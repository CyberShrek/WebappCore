<script lang="ts">

    import {onDestroy} from "svelte";
    import ContentBody from "./ContentBody.svelte";
    import {ReportSocket} from "../../../../common/wizard/ReportSocketCalculator";
    import Modal from "../../../../common/components/formation/Modal.svelte";

    export let
        config: TableConfig | ChartsBlockConfig | TilesBlockConfig,
        formValue: FormValue,
        mainSet: SimpleSet,
        nickChildren: NickReportConfig[],
        editable = false

    let showModal: boolean,
        socket: ReportSocket

    $: if(config && (editable || formValue) && mainSet) {
        reinstallSocket()
    }

    async function reinstallSocket() {
        await socket?.close()
        socket = new ReportSocket(mainSet, formValue ?? {}, config.query)
    }

    onDestroy(() => socket?.close())

</script>

{#if config}
    <div class="content">
        {#if config.title || editable}
            <div class="header">
                {#if config.modal === true}
                    <a href=""
                       on:click={(ev) => {ev.preventDefault() ; showModal = true}}>
                        {config.title ?? "Показать"}
                    </a>
                {:else if config.title}
                    <h3>{config.title}</h3>
                {/if}

                <slot/>
            </div>
        {/if}
        {#if config.modal === true}
            <Modal bind:show={showModal}>
                <ContentBody bind:config
                             {socket}
                             {nickChildren}
                             {editable}
                             on:report/>
            </Modal>
        {:else}
            <ContentBody bind:config
                         {socket}
                         {nickChildren}
                         {editable}
                         on:report/>
        {/if}
    </div>
{/if}