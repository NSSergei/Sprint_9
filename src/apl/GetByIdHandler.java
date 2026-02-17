package apl;
import server.BaseHttpHandler;
import server.ErrorResponse;
import server.Movie;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.List;





public class GetByIdHandler extends BaseHttpHandler {
    private List<Movie> films;

    public GetByIdHandler(List<Movie> films) {
        this.films = films;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {

        if(!ex.getRequestMethod().equalsIgnoreCase("GET")) {
            sendJson(ex,405, new ErrorResponse("Ошибка метода",405));
            return;
        }
        Gson gson = new Gson();
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        String path = ex.getRequestURI().getPath();
        String[] parts = path.split("/");

        if(parts.length != 3 || !path.contains("movies/")) {
            sendJson(ex,400, new ErrorResponse("id промущен формат : get/{id}",400));
            return;
        }

        String index = parts[2];
        Integer id ;


        try {
            id = Integer.valueOf(index);
        } catch (NumberFormatException e) {
            sendJson(ex,400, new ErrorResponse("Ошибка типа данных id",400));
            return;
        }


        Movie filmsId = null;

        for(Movie film : films) {
            if(film.getId() == id) {
                filmsId = film;
            }
        }

        if (filmsId == null) {
            sendJson(ex, 404, new ErrorResponse( "Фильм с данным ID не найден", 404));
            return;
        }


        sendJson(ex, 200, "Фильм по id-" + filmsId.getId() +  " называется: " + filmsId.getTitle());
    }
}
