package com.vsraven.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public record Customer(
        String id,
        String firstName,
        String lastName,
        String email,
        Gender gender
) implements Serializable, Entity<Customer> {
    public enum Gender { M, F, O }

    @Override
    public Customer withId(String id) {
        return new Customer(id, this.firstName, this.lastName, this.email, this.gender);
    }

    @Override
    public Customer patch(JsonNode patch) {
        return new Customer(
                this.id,
                patch.has("firstName") ? patch.get("firstName").asText() : this.firstName,
                patch.has("lastName") ? patch.get("lastName").asText() : this.lastName,
                patch.has("email") ? patch.get("email").asText() : this.email,
                patch.has("gender") ? Gender.valueOf(patch.get("gender").asText()) : this.gender
        );
    }

    public List<String> validate() {
        var errors = new ArrayList<String>();
        if (firstName == null || firstName.isBlank()) errors.add("firstName is required");
        if (lastName == null || lastName.isBlank()) errors.add("lastName is required");
        if (email == null || email.isBlank()) errors.add("email is required"); // TODO: Validate email format
        if (gender == null) errors.add("gender is required");
        return errors;
    }
}