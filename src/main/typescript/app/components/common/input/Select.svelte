<script lang="ts">

    import {VirtualSelectModule} from "../../../app/third-party/VirtualSelectModule"
    import {equal, deepCopyOf, mapToJson} from "../../util/data.js"

    export let
        config: SelectConfig,
        options: OptionsMap,
        value: SelectValue

    let rootElement: HTMLDivElement,
        module: VirtualSelectModule

    $: if (options == null)
        options = new Map()

    $: if (value == null)
        value = config.returnType === "json" ? {}
            : config.multiple ? []
                : null

    // React to config changes
    $: if(config && rootElement)
        recreate()

    // React to options changes
    $: if(options && module) {
        module.setOptions(options)
    }

    // Outer changes
    $: if(options && value !== undefined && module)
        updateValue()

    function recreate(){
        if(module && equal(config, module?.config))
            return

        module?.destroy()
        module = new VirtualSelectModule(rootElement, deepCopyOf(config))
        module.onChange(newKeys => {
            newKeys = newKeys ?? []
            const optionsJson = mapToJson(module.findOptions(newKeys))
            if (config.returnType === "key")
                value = config.multiple ? newKeys : newKeys[0] ?? null
            else if (config.returnType === "value")
                value = config.multiple ? Object.values(optionsJson) : Object.values(optionsJson)[0] ?? null
            else
                value = optionsJson
        })
    }

    function updateValue(){
        function convertKeys(): OptionKey[]{
            if(value == null)               return []
            if(Array.isArray(value))        return value
            if(typeof value === "object")   return Object.keys(value)
                                            return [value]
        }
        module.setValue(convertKeys())
    }

</script>

<div class="select" bind:this={rootElement}>
</div>

<!--{JSON.stringify(Object.entries(options.entries()))}-->