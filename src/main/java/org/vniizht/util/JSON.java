package org.vniizht.forge.webapp.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class JSON {

    public static Map<String, Object> parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, new TypeReference<Map<String, Object>>() {});
    }

    public static Map<String, Object> parse(InputStream jsonStream) throws IOException {
        return new ObjectMapper().readValue(jsonStream, new TypeReference<Map<String, Object>>() {});
    }

    public static String stringify(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }
}
