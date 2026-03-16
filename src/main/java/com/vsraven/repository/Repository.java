package com.vsraven.repository;

import com.vsraven.db.Database;
import com.vsraven.model.Entity;

import java.util.*;

public abstract class Repository<T extends Entity<T>> {

    private final Map<String, T> items;
    private final Database db;

    public Repository(Database db, String collectionName) {
        this.db = db;
        this.items = db.getCollection(collectionName);
    }

    public List<T> findMany() {
        return this.items.values().stream().toList();
    }

    public Optional<T> findOne(String id) {
        return Optional.ofNullable(items.get(id));
    }

    public Optional<T> insert(T data) {
        var id = UUID.randomUUID().toString();
        var itemWithId = data.withId(id);
        var result = items.putIfAbsent(id, itemWithId) == null ? itemWithId : null;
        db.commit();
        return Optional.ofNullable(result);
    }

    public boolean update(T data) {
        var result = items.replace(data.id(), data) != null;
        db.commit();
        return result;
    }

    public boolean delete(String id) {
        var result = items.remove(id) != null;
        db.commit();
        return result;
    }

}
