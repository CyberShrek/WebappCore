package org.vniizht.webapp.web.servlet.report;

import org.vniizht.webapp.exception.HttpException;
import org.vniizht.webapp.jdbc.Reports;
import org.vniizht.webapp.web.servlet.Util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/report")
public class ReportServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, HttpException {
        String reportId = request.getHeader("Report-Id");
        Map<String, Object> formValues = Util.parseJsonBody(request, response);
        try {
            Util.writeJson(
                    reportId.equals("sales") ? Reports.getSalesData(formValues)
                            : reportId.equals("deps") ? Reports.getDepsData(formValues)
                            : null,
                    response);
        } catch (SQLException e) {
            throw new HttpException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
