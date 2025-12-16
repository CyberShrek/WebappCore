package org.vniizht.webapp_core.web;

import org.vniizht.webapp_core.Application;
import org.vniizht.webapp_core.Mapping;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PageFilter implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String path = ((HttpServletRequest) request).getRequestURI();
        String mapping = path.replaceFirst(Application.CONTEXT_ROOT, "");
//        if (Mapping.has(mapping)) {
//            String page = Mapping.getPage(mapping);
//            if (page != null)
//                ((HttpServletResponse) response).sendRedirect(page);
//        }
        if (Mapping.has(mapping)) {
            String page = Mapping.getPage(mapping);
            if (page != null)
                request.getRequestDispatcher(Mapping.getPage(mapping)).forward(request, response);
        }
        else chain.doFilter(request, response);
    }
}
