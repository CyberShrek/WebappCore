package org.vniizht.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.ArrayList;
import java.util.List;

public class Report {

    public String title;
    public final List<Section> context = new ArrayList<>();
    public final List<Table>   tables  = new ArrayList<>();

    public static class Section {
        public String title;
        public final List<Field> fields;

        public Section(String title) {
            this(title, new ArrayList<>());
        }
        public Section(String title, List<Field> fields) {
            this.title = title;
            this.fields = fields;
        }

        public static class Field {
            public String title;
            public final List<String> values;
            public boolean pickedAll;

            public Field(String title) {
                this(title, new ArrayList<>());
            }
            public Field(String title, List<String> values) {
                this.title = title;
                this.values = values;
            }

            public String getSimpleValues() {
                StringBuilder builder = new StringBuilder();
                builder.append(pickedAll ? "выбрано всё" : "");
                if (!pickedAll && values != null && !values.isEmpty()) {
                    int size = Math.min(5, values.size());
                    builder.append(String.join(", ", values.subList(0, size)));
                    if (values.size() > size) {
                        builder.append(" и ")
                                .append(values.size() - size)
                                .append(" других");
                    }
                }
                return builder.toString();
            }
        }
    }

    public static class Table {
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
}