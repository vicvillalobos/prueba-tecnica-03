package com.vsraven.route;

import com.vsraven.model.Book;
import com.vsraven.repository.Repository;

public class BookRoutes extends ModelRoutes<Book> {

    public BookRoutes(Repository<Book> repository) {
        super(repository, Book.class);
    }
}
