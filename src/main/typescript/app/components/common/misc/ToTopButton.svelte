<script lang="ts">

    import Button from "../input/Button.svelte"
    import {scrollIntoElement} from "../../util/dom/common"

</script>

<Button hint="Вернуться наверх"
        image="to_top.svg"
        on:click={() => scrollIntoElement(document.body)}/>