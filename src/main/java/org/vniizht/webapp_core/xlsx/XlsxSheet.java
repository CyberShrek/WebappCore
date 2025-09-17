package org.vniizht.webapp_core.xlsx;

import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.vniizht.webapp_core.model.export.Section;
import org.vniizht.webapp_core.model.export.Table;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

public class XlsxSheet {

    private final static String CONTEXT_TITLE = "АСУ \"ЭКСПРЕСС-НП\"";
    private final static String CONTEXT_ADDRESS = "'Контекст'!B2"; // Формат: 'Имя листа'!Ячейка
    private final static int MARGIN = 1;
    private final static int HEADER_HEIGHT_IN_POINTS = 25;
    private final static int COLUMN_WIDTH_IN_SYMBOLS = 20;

    private final Table table;
    private final XSSFSheet sheet;
    private final Styles styles;

    private final int columnsCount;
    private final List<CellRangeAddress> mergedRegions = new ArrayList<>();

    public XlsxSheet(Table table,
                     XSSFSheet sheet,
                     List<Section> context,
                     Styles styles) {
        this.table = table;
        this.sheet = sheet;
        this.styles = styles;

        // Определение количества столбцов по самой широкой строке
        columnsCount = Stream.concat(
                        table.head.stream(),
                        table.body.stream()
                )
                .mapToInt(row -> row.cells.size())
                .max()
                .orElse(0);

        // Контекст
        if (context != null)
            addContext(context);

        // Добавление супер-заголовка
        addSuperHeader();

        // Остальные строки
        addRows(table.head, styles.headerStyle, HEADER_HEIGHT_IN_POINTS);
        addRows(table.body, null, 0);

        applyMergedRegions();

        // Установка общей границы
        applyOuterBorders();

        resizeColumns(); // Передаём количество столбцов
    }

    private void addContext( List<Section> context){

        createSingleCellRow(new Table.Cell(CONTEXT_TITLE),
                styles.contextHeaderStyle, HEADER_HEIGHT_IN_POINTS);

        createSingleCellRow(new Table.Cell(new SimpleDateFormat("dd.MM.yyyy HH:mm")
                                .format(new Date())),
                styles.contextStyle);

        context.forEach(section -> {
            if (section.title != null && !section.title.isEmpty()) {
                createSingleCellRow(new Table.Cell(section.title),
                        styles.contextSectionStyle,
                        HEADER_HEIGHT_IN_POINTS);
            }
            section.fields.forEach(field -> {

                Table.Cell cell0 = new Table.Cell(Optional.ofNullable(field.title).orElse(""));
                Table.Cell cell1 = new Table.Cell(field.getSimpleValues());
                cell1.colspan = columnsCount - 1;
                XSSFRow xssfRow = createRow(
                        new Table.Row(Arrays.asList(
                                cell0,
                                cell1
                        )),
                        styles.contextFieldStyle);
                CreationHelper helper = sheet.getWorkbook().getCreationHelper();
                Hyperlink link = helper.createHyperlink(HyperlinkType.DOCUMENT);
                link.setAddress(CONTEXT_ADDRESS);
                xssfRow.getCell(xssfRow.getLastCellNum()-1).setHyperlink(link);
            });
        });

        createSingleCellRow(new Table.Cell(),
                styles.contextHeaderStyle);
    }

    private void addSuperHeader() {
        createSingleCellRow(new Table.Cell(table.title),
                styles.headerStyle, HEADER_HEIGHT_IN_POINTS);
    }

    private void addRows(List<Table.Row> rows, CellStyle style, int height) {
        rows.forEach(row -> createRow(row, style, height));
    }

    private XSSFRow createRow(Table.Row row, CellStyle style, int height) {
        if (row == null) {
            return null;
        }
        XSSFRow xssfRow = sheet.createRow(Math.max(MARGIN, sheet.getLastRowNum() + 1));
        if (height > 0) {
            xssfRow.setHeightInPoints(height);
        }
        row.cells.forEach(cell -> createCell(cell, style));
        return xssfRow;
    }
    private XSSFRow createRow(Table.Row row, CellStyle style) {
        return createRow(row, style, 0);
    }

    private XSSFRow createSingleCellRow(Table.Cell cell, CellStyle style, int height) {
        cell.colspan = columnsCount;
        return createRow(new Table.Row(Collections.singletonList(cell)), style, height);
    }
    private XSSFRow createSingleCellRow(Table.Cell cell, CellStyle style) {
        return createSingleCellRow(cell, style, 0);
    }

    private XSSFCell createCell(Table.Cell cell, CellStyle style) {
        XSSFRow xssfRow = sheet.getRow(sheet.getLastRowNum());
        XSSFCell xssfCell = xssfRow.createCell(getNextCellIndex(xssfRow));
        if (cell == null) {
            xssfCell.setCellStyle(styles.blankStyle);
        } else {
            if (cell.type != null) switch (cell.type) {
                case TEXT:
                    xssfCell.setCellType(CellType.STRING);
                    xssfCell.setCellValue(cell.value);
                    break;
                case NUMBER:
                    xssfCell.setCellType(CellType.NUMERIC);
                    xssfCell.setCellValue(Double.parseDouble(cell.value));
                    break;
                case BOOLEAN:
                    xssfCell.setCellType(CellType.BOOLEAN);
                    xssfCell.setCellValue(Boolean.parseBoolean(cell.value));
                    break;
            }
            else xssfCell.setCellValue(cell.value);

            xssfCell.setCellStyle(style != null ? style
                    : cell.total ? styles.totalStyle
                    : styles.baseStyle);

            if (cell.rowspan > 1 || cell.colspan > 1) {
                int rowSpan = Math.max(1, cell.rowspan);
                int colSpan = Math.max(1, cell.colspan);
                int rowIndex = xssfCell.getRowIndex();
                int colIndex = xssfCell.getColumnIndex();

                // Создаем объединенную область
                CellRangeAddress region = new CellRangeAddress(
                        rowIndex, rowIndex + rowSpan - 1,
                        colIndex, colIndex + colSpan - 1
                );
                mergedRegions.add(region);
            }
        }
        return xssfCell;
    }

    private int getNextCellIndex(XSSFRow xssfRow) {
        int index = Math.max(MARGIN, xssfRow.getLastCellNum());
        while (isCellCovered(xssfRow.getRowNum(), index)) {
            index++;
        }
        return index;
    }

    private boolean isCellCovered(int rowIndex, int colIndex) {
        for (CellRangeAddress region : mergedRegions) {
            if (region.isInRange(rowIndex, colIndex)) {
                return true;
            }
        }
        return false;
    }

    private void applyMergedRegions() {
        for (CellRangeAddress region : mergedRegions) {
            sheet.addMergedRegion(region);
            applyBordersToMergedRegion(region, sheet
                    .getRow(region.getFirstRow())
                    .getCell(region.getFirstColumn())
                    .getCellStyle());
        }
    }

    private void applyBordersToMergedRegion(CellRangeAddress region, CellStyle style) {
        RegionUtil.setBorderTop(        style.getBorderTop(),           region, sheet);
        RegionUtil.setBorderBottom(     style.getBorderBottom(),        region, sheet);
        RegionUtil.setBorderLeft(       style.getBorderLeft(),          region, sheet);
        RegionUtil.setBorderRight(      style.getBorderRight(),         region, sheet);
        RegionUtil.setTopBorderColor(   style.getTopBorderColor(),      region, sheet);
        RegionUtil.setBottomBorderColor(style.getBottomBorderColor(),   region, sheet);
        RegionUtil.setLeftBorderColor(  style.getLeftBorderColor(),     region, sheet);
        RegionUtil.setRightBorderColor( style.getRightBorderColor(),    region, sheet);
    }

    private void applyOuterBorders() {

        int firstRow = MARGIN;
        int lastRow = sheet.getLastRowNum();
        int firstCol = MARGIN;
        int lastCol = columnsCount - 1 + MARGIN;

        // Проверяем валидность диапазона
        if (lastRow < firstRow || lastCol < firstCol) {
            System.err.println(
                    "Invalid merged region: " + firstRow + " " + lastRow + " " + firstCol + " " + lastCol
            );
            return;
        }

        CellRangeAddress fullRegion = new CellRangeAddress(
                firstRow,
                lastRow,
                firstCol,
                lastCol
        );
        applyBordersToMergedRegion(fullRegion, styles.outerBorderStyleOnly);
    }

    private void resizeColumns() {
        for (int i = MARGIN; i < columnsCount + MARGIN; i++) {
            sheet.setColumnWidth(i, COLUMN_WIDTH_IN_SYMBOLS * 256); // Фиксированная ширина
        }
    }
}
