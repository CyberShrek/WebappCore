package org.vniizht.security.usercheck;
import com.vniizht.ucheck.UserCheckRemote;
import org.vniizht.Properties;
import org.vniizht.exception.HttpException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.vniizht.Application.AppInfo;

class UserCheck {

    public final UserCheckRemote remote;

    UserCheck() throws NamingException {
        remote = (UserCheckRemote) new InitialContext().lookup(Properties.USER_CHECK_REMOTE_NAME);
    }

    public boolean applyRequest(HttpServletRequest request) throws HttpException {
        try {
            String code = AppInfo != null ? AppInfo.code : request.getHeader("Code");

            remote.setUser(request.getRemoteUser());
            remote.setStatTaskCode(code);
            remote.setTaskCode(code);
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