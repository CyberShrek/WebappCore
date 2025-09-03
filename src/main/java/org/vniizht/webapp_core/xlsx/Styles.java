package org.vniizht.webapp_core.xlsx;

import org.apache.poi.ss.usermodel.*;

public class Styles {

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

    public final CellStyle blankStyle;
    public final CellStyle baseStyle;
    public final CellStyle headerStyle;
    public final CellStyle totalStyle;
    public final CellStyle contextStyle;
    public final CellStyle contextHeaderStyle;
    public final CellStyle contextSectionStyle;
    public final CellStyle contextFieldStyle;
    public final CellStyle outerBorderStyleOnly;

    public Styles(Workbook workbook) {
        this.workbook = workbook;

        blankStyle = createStyle(BLANK_COLOR, null, null);

        baseStyle = createStyle(BASE_BORDER,
                createFont(10));
        baseStyle.setVerticalAlignment(VerticalAlignment.TOP);

        headerStyle = createStyle(ACCENT_COLOR, BASE_BORDER,
                createFont(10, BLANK_COLOR, FontModes.BOLD));
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        totalStyle  = createStyle(BASE_BORDER,
                createFont(10, FontModes.BOLD));

        contextStyle = createStyle(
                createFont(8, FontModes.ITALIC));

        contextHeaderStyle = createStyle(
                createFont(11, FontModes.BOLD));

        contextSectionStyle = createStyle(
                createFont(11, ACCENT_COLOR, FontModes.BOLD));

        contextFieldStyle = createStyle(
                createFont(10));

        outerBorderStyleOnly = workbook.createCellStyle();
        applyBorders(outerBorderStyleOnly, OUTER_BORDER);
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