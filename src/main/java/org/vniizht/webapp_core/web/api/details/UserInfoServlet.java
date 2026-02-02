package org.vniizht.webapp_core.web.api.details;

import org.vniizht.webapp_core.exception.HttpException;
import org.vniizht.webapp_core.usercheck.UserCheckManager;
import org.vniizht.webapp_core.web.api.HandyHttp;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/userinfo")
public class UserInfoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            HandyHttp.writeJson(UserCheckManager.getUser(request), response);
        } catch (Exception e) {
            HandyHttp.handleException(new HttpException(HttpServletResponse.SC_FORBIDDEN, e.getMessage()), response);
        }
    }
}
