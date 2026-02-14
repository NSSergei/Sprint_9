package Test;

import Apl.*;
import Server.FilmsCollections;

import Server.Movie;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Map;

public class ManualTestHelper implements HttpHandler {

    private final ArrayList<Movie> films;

    public static void main(String[] args) throws IOException {

        FilmsCollections filmsCollections = new FilmsCollections();
        ArrayList<Movie> films = filmsCollections.fullSampleData();

        int port = 9000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // создаём handler
        ManualTestHelper handler = new ManualTestHelper(films);

        // регистрируем путь
        server.createContext("/movies", handler);

        server.start();

        System.out.println("Сервер запущен на порту " + port);
    }

    ManualTestHelper(ArrayList<Movie> films) {
        this.films = films;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {

        String method = ex.getRequestMethod();
        String path = ex.getRequestURI().getPath();
        String query = ex.getRequestURI().getQuery();

        // /movies/число
        if (path.matches("/movies/\\d+")) {

            if (method.equalsIgnoreCase("GET")) {
                new GetByIdHandler(films).handle(ex);

            } else if (method.equalsIgnoreCase("DELETE")) {
                new DeleteHandler(films).handle(ex);

            } else {
                ex.sendResponseHeaders(405, -1);
            }

        }
        // /movies
        else if (path.equals("/movies")) {

            if (method.equalsIgnoreCase("GET")) {

                if (query != null && query.contains("year=")) {
                    new GetMoviesByYearHandler(films).handle(ex);
                } else {
                    new GetHandler(films).handle(ex);
                }

            } else if (method.equalsIgnoreCase("POST")) {
                new PostHandler(films).handle(ex);

            } else {
                ex.sendResponseHeaders(405, -1);
            }

        } else {
            ex.sendResponseHeaders(404, -1);
        }
    }

}