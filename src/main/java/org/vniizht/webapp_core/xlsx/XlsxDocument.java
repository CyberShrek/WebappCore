package org.vniizht.webapp_core.xlsx;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.vniizht.webapp_core.model.export.DocumentExport;
import org.vniizht.webapp_core.model.export.report.ImageExport;
import org.vniizht.webapp_core.model.export.report.ReportExport;
import org.vniizht.webapp_core.model.export.report.TableExport;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class XlsxDocument implements AutoCloseable {

    private final Workbook workbook;
    private final Styles   styles;

    public XlsxDocument(DocumentExport document) {

        workbook = new XSSFWorkbook();
        styles   = new Styles(workbook);

        XlsxSheet sheet = new XlsxSheet(workbook.createSheet(document.title), styles);
        sheet.setColumnCount(determineColumnCount(document));

        sheet.new Form(document.title, document.form);
        document.report.forEach(content -> {
            if (content.getClass().equals(TableExport.class))
                sheet.new Table((TableExport) content);
            else if (content.getClass().equals(ImageExport.class)) {
                sheet.new Image((ImageExport) content);
            }
            else
                throw new IllegalArgumentException("Unknown report content type: " + content.getClass().getName());
        });
        sheet.applyOuterBorders();
        sheet.resize();
    }

    public void write(OutputStream out) throws IOException {
        workbook.write(out);
        out.flush();
    }

    @Override
    public void close() throws IOException {
        workbook.close();
    }

    private static int determineColumnCount(DocumentExport document) {
        int columnCount = 0;

        for(ReportExport content : document.report) {
            if (content.getClass().equals(TableExport.class)) {
                List<TableExport.Cell> firstRow = ((TableExport) content).body.get(0);
                if (firstRow != null && firstRow.size() > columnCount)
                    columnCount = firstRow.size();
            }
        }
        return columnCount;
    }
}