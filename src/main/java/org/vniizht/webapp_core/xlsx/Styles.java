package org.vniizht.webapp_core.xlsx;

import org.apache.poi.ss.usermodel.*;

public class Styles {

    public final static int MARGIN = 1;
    public final static int TABLE_CELL_HEIGHT_IN_POINTS = 25;


    public final static String
            FONT_NAME = "Arial Cyr";

    public final static BorderStyle
            BASE_BORDER     = BorderStyle.THIN,
            OUTER_BORDER    = BorderStyle.THICK;

    public final static      IndexedColors
            BASE_FONT_COLOR = IndexedColors.BLACK,
            BLANK_COLOR     = IndexedColors.WHITE,
            ACCENT_COLOR    = IndexedColors.DARK_BLUE,
            BORDER_COLOR    = IndexedColors.BLUE_GREY;

    private final Workbook  workbook;

    public final CellStyle blank;
    public final CellStyle tableBody;
    public final CellStyle tableHead;
    public final CellStyle tableFoot;
    public final CellStyle imageTitle;
    public final CellStyle italic;
    public final CellStyle topHeader;
    public final CellStyle sectionTitle;
    public final CellStyle field;
    public final BorderStyle outerBorder;

    public Styles(Workbook workbook) {
        this.workbook = workbook;

        blank = createStyle(BLANK_COLOR, null, null);

        tableBody = createStyle(BASE_BORDER,
                createFont(10));
        tableBody.setVerticalAlignment(VerticalAlignment.TOP);

        tableHead = createStyle(ACCENT_COLOR, BASE_BORDER,
                createFont(10, BLANK_COLOR, FontModes.BOLD));
        tableHead.setAlignment(HorizontalAlignment.CENTER);
        tableHead.setVerticalAlignment(VerticalAlignment.CENTER);

        tableFoot = createStyle(BASE_BORDER,
                createFont(10, FontModes.BOLD));
        tableFoot.setVerticalAlignment(VerticalAlignment.TOP);

        imageTitle = createStyle(ACCENT_COLOR, BorderStyle.NONE,
                createFont(10, BLANK_COLOR, FontModes.BOLD));

        italic = createStyle(
                createFont(8, FontModes.ITALIC));

        topHeader = createStyle(
                createFont(11, FontModes.BOLD));

        sectionTitle = createStyle(
                createFont(11, ACCENT_COLOR, FontModes.BOLD));

        field = createStyle(
                createFont(10));

        CellStyle outerBorderStyle = workbook.createCellStyle();
        applyBorders(outerBorderStyle, OUTER_BORDER);
        outerBorder = outerBorderStyle.getBorderTop();
    }


    private CellStyle createStyle(IndexedColors background,
                                  BorderStyle border,
                                  Font font) {
        CellStyle style = workbook.createCellStyle();
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setVerticalAlignment(VerticalAlignment.BOTTOM);
        if (background != null)
            style.setFillForegroundColor(background.getIndex());
        if (font != null)
            style.setFont(font);
        if (border != null)
            applyBorders(style, border);

        return style;
    }
    private CellStyle createStyle(BorderStyle border,
                                  Font font){
        return createStyle(BLANK_COLOR, border, font);
    }
    private CellStyle createStyle(Font font){
        return createStyle(BLANK_COLOR, BorderStyle.NONE, font);
    }


    private Font createFont(int size,
                            IndexedColors color,
                            FontModes... fontModes) {
        Font font = workbook.createFont();
        font.setFontName(FONT_NAME);
        font.setFontHeightInPoints((short) size);
        font.setColor(color.getIndex());
        for (FontModes fontMode : fontModes) {
            switch (fontMode) {
                case BOLD: font.setBold(true); break;
                case ITALIC: font.setItalic(true); break;
                case UNDERLINE: font.setUnderline(Font.U_SINGLE);
            }
        }
        return font;
    }
    private Font createFont(int size,
                            FontModes... fontModes) {
        return createFont(size, BASE_FONT_COLOR, fontModes);
    }
    private Font createFont(int size) {
        return createFont(size, BASE_FONT_COLOR);
    }
    private enum FontModes {
        BOLD, ITALIC, UNDERLINE
    }


    private void applyBorders(CellStyle style, BorderStyle border) {
        short color = BORDER_COLOR.getIndex();
        style.setBorderTop(border);
        style.setTopBorderColor(color);
        style.setBorderRight(border);
        style.setRightBorderColor(color);
        style.setBorderBottom(border);
        style.setBottomBorderColor(color);
        style.setBorderLeft(border);
        style.setLeftBorderColor(color);
    }
}