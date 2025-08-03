import Webapp from "./components/Webapp.svelte"
import {getWebapp} from "./api/crud"

new Webapp({
    target: document.body,
    props: {
        webapp: await getWebapp()
    }
})