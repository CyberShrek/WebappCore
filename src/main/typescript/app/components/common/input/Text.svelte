<script lang="ts">

    export let
        config: TextConfig,
        value:  string,
        focused: boolean = false

    $: if (value?.length === 0 && config?.nullIfEmpty)
        value = null

    $: if (value === undefined)
        value = config?.nullIfEmpty ? null : ''
    
</script>
<input type="text"
       placeholder={config?.placeholder ?? ''}
       on:focus={() => focused = true}
       on:blur={() => focused = false}
       bind:value/>