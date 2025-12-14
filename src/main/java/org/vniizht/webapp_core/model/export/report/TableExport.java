package org.vniizht.webapp_core.model.export.report;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
public class TableExport extends ReportExport {
    public List<ColumnType> types;
    public List<List<Cell>> head;
    public List<List<Cell>> body;

    public enum ColumnType {
        STRING, NUMBER, BOOLEAN;

        @JsonCreator
        public static ColumnType fromString(String value) {
            return valueOf(value.toUpperCase());
        }
    }

    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Cell {
        public String  value;
        public Integer colspan;
        public Integer rowspan;
    }
}
