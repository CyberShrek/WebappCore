<script lang="ts">

    export let columnHeaders: string[]

</script>

<tr>
    {#each columnHeaders as header}
        <th>{header}</th>
    {/each}
</tr>
