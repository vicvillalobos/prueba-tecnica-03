package com.vsraven.route;

import com.fasterxml.jackson.databind.JsonNode;
import com.vsraven.model.Entity;
import com.vsraven.repository.Repository;
import io.javalin.config.RoutesConfig;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.util.List;
import java.util.Optional;

public abstract class ModelRoutes<T extends Entity<T>> {

    protected final Repository<T> repository;
    protected final Class<T> type;

    public ModelRoutes(Repository<T> repository, Class<T> type) {
        this.repository = repository;
        this.type = type;
    }

    public void register(RoutesConfig routes, String prefix) {
        routes.get(prefix,                      this::findAll);
        routes.get(prefix + "/{id}",       this::findOne);
        routes.post(prefix,                     this::insert);
        routes.patch(prefix + "/{id}",     this::update);
        routes.delete(prefix + "/{id}",    this::delete);
        System.out.println("Registered routes: " + prefix);
    }

    protected void findAll(Context context) {
        List<T> items = this.repository.findMany();
        context.status(HttpStatus.OK);
        context.json(items.toArray());
    }

    protected void findOne(Context context) {
        // TODO: Validate ID parameter

        Optional<T> item = this.repository.findOne(context.pathParam("id"));
        if (item.isEmpty()) {
            context.status(HttpStatus.NOT_FOUND);
            context.json("{\"message\": \"Item not found\"}");
            return;
        }
        context.status(HttpStatus.OK);
        context.json(item.get());
    }

    protected void insert(Context context) {
        var itemToBeInserted = context.bodyAsClass(type);
        var errors = itemToBeInserted.validate();

        if (!errors.isEmpty()) {
            context.status(HttpStatus.BAD_REQUEST);
            context.json(errors);
            return;
        }

        var inserted = this.repository.insert(itemToBeInserted);

        if (inserted.isEmpty()) {
            context.status(HttpStatus.INTERNAL_SERVER_ERROR);
            context.json("{\"message\": \"Failed to insert item\"}");
            return;
        }
        context.status(HttpStatus.CREATED);
        context.json(inserted.get());
    }

    protected void update(Context context) {

        var itemToBeUpdated = this.repository.findOne(context.pathParam("id"));
        if(itemToBeUpdated.isEmpty()) {
            context.status(HttpStatus.NOT_FOUND);
            context.json("{\"message\": \"Item not found\"}");
            return;
        }

        var updatedItem = itemToBeUpdated.get().patch(context.bodyAsClass(JsonNode.class));

        if(!this.repository.update(updatedItem)) {
            context.status(HttpStatus.INTERNAL_SERVER_ERROR);
            context.json("{\"message\": \"Failed to update item\"}");
            return;
        }

        context.status(HttpStatus.OK);
        context.json(updatedItem);
    }

    protected void delete(Context context) {

        var itemToBeDeleted = this.repository.findOne(context.pathParam("id"));

        if (itemToBeDeleted.isEmpty()) {
            context.status(HttpStatus.NOT_FOUND);
            context.json("{\"message\": \"Item not found\"}");
            return;
        }

        if (!this.repository.delete(itemToBeDeleted.get().id())) {
            context.status(HttpStatus.INTERNAL_SERVER_ERROR);
            context.json("{\"message\": \"Failed to delete item\"}");
            return;
        }

        context.status(HttpStatus.NO_CONTENT);
    }
}
