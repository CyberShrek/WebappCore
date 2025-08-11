package org.vniizht.model;

import org.vniizht.Application;
import org.vniizht.Properties;
import org.vniizht.prilinfo.PrilInfoRemote;

import javax.naming.InitialContext;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

public class AppInfo implements Serializable {

    public final String code = Properties.APPLICATION_CODE;
    Map<String, Object> prilInfo            = getPrilInfo(code);
    public final String name                = safeGet("zadname");
    public final String contextRoot         = safeGet("comstr");
    public final String groupName           = safeGet("zadnameV");
    public final String groupPath           = safeGet("comstrV");
    public final String version             = safeGet("version");
    public final String releaseDate         = safeGet("releaseDate");
    public final String updateDate          = Optional.ofNullable(Application.LAUNCH_DATE).orElse(java.time.LocalDate.now()).toString();
    public final String technologistName    = safeGet("fio");
    public final String technologistPhone   = safeGet("tel");
    public final String technologistMail    = safeGet("email");
    public final String helpPath            = safeGet("helpstr");
    public final String comment             = safeGet("comment");
    public final String[] tables            = safeGetArray("pril_tables");
    public final String instructionPath     = safeGet("instrstr");

    public AppInfo() {
    }

    private static Map<String, Object> getPrilInfo(String code) {
        try {
            PrilInfoRemote remote = (PrilInfoRemote) new InitialContext()
                    .lookup(Properties.PRIL_INFO_REMOTE_NAME);

            return remote.info(code);
        } catch (Exception e) {
            throw new RuntimeException("PrilInfo error", e);
        }
    }

    private String safeGet(String key) {
        return prilInfo.getOrDefault(key, "").toString();
    }

    private String[] safeGetArray(String key) {
        Object val = prilInfo.getOrDefault(key, new String[]{""});
        return val instanceof String[] ? (String[]) val : new String[]{""};
    }
}