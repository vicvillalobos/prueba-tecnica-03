package com.vsraven.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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
                patch.has("gender") ? Arrays.stream(Gender.values())
                        .filter(g -> g.name().equals(patch.get("gender").asText()))
                        .findFirst().orElse(null) : this.gender
        );
    }

    public List<String> validate() {
        var errors = new ArrayList<String>();
        if (firstName == null || firstName.isBlank()) errors.add("field 'firstName' is required");
        if (lastName == null || lastName.isBlank()) errors.add("field 'lastName' is required");
        if (email == null || email.isBlank()) {
            errors.add("field 'email' is required");
        } else if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            errors.add("field 'email' must be a valid email address");
        }
        if (gender == null) errors.add("field 'gender' is required, valid values: M, F, O");
        return errors;
    }
}