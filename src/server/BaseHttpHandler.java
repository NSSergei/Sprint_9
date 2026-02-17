package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.junit.jupiter.api.Test;

import javax.imageio.IIOException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public abstract class BaseHttpHandler implements HttpHandler {
    protected final Gson gson = new Gson();
    public void sendJson(HttpExchange ex, int status,Object body) throws IOException {
        String response = gson.toJson(body);
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        ex.sendResponseHeaders(status,bytes.length);

        try(OutputStream os = ex.getResponseBody()){
            os.write(bytes);
        }
    }
}
