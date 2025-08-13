<script lang="ts">

    import Sort from "../../../../input/Sort.svelte"
    import Text from "../../../../input/Text.svelte"
    import {ColumnType} from "../types";

    export let
        columnTypes: ColumnType[],
        columnOperations: {
            filter: string
            sort: "asc" | "desc"
        }[] = columnTypes.map(() => ({filter: "", sort: null}))

</script>

<tr class="operations sort">
    {#each columnOperations as operation}
        <th>
            <Sort bind:value={operation.sort}/>
        </th>
    {/each}
</tr>

<tr class="operations filter">
    {#each columnOperations as operation, colI}
        <th>
            {#if columnTypes[colI] === "string"}
                <Text bind:value={operation.filter}
                      placeholder="⌕ Поиск"/>
            {/if}
        </th>
    {/each}
</tr>