package org.vniizht.webapp_core.xlsx;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.vniizht.webapp_core.model.export.report.ImageExport;
import org.vniizht.webapp_core.model.export.report.TableExport;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.vniizht.webapp_core.xlsx.Styles.COLUMN_WIDTH_IN_SYMBOLS;
import static org.vniizht.webapp_core.xlsx.Styles.MARGIN;

class XlsxSheet {

    private final Sheet sheet;
    private final Styles styles;

    private Integer columnCount = 1;

    XlsxSheet(Sheet  sheet,
              Styles styles) {
        this.sheet = sheet;
        this.styles = styles;

    }

    void setColumnCount(Integer columnCount) {
        this.columnCount = columnCount;
        for (int i = MARGIN; i < columnCount + MARGIN; i++)
            sheet.setColumnWidth(i, COLUMN_WIDTH_IN_SYMBOLS * 256);
    }

    void applyOuterBorders() {
        CellRangeAddress region = new CellRangeAddress(
                MARGIN,
                sheet.getLastRowNum(),
                MARGIN,
                columnCount - 1 + MARGIN
        );
        RegionUtil.setBorderTop(        styles.outerBorder,        region, sheet);
        RegionUtil.setBorderBottom(     styles.outerBorder,        region, sheet);
        RegionUtil.setBorderLeft(       styles.outerBorder,        region, sheet);
        RegionUtil.setBorderRight(      styles.outerBorder,        region, sheet);
        RegionUtil.setTopBorderColor(   Styles.ACCENT_COLOR.index, region, sheet);
        RegionUtil.setBottomBorderColor(Styles.ACCENT_COLOR.index, region, sheet);
        RegionUtil.setLeftBorderColor(  Styles.ACCENT_COLOR.index, region, sheet);
        RegionUtil.setRightBorderColor( Styles.ACCENT_COLOR.index, region, sheet);
    }

    class Form extends Content{

        private final static String TOP_TITLE = "АСУ \"ЭКСПРЕСС-НП\"";

        Form(String title, Map<String, Map<String, String>> form) {
            nextRow(TOP_TITLE, styles.topHeader);
            nextRow(title,     styles.topHeader);
            nextRow(new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date()), styles.italic);
            nextRow("",     styles.blank);
            form.forEach((section, fields) -> {
                if (!section.isEmpty())
                    nextRow(section, styles.sectionTitle);
                fields.forEach((field, value) -> {
                    nextRow(field + ": " + value, styles.field);
                });
                nextRow("",     styles.blank);
            });
        }
    }

    class Table extends Content {

        private List<TableExport.ColumnType> types;

        Table(TableExport table) {
            types = table.types;
            nextRow(table.title, styles.tableHead).setHeightInPoints(Styles.TABLE_CELL_HEIGHT_IN_POINTS);
            buildSection(table.head, styles.tableHead, false);
            buildSection(table.body, styles.tableBody, true );
            buildSection(table.foot, styles.tableFoot, true );
        }

        private void buildSection(List<List<TableExport.Cell>> section, CellStyle style, boolean setTypes) {
            section.forEach(row -> {
                nextRow().setHeightInPoints(Styles.TABLE_CELL_HEIGHT_IN_POINTS);
                for (int i = 0; i < row.size(); i++) {
                    Cell cell = nextCell(row.get(i), style);
                    if (setTypes){
                        switch (types.get(i)){
                            case STRING: break;
                            case NUMBER: cell.setCellType(CellType.NUMERIC); break;
                            case BOOLEAN: cell.setCellType(CellType.BOOLEAN); break;
                        }
                    }
                }
            });
        }
    }

    class Image extends Content {

        Image(ImageExport image) {
            nextRow(image.title, styles.imageTitle);
            nextRow("", styles.blank);

            ClientAnchor anchor = sheet.getWorkbook().getCreationHelper().createClientAnchor();
            anchor.setCol1(MARGIN);
            anchor.setCol2(MARGIN + columnCount);
            anchor.setRow1(lastRow.getRowNum());
            anchor.setRow2(lastRow.getRowNum() + 1);

            resize(sheet.createDrawingPatriarch()
                    .createPicture(anchor, sheet.getWorkbook()
                            .addPicture( Base64.getDecoder().decode(image.base64),
                                    Workbook.PICTURE_TYPE_PNG)));
        }

        private void resize(Picture picture) {
            double proportion = picture.getImageDimension().getHeight() / picture.getImageDimension().getWidth();
            float totalWidthInPixels = 0;
            for (int i = MARGIN; i < columnCount + MARGIN; i++)
                totalWidthInPixels += sheet.getColumnWidthInPixels(i);

            lastRow.setHeightInPoints((float) (totalWidthInPixels * proportion * 72.0 / 96.0));
        }
    }

    abstract class Content {

        protected Row lastRow;
        protected Row nextRow() {
            lastRow = sheet.createRow(Math.max(sheet.getLastRowNum() + 1, MARGIN));
            return lastRow;
        }
        protected Row nextRow(TableExport.Cell cell, CellStyle style) {
            Row row = nextRow();
            nextCell(cell, style);
            return row;
        }
        protected Row nextRow(String value, CellStyle style) {
            return nextRow(TableExport.Cell.builder()
                    .value(value)
                    .colspan(columnCount)
                    .build(), style);
        }

        protected Cell lastCell;
        protected Cell nextCell(TableExport.Cell cell, CellStyle style) {

            lastCell = lastRow.createCell(Math.max(lastRow.getLastCellNum(), MARGIN));
            lastCell.setCellStyle(style);

            if (cell == null)
                return lastCell;

            lastCell.setCellValue(cell.value);

            if (cell.colspan != null)
                setColspan(lastCell, cell.colspan);

            if (cell.rowspan != null)
                setRowspan(lastCell, cell.rowspan);

            return lastCell;
        }

        private void setColspan(Cell cell, int colspan) {
            if (colspan > 1) {
                sheet.addMergedRegion(new CellRangeAddress(
                        cell.getRowIndex(),
                        cell.getRowIndex(),
                        cell.getColumnIndex(),
                        cell.getColumnIndex() + colspan - 1
                ));
            }
        }

        private void setRowspan(Cell cell, int rowspan) {
            if (rowspan > 1) {
                sheet.addMergedRegion(new CellRangeAddress(
                        cell.getRowIndex(),
                        cell.getRowIndex() + rowspan - 1,
                        cell.getColumnIndex(),
                        cell.getColumnIndex()
                ));
            }
        }
    }
}
