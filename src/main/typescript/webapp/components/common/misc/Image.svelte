<script lang="ts">

    import {serverLocations} from "../../../properties"

    export let
        name: string,
        hint: string = "",
        alt: string = hint,
        location: string = serverLocations.images

</script>

<img src="{location}{name}" {alt} title={hint}/>