package org.vniizht.forge.webapp.web.servlet;

import org.vniizht.forge.webapp.util.Resources;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@WebServlet
public class IndexServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        System.out.println("doGet: " + path);;
        if (path.startsWith("/resources/")) {
            response.sendError(HttpServletResponse.SC_CONFLICT);
            return;
        }

        Util.writeHtml(Resources.read("static/forged.html"), response);
    }
}
