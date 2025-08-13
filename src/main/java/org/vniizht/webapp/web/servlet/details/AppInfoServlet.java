package org.vniizht.webapp.web.servlet.details;

import org.vniizht.webapp.model.AppInfo;
import org.vniizht.webapp.web.servlet.Util;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/appinfo")
public class AppInfoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Util.writeJson(new AppInfo(), response);
    }
}
