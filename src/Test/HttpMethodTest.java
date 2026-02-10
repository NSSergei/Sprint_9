package Test;
import static org.junit.jupiter.api.Assertions.*;

import Apl.*;
import Server.*;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import java.util.Map;


public class HttpMethodTest {
    private static final String BASE = "http://localhost:8080";
    private static MoviesServer server;
    private static HttpClient client;
    static FilmsCollections filmsCollections = new FilmsCollections();

    //метод emptyFilmsList() для проверки пустого спика
    static Map<Integer, Map<Integer, String>> filmsMapIsEmpty = filmsCollections.emptyFilmsList();
    static Map<Integer, Map<Integer, String>> filmsMapNotIsEmpty = filmsCollections.fullSampleData();

    @BeforeAll
    static void beforeAll() {
        server = new MoviesServer(filmsMapNotIsEmpty);
        server.start();
        System.out.println("Сервер запущен");
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();

    }

    @AfterAll
    static void afterAll() {
        server.stop();
        System.out.println("Сервер остановлен");
    }

    @Test
    void getMovies_whenEmpty_returnsEmptyArray() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/get"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(200, response.statusCode(), "GET /get должен вернуть 200");

        String contentTypeHeaderValue = response.headers().firstValue("Content-Type").orElse("");

        assertEquals("application/json; charset=UTF-8",contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");
        String body = response.body().trim();

        //assertTrue если заменить на пустой массив
        assertFalse(body.startsWith("[") && body.endsWith("]"), "Ожидается JSON-массив");
    }

    @Test
    void genMovies_notEmpty() throws  Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/get"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "GET /get должен вернуть 200");
        String responseBody = response.body();
        JsonObject jO = JsonParser.parseString(responseBody).getAsJsonObject();

        //проверка нахождения ключа 1997
        assertTrue(jO.has("1997"), "Ответ должен содержать ключ '1997'");
    }

    @Test
    void deleteMovies() throws IOException, InterruptedException {
        //кол-во элементов в коллекции фильмов 1997 года
        int lengthMoviesFor1997 = filmsMapNotIsEmpty.get(1997).size();;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/delete/1997/1"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "/delete/year/id должен вернуть 200");

        assertTrue(filmsMapNotIsEmpty.get(1997).size() < lengthMoviesFor1997);
    }

    @Test
    void deleteMoviesWrongYear() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/delete/1980/1"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "/delete должен вернуть 400");
        assertEquals("\"Год / id не найден\"", response.body());
    }

    @Test
    void deleteMoviesWrongLength() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/delete/1980/"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "/delete должен вернуть 400");
        assertEquals("\"Длина пути котороткая , год или id пропущен\"", response.body());
    }

    @Test
    void getByIdHandlet() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/get/1997/1"))
                .GET()
                .build();

        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,response.statusCode(), "/get/year/id должен вернуть 200");
        assertEquals("\"Фильм по введенному id 1 называется: Пятый элемент\"",response.body());

    }

    @Test
    void getByIdHandletWrongPath() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/get/1997/12"))
                .GET()
                .build();

        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404,response.statusCode(), "/get/year/id должен вернуть 200");
        assertEquals("\"фильм с данным ID не найден / заданный год отсутствует\"",response.body());

    }

    @Test
    void getByYearHandler() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/get/movies?year=1997"))
                .GET()
                .build();
        HttpResponse response = client.send(request,HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "get/movies?year=1997, должен вернуть 200");

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        String expectedResponse = "{\"1\":\"Пятый элемент\",\"2\":\"Люди в чёрном\",\"3\":\"Игра\"}";
        assertEquals(expectedResponse, response.body());

    }

    @Test
    void getByYearHandlerWrongYear() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/get/movies?year=1780"))
                .GET()
                .build();
        HttpResponse response = client.send(request,HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "/get/movies?year=1780, должен вернуть 400");
        assertEquals("\"Год не найден\"",response.body());

    }

    @Test
    void PostHandlerAddNewFilm() throws  IOException, InterruptedException {
        String testFilm = """
                {
                "year": "1997",
                "id": "4",
                "name": "Тест Фильм"
                 }
                 """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/post"))
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(testFilm))
                .build();

        HttpResponse response = client.send(request,HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "/post, должен вернуть 200");

        String expectedResponse = "{"
                + "\"2024\":{"
                + "\"1\":\"BBC Cолнце\","
                + "\"2\":\"BBC Африка\","
                + "\"3\":\"Игра в Лаву\""
                + "},"
                + "\"1997\":{"
                + "\"1\":\"Пятый элемент\","
                + "\"2\":\"Люди в чёрном\","
                + "\"3\":\"Игра\","
                + "\"4\":\"Тест Фильм\""
                + "}"
                + "}";

        assertEquals(expectedResponse, response.body());
    }

    @Test
    void PostHandlerAddNewFilmWrongInformation() throws  IOException, InterruptedException {
        String testFilm = """
                {
                "year": "1997",
                "id": "4",
                "wrong": "Тест Фильм"
                 }
                 """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/post"))
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(testFilm))
                .build();
        HttpResponse response = client.send(request,HttpResponse.BodyHandlers.ofString());
        assertEquals("\"Поля name или year отсутствуют\"",response.body().toString());
    }
}