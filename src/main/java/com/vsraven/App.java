package com.vsraven;

import com.vsraven.repository.BookRepository;
import com.vsraven.route.BookRoutes;
import io.javalin.Javalin;

public class App {
    public static void main(String[] args) {

        var bookRepo = new BookRepository();

        var app = Javalin.create(config -> {
            var v1 = "/api/v1";
            new BookRoutes(bookRepo).register(config.routes, v1 + "/books");
//            new RecipeRoutes().register(config.routes, v1 + "/recipes");
        }).start(7070);
    }
}
