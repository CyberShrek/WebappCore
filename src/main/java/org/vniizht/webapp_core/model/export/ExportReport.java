package org.vniizht.webapp_core.model.export;

import java.util.ArrayList;
import java.util.List;

public class ExportReport {
    public String title;
    public final List<Section> context = new ArrayList<>();
    public final List<Table>   tables  = new ArrayList<>();
}