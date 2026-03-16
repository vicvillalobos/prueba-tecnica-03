package com.vsraven.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface Entity<T> {
    String id();
    List<String> validate();
    T withId(String id);
    T patch(JsonNode patch);
}
