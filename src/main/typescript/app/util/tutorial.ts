export function hint(
    element: HTMLElement,
    message: string,
    exitType: keyof HTMLElementEventMap
): Promise<void> {
    return new Promise<void>((resolve) => {

        const hintElement = document.createElement('div');
        hintElement.textContent = message;
        hintElement.style.position = 'absolute';
        hintElement.style.top = `${element.offsetTop + element.offsetHeight}px`;
        hintElement.style.left = `${element.offsetLeft}px`;
        hintElement.style.background = "white"
        hintElement.style.padding = "10px"
        hintElement.style.borderRadius = "5px"
        document.body.appendChild(hintElement)

        const handleExit = (event: Event) => {
            if (event.type === exitType) {
                resolve();
                document.body.removeChild(hintElement);
                element.removeEventListener(exitType, handleExit);
            }
        };

        element.addEventListener(exitType, handleExit);
    });
}
