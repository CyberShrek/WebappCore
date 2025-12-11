package org.vniizht.webapp_core.xlsx;


import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.vniizht.webapp_core.model.export.DocumentExport;
import org.vniizht.webapp_core.model.export.report.Section;
import org.vniizht.webapp_core.model.export.report.Table;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class XlsxReport
        implements AutoCloseable {

    private final DocumentExport report;
    private final XSSFWorkbook workbook;
    private final List<XlsxSheet> sheets = new ArrayList<>();
    private final Styles styles;

    public XlsxReport(DocumentExport report) {
        this.report = report;
//        report.context.addAll(getTestContext());
        workbook = new XSSFWorkbook();
        styles = new Styles(workbook);
        report.tables.forEach(table -> {
            sheets.add(new XlsxSheet(table,
                    workbook.createSheet(report.title),
                    report.context,
                    styles));
        });
        sheets.add(new XlsxSheet(getContextTable(),
                workbook.createSheet("Контекст"),
                null,
                styles));
    }

    public void write(OutputStream out) throws IOException {
        workbook.write(out);
        out.flush();
    }

    @Override
    public void close() throws IOException {
        workbook.close();
    }

    private Table getContextTable() {
        Table table = new Table();
        table.head.addAll(getContextHead());
        table.body.addAll(getContextBody());
        return table;
    }

    private List<Table.Row> getContextHead() {
        List<Table.Row> rows = Arrays.asList(
                new Table.Row(new ArrayList<>()),
                new Table.Row(new ArrayList<>()));

        report.context.forEach(section -> {
            Table.Cell firstCell = new Table.Cell(section.title);
            firstCell.colspan = section.fields.size();
            rows.get(0).cells.add(firstCell);
            section.fields.forEach(field ->
                    rows.get(1).cells.add(new Table.Cell(field.title)));
        });
        return rows;
    }

    private List<Table.Row> getContextBody() {
        List<Section.Field> fields = report.context.stream()
                .flatMap(section -> section.fields.stream())
                .collect(Collectors.toList());

        int rowsCount = fields
                .stream()
                .mapToInt(field -> field.values.isEmpty() ? 0 : field.values.size())
                .max()
                .orElse(0);

        List<Table.Row> rows = new ArrayList<>(rowsCount);
        for (Section.Field field : fields) {
            for (int rowI = 0; rowI < rowsCount; rowI++) {
                if (rows.size() <= rowI) {
                    rows.add(new Table.Row(new ArrayList<>()));
                }
                rows.get(rowI).cells.add(
                        !field.values.isEmpty() && rowI < field.values.size()
                                ? new Table.Cell(field.values.get(rowI))
                                : null);
            }
        }
        return rows;
    }
}
