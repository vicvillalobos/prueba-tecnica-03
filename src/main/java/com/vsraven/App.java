package com.vsraven;

import io.javalin.Javalin;

public class App {
    public static void main(String[] args) {
        var app = Javalin.create(config -> {
            config.routes.get("/", ctx -> ctx.result("Hello world"));
        }).start(7070);
    }
}
