package org.vniizht.webapp_core.web.servlet.database;

import org.vniizht.webapp_core.exception.HttpException;
import org.vniizht.webapp_core.jdbc.SimpleJdbc;
import org.vniizht.webapp_core.web.servlet.Util;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/query")
public class QueryServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, HttpException {
        String queryId = request.getHeader("Query-Id");
        Map<String, Object> formValues = Util.parseJsonBody(request, response);
        try {
            Util.writeJson(
                    SimpleJdbc.queryWithParams(queryId, formValues),
                    response);
        } catch (SQLException e) {
            throw new HttpException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
