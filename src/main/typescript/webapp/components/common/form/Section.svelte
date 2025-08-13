<script lang="ts">

    import {fade} from "svelte/transition"
    import Grid from "../misc/Grid.svelte"

    export let
        title: string = '',
        size: number = 4

</script>

<div class="section"
     transition:fade>

    <Grid className="fields"
          {title}
          {size}>

        <slot/>

    </Grid>
</div>