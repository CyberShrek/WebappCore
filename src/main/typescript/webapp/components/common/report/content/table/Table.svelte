<script lang="ts">

    import Body from "./body/Body.svelte"
    import {createEventDispatcher} from "svelte"
    import Head from "./head/Head.svelte"
    import Foot from "./foot/Foot.svelte"
    import {ColumnType} from "./types";
    import Loading from "../../../misc/Loading.svelte";
    import {resolveStyle} from "../../../../../util/resolver";

    const dispatch = createEventDispatcher()

    export let
        matrixData: (string | number | boolean | null)[][],
        columnHeaders: string[],
        columnTypes: ColumnType[]

    let columnOperations: {
        filter: string
        sort:   "asc" | "desc"
    }[]

</script>

{#await resolveStyle("table")}
    <Loading/>
{:then _}
    <table>
        <Head {columnHeaders}
              {columnTypes}
              bind:columnOperations>
        </Head>

        <Foot {matrixData}
              {columnTypes}/>

        <Body {matrixData}
              {columnTypes}/>
    </table>
{/await}