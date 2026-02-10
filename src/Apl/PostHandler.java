package Apl;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PostHandler implements HttpHandler {
    private Map<Integer,Map<Integer,String>> films;
    public PostHandler(Map<Integer,Map<Integer,String>> films) {
        this.films = films;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        Gson gson = new Gson();

        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        String method = ex.getRequestMethod();

        if(!method.equalsIgnoreCase("POST")){
            ex.sendResponseHeaders(405,0);

            String jsonResponse = gson.toJson("Ошибка метода");
            try(OutputStream os = ex.getResponseBody()){
                os.write(jsonResponse.getBytes());
            }
            return;
        }

        InputStream requestBody = ex.getRequestBody();
        String body = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);


        JsonElement jsonElement  = JsonParser.parseString(body);

        if(!jsonElement.isJsonObject()) {
            ex.sendResponseHeaders(400,0);

            String jsonResponse = gson.toJson("Тип объекта не Json");
            try (OutputStream os = ex.getResponseBody()){
                os.write(jsonResponse .getBytes());
            }
            return;

        }

        if (ex.getResponseHeaders().getFirst("Content-Type") == null) {
            ex.sendResponseHeaders(400, 0);

            String jsonResponse = gson.toJson("Неправильный Content-Type");
            try (OutputStream os = ex.getResponseBody()) {
                os.write(jsonResponse.getBytes());
            }
            return;
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        if (!jsonObject.has("name") || !jsonObject.has("year")) {
            ex.sendResponseHeaders(400, 0);

            String jsonResponse = gson.toJson("Поля name или year отсутствуют");
            try (OutputStream os = ex.getResponseBody()) {
                os.write(jsonResponse .getBytes());
            }
            return;

        }

        String name = jsonObject.get("name").getAsString();
        int id = jsonObject.get("id").getAsInt();
        int year = jsonObject.get("year").getAsInt();

        ArrayList<String> details = new ArrayList<>();

        if (name.isEmpty()) {
            details.add("название не должно быть пустым");
        }


        if (name.length() > 100) {
            details.add("превышение длины имени, максимальное количество символов — 100");
        }

        if (year < 1888 || year > 2026) {
            details.add("год должен быть между 1888 и 2026");
        }

        if (!details.isEmpty()) {
            ex.sendResponseHeaders(422, 0);
            JsonObject errorResponse = new JsonObject();
            errorResponse.addProperty("error", "Ошибка валидации");
            errorResponse.add("details", gson.toJsonTree(details));
            String jsonResponse = errorResponse.toString();
            try (OutputStream os = ex.getResponseBody()) {
                os.write(jsonResponse.getBytes());
            }
            return;
        }

        if(!films.containsKey(year)){
            films.put(year,new HashMap<Integer,String>());
        }
        films.get(year).put(id,name);

        String jsonResponse = gson.toJson(films);
        ex.sendResponseHeaders(200, 0);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(jsonResponse.getBytes());
        }
    }
}

