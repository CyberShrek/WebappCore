import Webapp from "./components/Webapp.svelte"
import {getAppInfo, getUserInfo} from "./api/info";

export const
    userInfo = await getUserInfo(),
    appInfo = await getAppInfo()

new Webapp({
    target: document.body
})