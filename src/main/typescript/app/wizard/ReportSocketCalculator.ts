import {SimpleSocket} from "../../app/api/././simple/SimpleSocket"
import {serverLocations} from "../../app/properties"
import AsyncLock from "async-lock"

export class ReportSocket {

    private readonly socketPromise = SimpleSocket.connect(serverLocations.report)
    private readonly lock = new AsyncLock()

    public constructor(readonly report: SimpleSet, formValue: FormValue, mutationQuery?: string) {
        this.socketPromise.then((socket) => socket.send(JSON.stringify({
            "columnNames": report.columnNames,
            "mainData": report.data,
            "params": formValue,
            mutationQuery
        })))
    }

    public async calculateFormulas(formulas: string[], bunchData?: MatrixData): Promise<SimpleSet | string> {
        return this.execute({
            formulas,
            bunchData
        })
    }

    public async calculateQuery(query: string, bunchData?: MatrixData): Promise<SimpleSet | string> {
        return this.execute({
            query,
            bunchData
        })
    }

    public async close() {
        (await this.socketPromise).close()
    }

    private async execute(jsonBody: object): Promise<SimpleSet | string> {
        return this.lock.acquire("calculate",  async () => {
            const socket = await this.socketPromise
            return new Promise(resolve => {
                socket.onmessage = (event) => {
                    try {
                        resolve(JSON.parse(event.data))
                    } catch (_) {
                        resolve(event.data)
                    }
                }
                socket.send(JSON.stringify(jsonBody))
            })
        })
    }
}