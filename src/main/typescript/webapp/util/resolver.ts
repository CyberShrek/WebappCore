import {serverLocations} from "../properties";

const
    stylesPromises  = new Map<string, Promise<any>>(),
    modulesPromises = new Map<string, Promise<any>>()

export const resolveStyle = (name: string) => {
    if(!stylesPromises.has(name))
        stylesPromises.set(name, new Promise((resolve, reject) => {
            const link = document.createElement('link')
            link.rel = 'stylesheet'
            link.href = `${serverLocations.styles}${name}.css`
            link.onload = resolve
            link.onerror = reject
            document.head.appendChild(link)
        }))

    return stylesPromises.get(name)
}

export const resolveStyles = (...names: string[]) => Promise.all(names.map(resolveStyle))

export const resolveModule = (name: string) =>
    promisePromise(name, modulesPromises, import(`${serverLocations.modules}${name}.js`))


function promisePromise<T>(promiseName: string,
                           promiseContainer: Map<string, Promise<any>>,
                           promise: Promise<T>): Promise<T>{

    if(!promiseContainer.has(promiseName))
        promiseContainer.set(promiseName, promise)

    return promiseContainer.get(promiseName)
}

