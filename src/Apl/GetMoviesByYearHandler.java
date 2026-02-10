package Apl;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class GetMoviesByYearHandler implements HttpHandler {
    Map<Integer, Map<Integer,String>> films;
    String jsonResponse;

    public GetMoviesByYearHandler(Map<Integer, Map<Integer,String>> films){
        this.films = films;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        Gson gson = new Gson();
        int year;
        String path = ex.getRequestURI().getQuery().split("=")[1];

        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");


        String query = ex.getRequestURI().getQuery();
        if (query == null || !query.contains("=")) {
            ex.sendResponseHeaders(400,0);

            jsonResponse = gson.toJson("Ошибка пути");
            try(OutputStream os = ex.getResponseBody()) {
                os.write(jsonResponse.getBytes());
            }
            return;
        }

        try {
            year = Integer.parseInt(path);

        } catch (NumberFormatException e) {
            ex.sendResponseHeaders(400,0);

            jsonResponse = gson.toJson("Ошибка форомата age не является числом ");
            try(OutputStream os = ex.getResponseBody()){
                os.write(jsonResponse.getBytes());
            }
            return;

        }


        if (!films.containsKey(Integer.parseInt(path))) {
            ex.sendResponseHeaders(400, 0);

            //jsonResponse = gson.toJson(new String[]{});
            jsonResponse = gson.toJson("Год не найден");
            try (OutputStream os = ex.getResponseBody()) {
                os.write(jsonResponse.getBytes());
            }
            return;
        }

        ex.sendResponseHeaders(200, 0);

        String JsonResponse = gson.toJson(films.get(Integer.parseInt(path)));
        try (OutputStream os = ex.getResponseBody()) {
            os.write(JsonResponse.getBytes());
        }

    }
}
