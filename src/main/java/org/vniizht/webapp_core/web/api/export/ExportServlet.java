package org.vniizht.webapp_core.web.api.export;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.vniizht.webapp_core.model.export.DocumentExport;
import org.vniizht.webapp_core.xlsx.XlsxDocument;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet("/api/export")
public class ExportServlet extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);

        // Десериализация JSON
        DocumentExport report = objectMapper.readValue(
                request.getInputStream(),
                DocumentExport.class
        );

        // Настройка HTTP-ответа
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment");

        // Запись файла в выходной поток
        try (XlsxDocument xlsxDocument = new XlsxDocument(report);
             OutputStream out = response.getOutputStream()) {
            xlsxDocument.write(out);
            out.flush();
        }
    }
}