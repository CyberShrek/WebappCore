<script lang="ts">

    import {ColumnType} from "../types"

    export let
        matrixData: (string | number | boolean | null)[][],
        columnTypes: ColumnType[]

</script>

<tbody>

    {#each matrixData as row}
        <tr>
            {#each row as cell, i}
                <td class={columnTypes[i]}>{cell != null ? cell : ""}</td>
            {/each}
        </tr>
    {/each}

</tbody>