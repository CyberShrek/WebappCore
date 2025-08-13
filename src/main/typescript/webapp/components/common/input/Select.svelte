<script lang="ts">

    import {VirtualSelectModule} from "../../../third-party/VirtualSelectModule"

    type Options = { [key: string]: string}

    export let
        value: Options,
        options: Options,
        multiple        = false,
        search          = false,
        showCodes       = false,
        placeholder     = '',
        pickAllCheckbox = false,
        maxValues       = 0

    let rootElement: HTMLDivElement,
        module: VirtualSelectModule

    $: if (options == null)
        options = {}

    $: if (value == null)
        value = {}

    $: if(rootElement)
        recreate()

    // React to options changes
    $: if(options && module) {
        module.setOptions(Object.entries(options)
            .map(([key, value]) => {return {
                value: key,
                label: value,
                alias: key,
                description: key
            }})
        )
    }

    // Handle value changes
    $: if(options && value !== undefined)
        module?.setValue(Object.keys(value))

    function recreate(){
        module?.destroy()
        module = new VirtualSelectModule(rootElement, {
            multiple,
            search,
            showCodes,
            placeholder,
            disableSelectAll: !pickAllCheckbox,
            maxValues
        })
        module.onChange(newKeys => {
            value = {}
            newKeys = newKeys ?? []
            newKeys.forEach(key => value[key] = options[key])
        })
    }

</script>

<div class="select" bind:this={rootElement}>
</div>

<!--{JSON.stringify(Object.entries(options.entries()))}-->