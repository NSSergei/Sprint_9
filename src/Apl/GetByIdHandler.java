package Apl;

import Server.Movie;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;




public class GetByIdHandler implements HttpHandler {
    private List<Movie> films;

    public GetByIdHandler(List<Movie> films) {
        this.films = films;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException{

        if(!ex.getRequestMethod().equalsIgnoreCase("GET")){
            ex.sendResponseHeaders(405,-1);
            String jsonResponse = "Ошибка метода";
            try(OutputStream os = ex.getResponseBody()){
                os.write(jsonResponse.getBytes());
            }
            return;
        }
        Gson gson = new Gson();
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        String path = ex.getRequestURI().getPath();
        String[] parts = path.split("/");

        if(parts.length != 3 || !parts[1].equals("movies")){
            ex.sendResponseHeaders(400,0);

            String jsonResponse = gson.toJson("id промущен формат : get/{id}");
            try(OutputStream os = ex.getResponseBody()){
                os.write(jsonResponse .getBytes());
            }
            return;
        }

        String index = parts[2];
        Integer id ;


        try {
            id = Integer.valueOf(index);
        } catch (NumberFormatException e) {
            ex.sendResponseHeaders(400,0);

            String jsomResponse = gson.toJson("Ошибка типа данных id");
            try(OutputStream os = ex.getResponseBody()){
                os.write(jsomResponse.getBytes());
            }
            return;
        }

        if (id < 1 || id >= films.size()) {
            ex.sendResponseHeaders(404, 0);
            String jsonResponse = gson.toJson("Фильм с данным ID не найден");
            try (OutputStream os = ex.getResponseBody()) {
                os.write(jsonResponse.getBytes());
            }
            return;
        }

        ex.sendResponseHeaders(200,0);
        Movie filmsId = null;

        for(Movie film : films){
            if(film.getId() == id){
                filmsId = film;
            }
        }

        String jsonResponse = gson.toJson(filmsId);

        try(OutputStream os = ex.getResponseBody()){
            os.write(jsonResponse.getBytes());

        }
    }
}
