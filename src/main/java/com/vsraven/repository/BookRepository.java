package com.vsraven.repository;

import com.vsraven.model.Book;

import java.util.List;

public class BookRepository extends Repository<Book> {
    public BookRepository() {
        super(List.of(
           new Book("1", "The Hobbit", 1937, 1, "J.R.R. Tolkien"),
            new Book("2", "Dune", 1965, 1, "Frank Herbert"),
            new Book("3", "Foundation", 1951, 1, "Isaac Asimov")
        ));
    }
}
