package Test;

import Apl.*;
import Server.FilmsCollections;
import Server.MoviesServer;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Map;

public class ManualTestHelper {
    public static void main(String[] args) throws IOException, InterruptedException {
        final HttpServer server;
        FilmsCollections filmsCollections = new FilmsCollections();
        //для проверки с заполненным списком
        Map<Integer, Map<Integer, String>> filmsMap = filmsCollections.fullSampleData();
        //для проверки пустого списка
        Map<Integer, Map<Integer, String>> filmsMap2 = filmsCollections.emptyFilmsList();

        MoviesServer moviesServer = new MoviesServer(filmsMap);

        server = HttpServer.create(new InetSocketAddress(8999), 0);

        server.createContext("/get", new GetHandler(filmsMap));
        server.createContext("/get/", new GetByIdHandler(filmsMap));
        server.createContext("/post", new PostHandler(filmsMap));
        server.createContext("/delete/", new DeleteHandler(filmsMap));
        server.createContext("/get/movies", new GetMoviesByYearHandler(filmsMap));

        server.start();

        System.out.println("Server запущен на порту 8999");


        HttpClient client = HttpClient.newHttpClient();

        String jsonData = "";


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8999/post"))
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(jsonData))
                .build();

    }
}
