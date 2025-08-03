
type Task = (args?: any) => any

const tasks = new Set<Task>()

export function runTask(task: Task, duration: number = 100, args?: any) {
    tasks.add(task)

    const tact = () => {
        if(tasks.has(task)) {
            task(args)
            setTimeout(() => {
                tact()
            }, duration)
        }
    }
    tact()
}

export function stopTask(task: Task) {
    tasks.delete(task)
}