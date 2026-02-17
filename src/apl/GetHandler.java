package apl;

import server.BaseHttpHandler;
import server.Movie;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class GetHandler extends BaseHttpHandler {
    private List<Movie> films;

    public GetHandler(List<Movie> films) {
        this.films = films;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        String method = ex.getRequestMethod();
        String path = ex.getRequestURI().getPath();

        if (films == null || films.isEmpty()) {
            sendJson(ex,200, new ArrayList<>());
        } else {
            sendJson(ex,200, films);
        }
    }
}
