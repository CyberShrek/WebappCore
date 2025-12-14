package org.vniizht.webapp_core.model.export.report;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use      = JsonTypeInfo.Id.NAME,
        include  = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ImageExport.class, name = "image"),
        @JsonSubTypes.Type(value = TableExport.class, name = "table")
})
public abstract class ReportExport {
    public String title;
}
