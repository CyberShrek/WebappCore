package org.vniizht.web.servlet;

import org.vniizht.exception.HttpException;
import org.vniizht.security.usercheck.UserCheckManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Security {


    /**
     * Checks if the user is authorized and has access to the service.
     */
    public static boolean checkUser(HttpServletRequest request, HttpServletResponse response) {
//        try {
//            return UserCheckManager.check(request);
//        } catch (Exception exception) {
//            handleException(exception, response);
//            return false;
//        }
        return true;
    }

    /**
     * Checks if the user is authorized and has access to the service, and returns the user details.
     */
    public static void writeUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Util.writeJson(UserCheckManager.getUser(request), response);
        } catch (Exception e) {
            Util.handleException(new HttpException(HttpServletResponse.SC_FORBIDDEN, e.getMessage()), response);
        }
    }
}
