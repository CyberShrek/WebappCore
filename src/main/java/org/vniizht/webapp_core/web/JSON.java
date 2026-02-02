package org.vniizht.webapp_core.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class JSON {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static Map<String, Object> parse(String json) throws JsonProcessingException {
        return mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
    }

    public static Map<String, Object> parse(InputStream jsonStream) throws IOException {
        return mapper.readValue(jsonStream, new TypeReference<Map<String, Object>>() {});
    }

    public static String stringify(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }
}
