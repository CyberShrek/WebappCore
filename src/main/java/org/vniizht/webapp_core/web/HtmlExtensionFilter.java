package org.vniizht.webapp_core.web;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class HtmlExtensionFilter implements Filter {
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        String path = request.getServletPath();

        if (path.matches("^/[^.]*$")) {
            RequestDispatcher rd = request.getRequestDispatcher(path + ".html");
            rd.forward(req, res);
        } else {
            chain.doFilter(req, res);
        }
    }
}
