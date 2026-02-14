package Apl;

import Server.Movie;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetMoviesByYearHandler implements HttpHandler {
    List<Movie> films;
    String jsonResponse;

    public GetMoviesByYearHandler(List<Movie> films){
        this.films = films;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {

        if(!ex.getRequestMethod().equalsIgnoreCase("GET")){
            ex.sendResponseHeaders(405,-1);
            String jsonResponse = "Ошибка метода";
            try(OutputStream os = ex.getResponseBody()){
                os.write(jsonResponse.getBytes());
            }
            return;
        }

        Gson gson = new Gson();
        int year;
        //получение значения наприме year=1997 и получение 1997 после расщипление на 2 значения
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

            jsonResponse = gson.toJson("Ошибка форомата age не является числом");
            try(OutputStream os = ex.getResponseBody()){
                os.write(jsonResponse.getBytes());
            }
            return;

        }

        List<Movie> sameYearsFilms = new ArrayList<>();

        for(Movie film : films){
            if(film.getYear() == year){
                sameYearsFilms.add(film);
            }
        }


        if (sameYearsFilms.isEmpty()) {
            ex.sendResponseHeaders(400, 0);
            String jsonResponse = "Фильмы с данным годом отсутствует в списке";
            try (OutputStream os = ex.getResponseBody()) {
                os.write(jsonResponse.getBytes());
            }
            return;
        }

        ex.sendResponseHeaders(200, 0);
        String jsonResponse = gson.toJson(sameYearsFilms);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(jsonResponse.getBytes());
        }

    }
}
