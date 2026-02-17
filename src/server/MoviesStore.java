package server;
import apl.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class MoviesStore {
    private List<Movie> films;
    private final HttpServer server;


    public MoviesStore (List<Movie> films) {
        try {
            this.films = films;
            server = HttpServer.create(new InetSocketAddress(8080), 0);

            //реализация с помощью лямда выражения, чтобы не писать отдельный handle для реализации подобной структуры
            //приме реализации в ManualTestHelper
            server.createContext("/movies", ex -> {
                String method = ex.getRequestMethod();
                String path = ex.getRequestURI().getPath();
                String[] parts = path.split("/");


                if (parts.length == 3){
                    if (method.equalsIgnoreCase("GET")) {
                        new GetByIdHandler(films).handle(ex);
                    } else if (method.equalsIgnoreCase("DELETE")) {
                        new DeleteHandler(films).handle(ex);
                    } else {
                        ex.sendResponseHeaders(405, -1);
                    }

                } else if (parts.length == 2) {
                    if (method.equalsIgnoreCase("GET")) {
                        if (ex.getRequestURI().getQuery() != null &&
                                ex.getRequestURI().getQuery().contains("year=")) {
                            new GetMoviesByYearHandler(films).handle(ex);
                        } else {
                            new GetHandler(films).handle(ex);
                        }
                    } else if (method.equalsIgnoreCase("POST")) {
                        new PostHandler(films).handle(ex);
                    } else {
                        ex.sendResponseHeaders(405, -1); // Метод не разрешён
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать HTTP-сервер", e);
        }


    }
    public void start() {
        server.start();
        System.out.println("Сервер запущен");
    }

    public void stop() {
        server.stop(0);
        System.out.println("Сервер остановлен");
    }
}
