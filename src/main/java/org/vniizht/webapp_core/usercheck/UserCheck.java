package org.vniizht.webapp_core.usercheck;
import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.vniizht.ucheck.UserCheckRemote;
import org.vniizht.webapp_core.Application;
import org.vniizht.webapp_core.Mapping;
import org.vniizht.webapp_core.exception.HttpException;
import org.vniizht.webapp_core.web.api.SimpleHttp;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;


class UserCheck {

    public final UserCheckRemote remote;

    UserCheck() throws NamingException {
        remote = (UserCheckRemote) new InitialContext().lookup(Application.USER_CHECK_REMOTE_NAME);
    }

    public boolean applyRequest(HttpServletRequest request) throws HttpException {
        try {
            remote.setUser(request.getRemoteUser());
            remote.setStatTaskCode(SimpleHttp.getAppCode(request));
            remote.setTaskCode(SimpleHttp.getAppCode(request));
            remote.setIp(extractIp(request));
            return remote.check();
        } catch (Exception e) {
            throw new HttpException(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        }
    }

    public void exit() throws Exception {
        remote.SaveTimeExit();
        remote.remove();
    }

    public static String extractIp(HttpServletRequest request) {
        return new String(JsonStringEncoder
                .getInstance()
                .quoteAsString(
                        Optional.ofNullable(request.getHeader("X-Real-IP"))
                                .orElse(Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                                .orElse(request.getRemoteAddr()))));
    }
}