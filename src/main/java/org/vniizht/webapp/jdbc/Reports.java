package org.vniizht.webapp.jdbc;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class Reports {

    public static List<List<Object>> getSalesData(Map<String, Object> formValues) throws SQLException {

        List<String> period = (List<String>) formValues.get("period");
        boolean hasPeriodDetailing = (boolean) formValues.get("periodDetailing");

        String query = "SELECT " +
                (hasPeriodDetailing ? "operation_date,\n" : "") +
//                "       p25,\n" +
//                "       p51,\n" +
                "       sum(p33) as quantity,\n" +
                "       sum(p36) as income,\n" +
                "       sum(p44) as lost_income,\n" +
                "       sum(CASE WHEN p21 = '8' THEN p36 ELSE 0 END) as colection_sum,\n" +
                "       sum(CASE WHEN p21 = '6' THEN p46 ELSE 0 END) as luggage_income\n" +
                "\n" +
                "FROM prigl3.co22_t1 t1 JOIN prigl3.co22_meta meta on t1.p2 = meta.t1_id\n" +
                "\n" +
                "WHERE operation_date between '" + period.get(0) + "' and '" + period.get(1) + "'\n" +
                (hasPeriodDetailing ? "group by " : "") +
                (hasPeriodDetailing ? "operation_date " : "");

        return SimpleJdbc.query(query);
    }

    public static List<List<Object>> getDepsData(Map<String, Object> formValues) throws SQLException {

        List<String> period = (List<String>) formValues.get("period");
        boolean hasPeriodDetailing = (boolean) formValues.get("periodDetailing");
        boolean hasTrainCategories = formValues.get("additional") != null && formValues.get("trainCategories") != null;

        return SimpleJdbc.query("SELECT " +
                (hasPeriodDetailing ? "ticket_begdate,\n" : "") +
                (hasTrainCategories ? "p19,\n" : "") +
                "       sum(CASE WHEN p21 = '2' OR p21 = '3' OR p21 = '4' OR p21 = '5' THEN p36 ELSE 0 END) as quantity,\n" +
                "       avg(CASE WHEN p21 = '2' OR p21 = '3' OR p21 = '4' OR p21 = '5' THEN 0 ELSE 0 END) as dist,\n" +
                "       sum(CASE WHEN p21 = '2' OR p21 = '3' OR p21 = '4' OR p21 = '5' THEN 0 ELSE 0 END) as passkm\n" +
                "\n" +
                "FROM prigl3.co22_t1 t1 JOIN prigl3.co22_meta meta on t1.p2 = meta.t1_id\n" +
                "\n" +
                "WHERE ticket_begdate between '" + period.get(0) + "' and '" + period.get(1) + "'\n" +
                (hasPeriodDetailing || hasTrainCategories ? "group by " : "")
                + (hasPeriodDetailing ? "ticket_begdate" + (hasTrainCategories ? ", " : "") : "")
                + (hasTrainCategories ? "p19" : ""));
    }
}
