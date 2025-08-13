import {Module} from "./abstract/Module"
import {resolveStyle} from "../util/resolver"
import swal, {SweetAlertOptions, SweetAlertPosition, SweetAlertResult} from "sweetalert2"
import {sweetAlertProperties} from "../properties"

resolveStyle("third-party/sweetalert2")
resolveStyle("third-party/animate")

export class SweetAlertModule extends Module {

    private mouseEvent: MouseEvent

    constructor() {
        super()
        document.addEventListener("mousemove", event => this.mouseEvent = event)
    }

    alert(options: SweetAlertOptions): Promise<SweetAlertResult>{
        const position = this.getSweetMousePosition()
        // @ts-ignore
        return swal.fire({
            position,
            ...sweetAlertProperties,
            ...options,
            showClass: {popup: 'animate__animated '+getAnimationShowClass(position)},
            hideClass: {popup: 'animate__animated animate__zoomOut'},
        })
    }

    private getSweetMousePosition(): SweetAlertPosition{
        if(!this.mouseEvent) return "center"

        const { clientX, clientY } = this.mouseEvent,
            screenWidth = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth,
            screenHeight = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight,
            horizontalPosition = clientX <= screenWidth / 3 ? 'left' : clientX >= (screenWidth / 3) * 2 ? 'right' : 'center',
            verticalPosition = clientY <= screenHeight / 3 ? 'top' : clientY >= (screenHeight / 3) * 2 ? 'bottom' : 'center',
            position = `${verticalPosition}-${horizontalPosition}`

        return position === "top-center" ? "top" : position === "bottom-center" ? "bottom" : position as SweetAlertPosition
    }
}

function getAnimationShowClass(position: SweetAlertPosition): string{
    switch (position) {
        case "top-left"     : return "animate__fadeInTopLeft"
        case "top"          : return "animate__fadeInDown"
        case "top-right"    : return "animate__fadeInTopRight"
        case "center-left"  : return "animate__fadeInLeft"
        case "center"       : return "animate__fadeIn"
        case "center-right" : return "animate__fadeInRight"
        case "bottom-left"  : return "animate__fadeInBottomLeft"
        case "bottom"       : return "animate__fadeInUp"
        case "bottom-right" : return "animate__fadeInBottomRight"
        default             : return ""
    }
}