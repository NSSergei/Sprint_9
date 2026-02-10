package Apl;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;


//возвращает ошибку, если фильм не найден;
//возвращает ошибку, если id не число.
public class DeleteHandler implements HttpHandler {
    private Map<Integer,Map<Integer,String>> films;
    Gson gson = new Gson().newBuilder()
            .setPrettyPrinting()
            .create();

    public DeleteHandler(Map<Integer,Map<Integer,String>> films) {
        this.films = films;
    }


    @Override
    public void handle(HttpExchange ex) throws IOException {
        String[] path = ex.getRequestURI().getPath().split("/");


        if (path.length < 4){
            ex.sendResponseHeaders(400,0);

            String jsonResponse = gson.toJson("Длина пути котороткая , год или id пропущен");
            try(OutputStream os = ex.getResponseBody()){
                os.write(jsonResponse.getBytes());
            }
            return;
        }

        Integer year = Integer.valueOf(path[2]);
        Integer id = Integer.valueOf(path[3]);

        try{
            int testYear = year;
            int testId = id;
        } catch (NumberFormatException e){
            ex.sendResponseHeaders(400,0);

            String jsomResponse = gson.toJson("Ошибка год или id не являются числом");
            try(OutputStream os = ex.getResponseBody()){
                os.write(jsomResponse.getBytes());
            }
            return;
        }


        if(!films.containsKey(year) || !films.get(year).containsKey(id)){
            ex.sendResponseHeaders(400,0);
            String jsonResponse = gson.toJson("Год / id не найден");
            try(OutputStream os = ex.getResponseBody()){
                os.write(jsonResponse.getBytes());
            }
            return;
        }

        films.get(year).remove(id);
        if(films.get(year).isEmpty()){
            films.remove(year);
        }

        ex.sendResponseHeaders(200,0);

        String JsonResponse = gson.toJson(films);
        String JsonResponse2 = gson.toJson("Фильм был удален");
        try(OutputStream os = ex.getResponseBody()){
            os.write(JsonResponse2.getBytes());
            os.write(JsonResponse.getBytes());
        }
    }
}
