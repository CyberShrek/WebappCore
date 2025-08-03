<script lang="ts">

    export let
        checkedArray: boolean[],
        clientIndices: number[] = null

    let checked: boolean

    $: if (clientIndices == null)
        clientIndices = checkedArray?.map((_, index) => index)

    $: checked ? pickAll() : unpickAllIfAllArePicked()
    $: allAreChecked = clientIndices?.length > 0 && clientIndices.every(index => checkedArray[index] === true)
    $: checked = allAreChecked === true

    function pickAll() {
        checkedArray = checkedArray.map((value, index) => clientIndices.includes(index) ? true : value)
    }

    function unpickAllIfAllArePicked() {
        if(allAreChecked) {
            checkedArray = checkedArray.map((value, index) => clientIndices.includes(index) ? false : value)
        }
    }

</script>

<input type="checkbox" bind:checked/>