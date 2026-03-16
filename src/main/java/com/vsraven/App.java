package com.vsraven;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsraven.db.Database;
import com.vsraven.repository.BookRepository;
import com.vsraven.repository.CustomerRepository;
import com.vsraven.route.BookRoutes;
import com.vsraven.route.CustomerRoutes;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

import java.nio.file.Path;

public class App {

    public static void main(String[] args) {

        var db = new Database(Path.of("data/app.db"));

        var bookRepo = new BookRepository(db);
        var customerRepo = new CustomerRepository(db);

        var objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);

        var app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson(objectMapper, false));
            config.routes.before(ctx -> {
                if (!ctx.body().isEmpty()) {
                    var contentType = ctx.contentType();
                    if (contentType == null || !contentType.contains("application/json")) {
                        ctx.status(415).json("{\"message\": \"Content-Type must be application/json\"}");
                        ctx.skipRemainingHandlers();
                    }
                }
            });
            var v1 = "/api/v1";
            new BookRoutes(bookRepo).register(config.routes, v1 + "/books");
            new CustomerRoutes(customerRepo).register(config.routes, v1 + "/customers");
        }).start(7070);
    }
}
