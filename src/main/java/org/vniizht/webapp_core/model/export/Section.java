package org.vniizht.webapp_core.model.export;

import java.util.ArrayList;
import java.util.List;

public class Section {
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
