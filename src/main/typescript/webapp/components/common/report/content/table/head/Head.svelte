<script lang="ts">

    import ColumnHeaders from "./ColumnHeaders.svelte"
    import ColumnOperations from "./ColumnOperations.svelte"
    import {ColumnType} from "../types";

    export let
        columnHeaders: string[],
        columnTypes: ColumnType[],
        // Provides
        columnOperations: {
            filter: string
            sort: "asc" | "desc"
        }[]

</script>

<thead>
    <ColumnHeaders    {columnHeaders}/>
<!--    <ColumnOperations {columnTypes}-->
<!--                      bind:columnOperations/>-->
</thead>