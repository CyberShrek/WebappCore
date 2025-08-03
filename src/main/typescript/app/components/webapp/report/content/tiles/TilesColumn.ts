import {ReportSocket} from "../../../../../common/wizard/ReportSocketCalculator";
import {regexp} from "../../../../../common/util/string";

export type TileContent = {
    html: string
    childReportId?: string
}

export class TilesColumn {

    content: Promise<TileContent[]>

    constructor(
        // @ts-ignore
        config: TileConfig,
        socket: ReportSocket
    ) {
        const formulas = [...(config?.html?.match(regexp.curlyBraceContent) ?? []), config?.childButtonCondition ?? '']
        this.content = new Promise(async resolve => {
            const column: TileContent[] = []
            const calculated = await socket.calculateFormulas(formulas)

            if (calculated == null || typeof calculated === "string")
                column.push({
                    html: calculated as string
                })
            else {
                calculated.data?.forEach(calculatedRow => {
                    let calculatedContent: TileContent = {
                        html: config.html ?? ''
                    }
                    formulas.forEach((formula, formulaI) => {
                        if (formulaI < formulas.length - 1)
                            calculatedContent.html = calculatedContent.html.replace(`{${formula}}`, calculatedRow[formulaI]?.toString())
                        else
                            calculatedContent.childReportId = calculatedRow[formulaI] as string
                    })
                    column.push(calculatedContent)
                })
            }
            resolve(column)
        })
    }
}