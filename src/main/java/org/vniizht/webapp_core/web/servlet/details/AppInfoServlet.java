package org.vniizht.webapp_core.web.servlet.details;

import org.vniizht.webapp_core.model.AppInfo;
import org.vniizht.webapp_core.web.servlet.SimpleHttp;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/appinfo")
public class AppInfoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SimpleHttp.writeJson(new AppInfo(), response);
    }
}
