package Server;

import Apl.*;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;

public class MoviesServer {
    private final HttpServer server;


    public MoviesServer(Map<Integer, Map<Integer, String>> filmsMap) {
        try {
            server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/get", new GetHandler(filmsMap));
            server.createContext("/get/", new GetByIdHandler(filmsMap));
            server.createContext("/post", new PostHandler(filmsMap));
            server.createContext("/delete/", new DeleteHandler(filmsMap));
            server.createContext("/get/movies", new GetMoviesByYearHandler(filmsMap));
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
