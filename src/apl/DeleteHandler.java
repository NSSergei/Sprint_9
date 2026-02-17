package apl;

import server.BaseHttpHandler;
import server.ErrorResponse;
import server.Movie;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

import java.util.List;
import java.util.Map;


//возвращает ошибку, если фильм не найден;
//возвращает ошибку, если id не число.
public class DeleteHandler extends BaseHttpHandler {
    private List<Movie> films;
    Gson gson = new Gson().newBuilder()
            .setPrettyPrinting()
            .create();

    public DeleteHandler(List<Movie> films) {
        this.films = films;
    }




    @Override
    public void handle(HttpExchange ex) throws IOException {
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        String[] path = ex.getRequestURI().getPath().split("/");

        int id;

        try {
            id  = Integer.parseInt(path[2]);
        } catch (NumberFormatException e){
            sendJson(ex,400,new ErrorResponse("id не являются числом",400));
            /*ex.sendResponseHeaders(400,0);
            String jsomResponse = gson.toJson("id не являются числом");
            try(OutputStream os = ex.getResponseBody()){
                os.write(jsomResponse.getBytes());
            }*/
            return;
        }


        Movie toRemove = null;

        for(Movie film : films) {
            if(film.getId() == id) {
                toRemove = film;
                break;
            }
        }


        if (toRemove == null) {
            sendJson(ex,404,new ErrorResponse("id для удаления отсутсвует",400));
            return;
        }

        //ex.sendResponseHeaders(204,-1);
        sendJson(ex,204,"");
        films.remove(toRemove);
    }
}
