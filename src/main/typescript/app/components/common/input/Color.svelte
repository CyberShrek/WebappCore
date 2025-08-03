<script lang="ts">
    export let value: string
</script>

<div class="color">
    <input type="color"
           bind:value>

    <input type="text"
           bind:value>
</div>