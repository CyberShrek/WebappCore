package org.vniizht.webapp_core.model.export;

import lombok.ToString;
import org.vniizht.webapp_core.model.export.report.ReportExport;

import java.util.List;
import java.util.Map;

@ToString
public class DocumentExport {
    public String title;
    public Map<String, Map<String, String>> form;
    public List<ReportExport> report;
}