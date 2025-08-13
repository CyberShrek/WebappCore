import {toJpeg} from "dom-to-image"

// Used to create inner html and return them as elements
const factoryElement = document.createElement("factory")
document.body.appendChild(factoryElement)
factoryElement.hidden = true

export function create<T extends HTMLElement>(html: string): T{
    factoryElement.innerHTML = html
    return factoryElement.firstElementChild as T
}

// Removes all child elements
export function emptyElement(element: Element, withText: boolean = true){
    const text = element.textContent
    while(element.firstElementChild !== null)
        element.firstElementChild.remove()

    if(!withText) element.textContent = text
}

export function swapElements(element1: HTMLElement, element2: HTMLElement) {
    const temp = document.createElement('div')
    element1.replaceWith(temp)
    element2.replaceWith(element1)
    temp.replaceWith(element2)
}

export function exportAsJpeg(element: HTMLElement, jpegName: string = "element"){
    toJpeg(element)
        .then((dataUrl) => {
            const link = document.createElement("a")
            link.download = jpegName+".jpeg"
            link.href = dataUrl
            link.click()
            link.remove()
        })
}

export function scrollIntoElement(element: HTMLElement) {
    element.scrollIntoView({behavior: "smooth", block: "start"})
}

export function toggleFullscreen(element: HTMLElement){
    if(!!getFullscreenElement())
        document.exitFullscreen()
    else
        element.requestFullscreen()
}

export function getFullscreenElement(): Element{
    return document.querySelector(":fullscreen")
}

// Each enable of cursor loading adds 1 item into this array, each disabling removes also 1 item.
// The only purpose is to prevent disabling when not all loaded
let cursorLoadersCount = 0

export function addCursorLoader() {
    document.documentElement.style.cursor = 'wait'
    cursorLoadersCount++
}

export function removeCursorLoader() {
    cursorLoadersCount--
    if(cursorLoadersCount <= 0)
        document.documentElement.style.cursor = 'default'
}