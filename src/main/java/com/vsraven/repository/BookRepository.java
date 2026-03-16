package com.vsraven.repository;

import com.vsraven.db.Database;
import com.vsraven.model.Book;

import java.util.List;

public class BookRepository extends Repository<Book> {
    public BookRepository(Database db) {
        super(db, "books");
    }
}
