package apl;

import server.BaseHttpHandler;
import server.ErrorResponse;
import server.Movie;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.List;


public class GetMoviesByYearHandler extends BaseHttpHandler {
    List<Movie> films;

    public GetMoviesByYearHandler(List<Movie> films) {
        this.films = films;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {

        if (!ex.getRequestMethod().equalsIgnoreCase("GET")) {
            sendJson(ex,405,"");
            return;
        }

        Gson gson = new Gson();
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

        String query = ex.getRequestURI().getQuery();


        if (query == null || query.equals("year=")) {
            sendJson(ex,400,new ErrorResponse("Неверный параметр запроса путь " +
                    "http://localhost:9000/movies?year={год}",400));
            return;
        }

        int year;

        try {
            year = Integer.parseInt(query.split("=")[1]);
        } catch (NumberFormatException e) {
            sendJson(ex,400,new ErrorResponse("Ошибка формата: year не является числом",400));
            return;
        }

        List<Movie> sameYearsFilms = new ArrayList<>();

        for (Movie film : films) {
            if (film.getYear() == year) {
                sameYearsFilms.add(film);
            }
        }

        if (sameYearsFilms.isEmpty()) {
            sendJson(ex,400,new ErrorResponse("Фильмы с данным годом отсутствует в списке",400));
            return;
        }

        sendJson(ex,200,sameYearsFilms);
        ex.sendResponseHeaders(200, 0);
    }
}