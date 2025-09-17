package org.vniizht.webapp_core.model.export;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.ArrayList;
import java.util.List;

public class Table {
    public String title;
    public final List<Row> head = new ArrayList<>();
    public final List<Row> body = new ArrayList<>();

    public Table() {}

    public Table(String title) {
        this.title = title;
    }

    public static class Row {
        public final List<Cell> cells;

        public Row() {
            cells = new ArrayList<>();
        }

        public Row(List<Cell> cells) {
            this.cells = cells;
        }
    }

    public static class Cell {
        public String value;
        public ColumnType type;
        public int colspan;
        public int rowspan;
        public boolean total;

        public Cell() {}

        public Cell(String value) {
            this.value = value;
        }
    }

    public enum ColumnType {
        TEXT, NUMBER, BOOLEAN;

        @JsonCreator
        public static ColumnType fromString(String value) {
            return valueOf(value.toUpperCase());
        }
    }
}
