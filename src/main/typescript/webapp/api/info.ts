import {SimpleHttp} from "./http/SimpleHttp"
import {serverLocations} from "../properties"

export function getUserInfo(): Promise<UserInfo> {
    return SimpleHttp
        .justGet(serverLocations.userinfo)
        .json<UserInfo>()
}

export function getAppInfo(): Promise<AppInfo> {
    return SimpleHttp
        .justGet(serverLocations.appinfo)
        .json<AppInfo>()
}