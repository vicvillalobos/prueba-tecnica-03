package com.vsraven.repository;

import com.vsraven.db.Database;
import com.vsraven.model.Customer;

public class CustomerRepository extends Repository<Customer> {
    public CustomerRepository(Database db) {
        super(db, "customers");
    }
}