<script lang="ts">
    import {resolveStyles} from "../../util/resolver"
    import Loading from "./misc/Loading.svelte"
    import Header from "./navigation/Header.svelte"
    import ToTopButton from "./navigation/ToTopButton.svelte"
    import Fix from "./misc/Fix.svelte"

    let scrollY: number,
        innerHeight: number,
        outerHeight: number

</script>

{#await resolveStyles("global", "header", "inputs", "states", "formation")}
    <Loading/>
{:then _}
    <Header/>

    <slot/>

    <!--{#if scrollY > 100}-->
    <!--    <Fix right={true}-->
    <!--         bottom={true}>-->
    <!--        <ToTopButton/>-->
    <!--    </Fix>-->
    <!--{/if}-->
{/await}

<svelte:window bind:scrollY
               bind:innerHeight
               bind:outerHeight/>