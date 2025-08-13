import {ExportableReport} from "../model/export/ExportableReport"
import {SimpleHttp} from "./http/SimpleHttp"
import {serverLocations} from "../properties"

export function getReportData(reportId: string, formValues: { [fieldId: string]: any }) {
    return SimpleHttp
        .withHeaders({"Report-Id": reportId})
        .andBody(formValues)
        .post(serverLocations.report)
        .json<(string | number | boolean)[][]>()
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