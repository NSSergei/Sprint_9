
package apl;

import server.BaseHttpHandler;
import server.ErrorResponse;
import server.Movie;
import com.google.gson.*;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostHandler extends BaseHttpHandler {
    private List<Movie> films;
    public PostHandler(List<Movie> films) {
        this.films = films;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        Gson gson = new Gson();
        ex.getResponseHeaders().set("ContentType","application/json; charset=UTF-8");

        String contentType = ex.getRequestHeaders().getFirst("Content-Type");

        if(contentType == null || !contentType.contains("application/json")){
            sendJson(ex,400,"");
        }

        String method = ex.getRequestMethod();

        if(!method.equalsIgnoreCase("POST")){
            sendJson(ex,405,"Ошибка метода");
            return;
        }

        InputStream requestBody = ex.getRequestBody();
        String body = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);

        JsonElement jsonElement  = JsonParser.parseString(body);

        if(!jsonElement.isJsonObject()) {
            sendJson(ex,400,"Тип объекта не Json");
            return;

        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        if (!jsonObject.has("name") || !jsonObject.has("year")) {
            sendJson(ex,400,new ErrorResponse("Поля name или year отсутствуют",400));
            return;

        }

        String name = jsonObject.get("name").getAsString();
        int year = jsonObject.get("year").getAsInt();

        List<String> details = new ArrayList<>();

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
            sendJson(ex,422,new ErrorResponse("Ошибка валидации: " + details,422));
            return;
        }


        int id = films.size() + 1;
        films.add(new Movie(id ,name,year));

        sendJson(ex,201,films);
        ex.sendResponseHeaders(201, 0);
    }
}
