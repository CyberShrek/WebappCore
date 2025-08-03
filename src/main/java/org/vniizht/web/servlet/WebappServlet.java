package org.vniizht.forge.webapp.web.servlet;

import org.vniizht.forge.webapp.model.Webapp;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static org.vniizht.forge.webapp.Application.WEBAPP;

@WebServlet("/webapp")
public class WebappServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (Security.checkUser(req, resp)) {
            Util.writeJson(
                    req.getHeader("Code") != null ? new Webapp(req.getHeader("Code")) : WEBAPP,
                    resp
            );
        }
    }
}