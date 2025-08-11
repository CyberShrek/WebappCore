import {SweetAlertModule} from "../../app/third-party/SweetAlertModule"

const sweet = new SweetAlertModule()

export function popupMessage(title?: string, text?: string){
    sweet.alert({title, text})
}

export function popupList(title?: string, list?: (string | { icon: string, text: string })[], footer?: string){
    sweet.alert({
        title,
        html:`<ul>${list.map(item => `<li style='list-style-type: "${typeof item === "object" ? item.icon + "\t" : ""}"'>${typeof item === "object" ? item.text : item}</li>`).join("")}</ul>`,
        footer
    })
}


export function popupAction(title?: string, text?: string, confirmButtonText?: string, onConfirm?: () => void){
    sweet.alert({
        title,
        text,
        confirmButtonText,
        showConfirmButton: true
    }).then(result => {
        if(result.isConfirmed)
            onConfirm()
    })
}

export function popupRadioAction(title: string, text: string, radios: {[value: string]: string}, confirmButtonText?: string, onConfirm?: (value: string) => void){
    sweet.alert({
        title,
        text,
        confirmButtonText,
        showConfirmButton: true,
        inputOptions: radios,
        input: "radio",
        inputValue: Object.keys(radios)[0],
        inputValidator: (value) => {
            if (!value) {
                return "выберите вариант"
            }
        }
    }).then(result => {
        if(result.isConfirmed)
            onConfirm(result.value)
    })
}

export function popupTimeoutAction(text?: string, confirmButtonText?: string, onConfirm?: () => void){
    sweet.alert({
        text, confirmButtonText,
        showConfirmButton: true,
        timer: 5000,
        timerProgressBar: true,
        backdrop: false
    }).then(result => {
        if(result.isConfirmed)
            onConfirm()
    })
}

export function popupError(error?: Error, footer?: string){
    return sweet.alert({title: error.name, text: error.message, icon: "error", footer})
    // throw error/**/
}

export function popupTextInput(title?: string, text?: string, confirmButtonText?: string, onConfirm?: (value: string) => void){
    sweet.alert({
        title,
        text,
        confirmButtonText,
        showConfirmButton: true,
        input: "text",
        inputValidator: (value) => {
            if (!value) {
                return "введите текст"
            }
        }
    }).then(result => {
        if(result.isConfirmed)
            onConfirm(result.value)
    })
}