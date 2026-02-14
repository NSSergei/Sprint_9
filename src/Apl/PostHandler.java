    package Apl;

    import Server.Movie;
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

    public class PostHandler implements HttpHandler {
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
                ex.sendResponseHeaders(400,0);
            }

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

            int id = films.size() + 1;

            films.add(new Movie(id,name,year));

            ex.sendResponseHeaders(201, 0);
            try (OutputStream os = ex.getResponseBody()) {
                String jsonResponse = gson.toJson(films);
                os.write(jsonResponse.getBytes());
            }
        }
    }

