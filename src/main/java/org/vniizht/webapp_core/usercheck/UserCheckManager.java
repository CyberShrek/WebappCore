package org.vniizht.webapp_core.usercheck;

import org.vniizht.webapp_core.model.UserInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

public class UserCheckManager {

    static final HashMap<String, UserCheckSession> sessionsMap = new HashMap<>(); // Key is IP

    public static synchronized boolean check(HttpServletRequest request) throws Exception {
        return getSession(UserCheck.extractIp(request))
                .userCheck
                .applyRequest(request);
    }

    public static synchronized UserInfo getUser(HttpServletRequest request) throws Exception {
        UserCheck userCheck = getSession(UserCheck.extractIp(request)).userCheck;
        userCheck.applyRequest(request);
        return new UserInfo(userCheck.remote);
    }

    private synchronized static UserCheckSession getSession(String ip) throws Exception {
        UserCheckSession session = sessionsMap.get(ip);
        if (session == null) {
            session = createSession(ip);
        }
        session.prolong();
        return session;
    }

    private synchronized static UserCheckSession createSession(String ip) throws Exception {
        UserCheckSession session = new UserCheckSession(ip);
        sessionsMap.put(ip, session);
        return session;
    }
}
