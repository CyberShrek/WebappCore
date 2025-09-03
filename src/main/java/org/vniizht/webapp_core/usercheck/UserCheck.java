package org.vniizht.webapp_core.usercheck;
import com.vniizht.ucheck.UserCheckRemote;
import org.vniizht.webapp_core.Properties;
import org.vniizht.webapp_core.exception.HttpException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;


class UserCheck {

    public final UserCheckRemote remote;

    UserCheck() throws NamingException {
        remote = (UserCheckRemote) new InitialContext().lookup(Properties.USER_CHECK_REMOTE_NAME);
    }

    public boolean applyRequest(HttpServletRequest request) throws HttpException {
        try {
            remote.setUser(request.getRemoteUser());
            remote.setStatTaskCode(Properties.APPLICATION_CODE);
            remote.setTaskCode(Properties.APPLICATION_CODE);
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
        return Optional.ofNullable(request.getHeader("X-Real-IP"))
                .orElse(Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                        .orElse(request.getRemoteAddr()));
    }
}