package org.vniizht.xlsx;


import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.vniizht.model.Report;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class XlsxReport
        implements AutoCloseable {

    private final Report report;
    private final XSSFWorkbook workbook;
    private final List<XlsxSheet> sheets = new ArrayList<>();
    private final Styles styles;

    public XlsxReport(Report report) {
        this.report = report;
//        report.context.addAll(getTestContext());
        workbook = new XSSFWorkbook();
        styles = new Styles(workbook);
        report.tables.forEach(table -> {
            sheets.add(new XlsxSheet(table,
                    workbook.createSheet(table.title),
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

    private Report.Table getContextTable() {
        Report.Table table = new Report.Table("Выбрано");
        table.head.addAll(getContextHead());
        table.body.addAll(getContextBody());
        return table;
    }

    private List<Report.Table.Row> getContextHead() {
        List<Report.Table.Row> rows = Arrays.asList(
                new Report.Table.Row(new ArrayList<>()),
                new Report.Table.Row(new ArrayList<>()));

        report.context.forEach(section -> {
            Report.Table.Cell firstCell = new Report.Table.Cell(section.title);
            firstCell.colspan = section.fields.size();
            rows.get(0).cells.add(firstCell);
            section.fields.forEach(field ->
                    rows.get(1).cells.add(new Report.Table.Cell(field.title)));
        });
        return rows;
    }

    private List<Report.Table.Row> getContextBody() {
        List<Report.Section.Field> fields = report.context.stream()
                .flatMap(section -> section.fields.stream())
                .collect(Collectors.toList());

        int rowsCount = fields
                .stream()
                .mapToInt(field -> field.values.isEmpty() ? 0 : field.values.size())
                .max()
                .orElse(0);

        List<Report.Table.Row> rows = new ArrayList<>(rowsCount);
        for (Report.Section.Field field : fields) {
            for (int rowI = 0; rowI < rowsCount; rowI++) {
                if (rows.size() <= rowI) {
                    rows.add(new Report.Table.Row(new ArrayList<>()));
                }
                rows.get(rowI).cells.add(
                        !field.values.isEmpty() && rowI < field.values.size()
                                ? new Report.Table.Cell(field.values.get(rowI))
                                : null);
            }
        }
        return rows;
    }
}
