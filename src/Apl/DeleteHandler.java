package Apl;

import Server.Movie;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;


//возвращает ошибку, если фильм не найден;
//возвращает ошибку, если id не число.
public class DeleteHandler implements HttpHandler {
    private List<Movie> films;
    Gson gson = new Gson().newBuilder()
            .setPrettyPrinting()
            .create();

    public DeleteHandler(List<Movie> films) {
        this.films = films;
    }


    @Override
    public void handle(HttpExchange ex) throws IOException {
        if(!ex.getRequestMethod().equalsIgnoreCase("DELETE")){
            ex.sendResponseHeaders(405,-1);
            String jsonResponse = gson.toJson("Ошибка метода");
            try(OutputStream os = ex.getResponseBody()){
                os.write(jsonResponse.getBytes());
            }
            return;
        }

        String[] path = ex.getRequestURI().getPath().split("/");


        if (path.length != 3){
            ex.sendResponseHeaders(400,0);
            String jsonResponse = gson.toJson("id пропущен");
            try(OutputStream os = ex.getResponseBody()){
                os.write(jsonResponse.getBytes());
            }
            return;
        }

        int id;

        try{
            id  = Integer.valueOf(path[2]);
        } catch (NumberFormatException e){
            ex.sendResponseHeaders(400,0);
            String jsomResponse = gson.toJson("id не являются числом");
            try(OutputStream os = ex.getResponseBody()){
                os.write(jsomResponse.getBytes());
            }
            return;
        }

        if (id < 0 || id >= films.size()) {
            ex.sendResponseHeaders(404, 0);
            String jsonResponse = gson.toJson("Позиция вне диапазона " + id);
            try (OutputStream os = ex.getResponseBody()) {
                os.write(jsonResponse.getBytes());
            }
            return;
        }

        Movie toRemove = null;

        for(Movie film : films){
            if(film.getId() == id){
                toRemove = film;
            }
        }

        if (toRemove == null){
            ex.sendResponseHeaders(404,0);
            try(OutputStream os = ex.getResponseBody()){
                String jsonResponse = "id для удаления отсутсвует";
                os.write(jsonResponse.getBytes());
            }
        }

        ex.sendResponseHeaders(204,-1);

        films.remove(toRemove);
    }
}
