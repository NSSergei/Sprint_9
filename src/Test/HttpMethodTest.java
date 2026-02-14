package Test;
import static org.junit.jupiter.api.Assertions.*;

import Apl.*;
import Server.*;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.StreamSupport;


public class HttpMethodTest {
    private static final String BASE = "http://localhost:8080";
    private static MoviesStore server;
    private static HttpClient client;
    static FilmsCollections filmsCollections = new FilmsCollections();


    //метод emptyFilmsList() для проверки пустого спика
    static ArrayList<Movie> filmsListIsEmpty = filmsCollections.emptyFilmsList();
    static ArrayList<Movie> filmsListNotIsEmpty = filmsCollections.fullSampleData();

    @BeforeAll
    static void beforeAll() {
        server = new MoviesStore(filmsListNotIsEmpty);
        server.start();
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();

    }

    @AfterAll
    static void afterAll() {
        server.stop();
    }

    @Test
    void getMovies_whenEmpty_returnsEmptyArray() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200,response.statusCode(), "GET /movies должно вернуть 200");

        String contentTypeHeaderValue = response.headers().firstValue("Content-Type").orElse("");

        assertEquals("application/json; charset=UTF-8", contentTypeHeaderValue, "Content-Type должен содержать формат данных и кодировку");

        String body = response.body().trim();

        //System.out.println(body);
        assertFalse(body.startsWith("[") && body.endsWith("]") && body.length() == 2, "Ожидается JSON-массив");
    }
    @Test
    void genMovies_notEmpty() throws  Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "GET /movies должно вернуть 200");

        JsonElement json = JsonParser.parseString(response.body());
        assertTrue(json.isJsonArray(), "Ответ должен быть JSON-массивом");

        JsonArray movies = json.getAsJsonArray();

        boolean has1997 = StreamSupport.stream(movies.spliterator(), false)
                .map(JsonElement :: getAsJsonObject)
                .anyMatch(o -> o.get("year").getAsInt() == 1997);

        assertTrue(has1997);
        }

    @Test
    void deleteMovies() throws IOException, InterruptedException {
        int fullSizeList = filmsListNotIsEmpty.size();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies/1"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode(), "/DELETE id должен вернуть 204");

        System.out.println(fullSizeList + "  " + filmsListNotIsEmpty.size());
        assertTrue(fullSizeList > filmsListNotIsEmpty.size());

    }

    @Test
    void deleteMoviesWrongLength() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies/666"))
                .DELETE()
                .build();
        HttpResponse response = client.send(request,HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "/DELETE id должен вернуть 404");

        assertEquals("\"Позиция вне диапазона 666\"",response.body());

    }

    @Test
    void getByIdHandlet() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies/1"))
                .GET()
                .build();
        HttpResponse response = client.send(request,HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "/GET id должен вернуть 200");

        assertEquals("{\"title\":\"Люди в чёрном\",\"year\":1997,\"id\":2}",response.body());
    }

    @Test
    void getByIdHandletWrongPath() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies/12"))
                .GET()
                .build();
        HttpResponse response = client.send(request,HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "/GET id должен вернуть 404");
    }

    @Test
    void getByYearHandler() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies?year=1997"))
                .GET()
                .build();
        HttpResponse response = client.send(request,HttpResponse.BodyHandlers.ofString());

        assertEquals("[{\"title\":\"Пятый элемент\",\"year\":1997,\"id\":1}," +
                                "{\"title\":\"Люди в чёрном\",\"year\":1997,\"id\":2}," +
                                "{\"title\":\"Игра\",\"year\":1997,\"id\":3}]",
                                response.body());

    }

    @Test
    void getByYearHandlerWrongYear() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies?year=1780"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "/movies?year=1780, должен вернуть 400");
        assertEquals("Фильмы с данным годом отсутствует в списке",response.body());
    }

    @Test
    void PostHandlerAddNewFilm() throws  IOException, InterruptedException {
        String testFilm = """
                {
                "year": "1997",
                "name": "тест"
                }
        """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies"))
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(testFilm))
                .build();
        HttpResponse response = client.send(request,HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(),"POST /movies должен вернуть 201");

        assertEquals("[" +
                "{\"title\":\"Пятый элемент\",\"year\":1997,\"id\":1}," +
                "{\"title\":\"Люди в чёрном\",\"year\":1997,\"id\":2}," +
                "{\"title\":\"Игра\",\"year\":1997,\"id\":3}," +
                "{\"title\":\"BBC Cолнце\",\"year\":2024,\"id\":4}," +
                "{\"title\":\"BBC Африка\",\"year\":2024,\"id\":5}," +
                "{\"title\":\"Игра в Лаву\",\"year\":2024,\"id\":6}," +
                "{\"title\":\"тест\",\"year\":1997,\"id\":7}" +
                "]"
                ,
                response.body());
    }

    @Test
    void PostHandlerAddNewFilmWrongInformation() throws  IOException, InterruptedException {
        String testFilm = """
                {
                "year": "105",
                "name": "Тест Фильм"
                 }
                 """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies"))
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(testFilm))
                .build();
        HttpResponse response = client.send(request,HttpResponse.BodyHandlers.ofString());
        assertEquals("{\"error\":\"Ошибка валидации\",\"details\":[\"год должен быть между 1888 и 2026\"]}",response.body().toString());
    }
}




