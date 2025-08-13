<script lang="ts">

    export let
        value:  string,
        placeholder = "",
        isFocused: boolean = false

    $: if (value == null)
        value = ''
    
</script>
<input type="text"
       placeholder={placeholder}
       on:focus={() => isFocused = true}
       on:blur={() => isFocused = false}
       bind:value/>