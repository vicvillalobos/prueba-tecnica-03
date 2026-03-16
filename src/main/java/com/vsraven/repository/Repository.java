package com.vsraven.repository;

import com.vsraven.model.Entity;

import java.util.*;

public abstract class Repository<T extends Entity<T>> {

    private final Map<String, T> items;

    public Repository(List<T> items) {
        this.items = new HashMap<String, T>();
        for (T item : items) {
            this.items.putIfAbsent(item.id(), item);
        }
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
        return Optional.ofNullable(items.putIfAbsent(id, itemWithId) == null ? itemWithId : null);
    }

    public boolean update(T data) {
        return items.replace(data.id(), data) != null;
    }

    public boolean delete(String id) {
        return items.remove(id) != null;
    }

}
