
package test;

import apl.*;
import server.FilmsCollections;

import server.Movie;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ManualTestHelper{
    private List<Movie> films;
    private final HttpServer server;

    public ManualTestHelper(List<Movie> films) {
        try {
            this.films = films;
            server = HttpServer.create(new InetSocketAddress(9000), 0);

            //реализация с помощью лямда выражения, чтобы не писать отдельный handle для реализации подобной структуры
            //приме реализации в ManualTestHelper
            server.createContext("/movies", ex -> {
                String method = ex.getRequestMethod();
                String path = ex.getRequestURI().getPath();
                String[] parts = path.split("/");


                if (parts.length == 3) {
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
    public static void main (String[]args) throws IOException {
        FilmsCollections filmsCollections = new FilmsCollections();
        ManualTestHelper manualTestHelper = new ManualTestHelper(filmsCollections.fullSampleData());

        manualTestHelper.server.start();
        System.out.println("Сервер запущен port 9000");

    }
}

