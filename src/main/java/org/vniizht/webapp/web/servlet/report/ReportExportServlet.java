package org.vniizht.webapp.web.servlet.report;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.vniizht.webapp.model.Report;
import org.vniizht.webapp.xlsx.XlsxReport;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet("/report/export")
public class ReportExportServlet extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);

        // Десериализация JSON
        Report report = objectMapper.readValue(
                request.getInputStream(),
                Report.class
        );

        // Настройка HTTP-ответа
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + report.title + ".xlsx");

        // Запись файла в выходной поток
        try (XlsxReport xlsxReport = new XlsxReport(report);
             OutputStream out = response.getOutputStream()) {
            xlsxReport.write(out);
            out.flush();
        }
    }
}