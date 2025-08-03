package org.vniizht.forge.webapp.web.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.vniizht.forge.webapp.util.JSON;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class AILanguageModelClient {

    private static final String PATH = "https://llm.api.cloud.yandex.net/foundationModels/v1/completion";
    private static final String AUTHORIZATION = "Api-Key AQVNwSV8HYzjTH_is1l8G7wzzbpsKT3-Q0cYJANH";

    private static final String MODEL_URI = "gpt://b1g5vggvabnt2pg9ld1m/yandexgpt-lite";
    private static final Integer MAX_TOKENS = 4096;
    private static final Float TEMPERATURE = 0.3f;

    private final String system;

    public AILanguageModelClient(String system) {
        this.system = system;
    }

    public String ask(String prompt) throws Exception {
        return extractMessage(
                readResponse(
                        createHttpPostConnection(
                                createJson(prompt)
                        )));
    }

    private static HttpURLConnection createHttpPostConnection(String body) throws Exception {
        System.out.println("body: " + body);
        HttpURLConnection connection = (HttpURLConnection) new URL(PATH).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", AUTHORIZATION);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        connection.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
        return connection;
    }

    private String createJson(String prompt) throws JsonProcessingException {
        return "{\n" +
                "  \"modelUri\": \"" + MODEL_URI +"\",\n" +
                "  \"completionOptions\": {\n" +
                "    \"temperature\": " + TEMPERATURE + ",\n" +
                "    \"maxTokens\": " + MAX_TOKENS + "\n" +
                "  },\n" +
                "  \"messages\": [\n" +
                "    {\n" +
                "      \"role\": \"system\",\n" +
                "      \"text\": " + JSON.stringify(system) + "\n" +
                "    },\n" +
                "    {\n" +
                "      \"role\": \"user\",\n" +
                "      \"text\": " + JSON.stringify(prompt) + "\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }


    private String readResponse(HttpURLConnection connection) throws Exception {
        BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return response.toString();
    }

    private static String extractMessage(String response) {
        try {
            Map<String, Object> result             = (Map<String, Object>) JSON.parse(response).get("result");
            List<Map<String, Object>> alternatives = (List<Map<String, Object>>) result.get("alternatives");
            Map<String, Object> message            = (Map<String, Object>) alternatives.get(0).get("message");
            System.out.println((String) message.get("text"));
            return (String) message.get("text");
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
