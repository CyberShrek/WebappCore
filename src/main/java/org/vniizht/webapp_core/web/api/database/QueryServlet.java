package org.vniizht.webapp_core.web.api.database;

import org.vniizht.webapp_core.exception.HttpException;
import org.vniizht.webapp_core.jdbc.SimpleJdbc;
import org.vniizht.webapp_core.web.api.SimpleHttp;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/api/query")
public class QueryServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String queryId = request.getHeader("Query-Id");
            if (!SimpleJdbc.isQueryExists(queryId)) {
                throw new HttpException(HttpServletResponse.SC_NOT_FOUND, "Query not found: " + queryId);
            }
            Map<String, Object> formValues = SimpleHttp.parseJsonBody(request, response);
            SimpleHttp.writeJson(
                    SimpleJdbc.queryWithParams(queryId, formValues),
                    response);

        } catch (Exception e) {
            SimpleHttp.handleException(e, response);
        }
    }
}
