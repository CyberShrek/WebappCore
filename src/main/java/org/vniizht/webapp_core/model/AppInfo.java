package org.vniizht.webapp_core.model;

import org.vniizht.webapp_core.Application;
import org.vniizht.prilinfo.PrilInfoRemote;

import javax.naming.InitialContext;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AppInfo {

    public final Map<String, Object> prilInfo;
    public final String name;
    public final String contextRoot;
    public final String groupName;
    public final String groupPath;
    public final String version;
    public final String releaseDate;
    public final String updateDate;
    public final String technologistName;
    public final String technologistPhone;
    public final String technologistMail;
    public final String helpPath;
    public final String comment;
    public final String[] tables;
    public final String instructionPath;

    public AppInfo(String code) {
        this.prilInfo = getPrilInfo(code);
        name                = safeGet("zadname");
        contextRoot         = safeGet("comstr");
        groupName           = safeGet("zadnameV");
        groupPath           = safeGet("comstrV");
        version             = safeGet("version");
        releaseDate         = safeGet("releaseDate");
        updateDate          = Optional.ofNullable(Application.LAUNCH_DATE).orElse(LocalDate.now()).toString();
        technologistName    = safeGet("fio");
        technologistPhone   = safeGet("tel");
        technologistMail    = safeGet("email");
        helpPath            = safeGet("helpstr");
        comment             = safeGet("comment");
        tables              = safeGetArray("pril_tables");
        instructionPath     = safeGet("instrstr");
    }

    private static Map<String, Object> getPrilInfo(String code) {
        try {
            PrilInfoRemote remote = (PrilInfoRemote) new InitialContext()
                    .lookup(Application.PRIL_INFO_REMOTE_NAME);

            return Optional.ofNullable(remote.info(code)).orElse(new HashMap<>());
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