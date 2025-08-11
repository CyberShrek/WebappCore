import {SimpleHttp} from "./././simple/SimpleHttp"
import {serverLocations} from "../properties"
import {ExportableReport} from "../model/export/ExportableReport"

export async function getUser(code?: string): Promise<UserDetails> {
    return SimpleHttp
        .withHeaders(
            code ? {"Code": code} : {}
        )
        .get(serverLocations.user)
        .json<UserDetails>()
}

export async function downloadReport(report: ExportableReport) {
    download(await SimpleHttp
        .withHeaders()
        .andBody(report)
        .post(serverLocations.export)
        .blob(), report.title)
}

function download(blob: Blob, name: string) {
    const url  = URL.createObjectURL(blob);
    const a    = document.createElement('a');
    a.href     = url;
    a.download = name;
    a.click();
    a.remove();
    URL.revokeObjectURL(url);
}