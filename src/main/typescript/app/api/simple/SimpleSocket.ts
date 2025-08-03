import {popupError} from "../util/alert";

export class SimpleSocket{

    static connect(location: string): Promise<WebSocket> {
        return new Promise<WebSocket>((resolve) => {
            const socket = new WebSocket(location)
            let errorOccured = false

            socket.onopen = (event) => {
                resolve(socket)
            }

            socket.onerror = (event) => {
                console.log("error", event)
                errorOccured = true
            }
            socket.onclose = (event) => {
                if(errorOccured) {
                    const error = new Error(`${event.code}: ${event.reason}`)
                    popupError(error)
                    console.error(error)
                }
            }
        })
    }
}