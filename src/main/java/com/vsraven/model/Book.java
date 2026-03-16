package com.vsraven.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public record Book(
        String id,
        String title,
        int publicationYear,
        int edition,
        String authorName
) implements Serializable, Entity<Book> {

    @Override
    public Book withId(String id) {
        return new Book(id, this.title, this.publicationYear, this.edition, this.authorName);
    }

    @Override
    public Book patch(JsonNode patch) {
        return new Book(
                this.id,
                patch.has("title") ? patch.get("title").asText() : this.title,
                patch.has("publicationYear") ? patch.get("publicationYear").asInt() : this.publicationYear,
                patch.has("edition") ? patch.get("edition").asInt() : this.edition,
                patch.has("authorName") ? patch.get("authorName").asText() : this.authorName
        );
    }

    public List<String> validate() {
        var errors = new ArrayList<String>();
        if (title == null || title.isBlank()) errors.add("field 'title' is required");
        if (publicationYear <= 0) errors.add("field 'publicationYear' must be a positive integer");
        if (edition < 1) errors.add("field 'edition' must be a positive integer");
        if (authorName == null || authorName.isBlank()) errors.add("field 'authorName' is required");
        return errors;
    }
}
