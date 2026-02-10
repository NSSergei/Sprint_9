package Apl;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import com.google.gson.Gson;

public class GetHandler implements HttpHandler {
    private Map<Integer,Map<Integer,String>>  films;

    public GetHandler(Map<Integer,Map<Integer,String>> films) {
        this.films = films;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        String method = ex.getRequestMethod();
        Gson gson = new Gson();
        String result;

        if (method.equalsIgnoreCase("GET")) {
            if (films == null || films.isEmpty()) {
                result = "[]";
            } else {
                result  = gson.toJson(films);
            }

            ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            ex.sendResponseHeaders(200, 0);
            try (OutputStream os = ex.getResponseBody()) {
                os.write(result.getBytes());
            }

        } else {
            ex.sendResponseHeaders(401, -1);
        }
    }
}
