package org.vniizht.forge.webapp.security;

import org.vniizht.forge.webapp.exception.HttpException;

import java.util.HashSet;
import java.util.Set;

public class SqlValidator {

    private static final Set<String> whiteSqlSet = new HashSet<>();

    public static boolean isTrusted(String expression) throws HttpException {
//        if(whiteSqlSet.contains(expression))
            return true;
//        else
//            throw new HttpException(HttpServletResponse.SC_FORBIDDEN, "Ты правда думаешь, что меня так просто взломать?");
    }

    public static boolean isTrusted(String[] expressions) throws HttpException {
        for(String expression : expressions)
            isTrusted(expression);
        return true;
    }

    public static boolean checkForInjections(String expression) {
        // TODO
        return true;
    }
}