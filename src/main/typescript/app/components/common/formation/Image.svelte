<script lang="ts">
    import {serverLocations as defaultLocation} from "../../properties"

    export let
        name: string,
        hint: string = "",
        alt: string = hint,
        location: string = defaultLocation.images

</script>

<img src="{location}{name}" {alt} title={hint}/>