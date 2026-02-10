package Apl;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import com.google.gson.Gson;




public class GetByIdHandler implements HttpHandler {
    private Map<Integer, Map<Integer,String>> films;

    public GetByIdHandler(Map<Integer,Map<Integer,String>> films) {
        this.films = films;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException{
        Gson gson = new Gson();
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        String path = ex.getRequestURI().getPath();

        String[] parts = path.split("/");
        Integer year;
        Integer id;

        if(parts.length < 4){
            ex.sendResponseHeaders(400,0);

            String jsonResponse = gson.toJson("год или id промущен формат : get/year/id");
            try(OutputStream os = ex.getResponseBody()){
                os.write(jsonResponse .getBytes());
            }
            return;
        }


        try {
            year = Integer.valueOf(parts[2]);
        } catch (NumberFormatException e){
            ex.sendResponseHeaders(400,0);

            String jsomResponse = gson.toJson("Ошибка типа данных года");
            try(OutputStream os = ex.getResponseBody()){
                os.write(jsomResponse.getBytes());
            }
            return;
        }

        try {
            id = Integer.valueOf(parts[3]);
        } catch (NumberFormatException e) {
            ex.sendResponseHeaders(400,0);

            String jsomResponse = gson.toJson("Ошибка типа данных id");
            try(OutputStream os = ex.getResponseBody()){
                os.write(jsomResponse.getBytes());
            }
            return;
        }

        if(!films.containsKey(year) || !films.get(year).containsKey(id)){
            ex.sendResponseHeaders(404,0);

            String jsonResponse = gson.toJson("фильм с данным ID не найден / заданный год отсутствует");
            try(OutputStream os = ex.getResponseBody()){
                os.write(jsonResponse .getBytes());
            }
            return;
        }

        String name = films.get(year).get(id);

        ex.sendResponseHeaders(200,0);

        String jsonResponse = gson.toJson("Фильм по введенному id " + id + " называется: " + name);
        try(OutputStream os = ex.getResponseBody()){
            os.write(jsonResponse.getBytes());
        }
    }
}
