<script lang="ts">

    export let
        matrixData: (string | number | boolean)[][],
        columnTypes: (typeof matrixData[0][0])[]

    let totalRowData: typeof matrixData[0]

    $: {
        totalRowData = columnTypes.map(type => type === "number" ? 0 : null)
        matrixData.forEach(rowData => {
            rowData.forEach((cell, cellI) => {
                if(totalRowData[cellI] != null)
                    (totalRowData[cellI] as number) += isNaN(cell as number) ? 0 : Number(cell)
            })
        })
    }

</script>

<tfoot>
{#if totalRowData?.length > 0}
    <tr class="total">
        {#each totalRowData as totalCell, cellI}
            <td class={"total " + columnTypes[cellI]}>
                {totalCell != null ? totalCell : ""}
            </td>
        {/each}
    </tr>
{/if}
</tfoot>