package org.vniizht.forge.webapp.model;

import com.fasterxml.jackson.databind.JsonNode;
import org.vniizht.forge.webapp.Application;
import org.vniizht.forge.webapp.Properties;
import org.vniizht.prilinfo.PrilInfoRemote;

import javax.naming.InitialContext;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

public class Webapp implements Serializable {

    public final String code;
    public JsonNode frontend;
    public String dataSource;

    @Override
    public String toString() {
        return "code: " + code + ", frontend: " + frontend + ", dataSource: " + dataSource;
    }

    public Webapp(String code, JsonNode frontend, String dataSource) {
        this(code);
        this.frontend = frontend;
        this.dataSource = dataSource;
    }

    public Webapp(String code) {
        this.code = code;
    }

    public Info getInfo() {
        return new Info(code);
    }

    public static class Info {
        public final String name;              // название
        public final String contextRoot;       // путь на сервере приложений
        public final String groupName;         // название приложения верхнего ур.
        public final String groupPath;         // путь к приложению верхнего ур.
        public final String version;           // версия
        public final String releaseDate;       // дата выхода
        public final String updateDate;        // дата последнего обновления
        public final String technologistName;  // имя технолога
        public final String technologistPhone; // телефон технолога
        public final String technologistMail;  // почта технолога
        public final String helpPath;          // путь к помощи
        public final String comment;           // сообщение о ЕСПП
        public final String[] tables;          // список таблиц
        public final String instructionPath;   // Путь к руководству пользователя

        public Info(String code) {
            Map<String, Object> prilInfo = getPrilInfo(code);
            this.name             = safeGet(prilInfo, "zadname");
            this.groupName        = safeGet(prilInfo, "zadnameV");
            this.contextRoot = safeGet(prilInfo, "comstr");
            this.groupPath        = safeGet(prilInfo, "comstrV");
            this.version          = safeGet(prilInfo, "version");
            this.releaseDate      = safeGet(prilInfo, "datan");
            this.updateDate       = Optional.ofNullable(Application.LAUNCH_DATE).orElse(java.time.LocalDate.now()).toString();
            this.technologistName = safeGet(prilInfo, "fio");
            this.technologistPhone= safeGet(prilInfo, "tel");
            this.technologistMail = safeGet(prilInfo, "email");
            this.helpPath         = safeGet(prilInfo, "helpstr");
            this.comment          = safeGet(prilInfo, "comment");
            this.tables           = safeGetArray(prilInfo, "pril_tables");
            this.instructionPath  = safeGet(prilInfo, "helpstr");
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

        private static String safeGet(Map<String, Object> map, String key) {
            return map.getOrDefault(key, "").toString();
        }

        private static String[] safeGetArray(Map<String, Object> map, String key) {
            Object val = map.getOrDefault(key, new String[]{""});
            return val instanceof String[] ? (String[]) val : new String[]{""};
        }
    }
}