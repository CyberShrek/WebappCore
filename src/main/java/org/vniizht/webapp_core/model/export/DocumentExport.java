package org.vniizht.webapp_core.model.export;

import org.vniizht.webapp_core.model.export.form.FormExport;
import org.vniizht.webapp_core.model.export.report.ContentExport;

import java.util.ArrayList;
import java.util.List;

public class DocumentExport {
    public String title;
    public FormExport form;
    public List<ContentExport> report;
}