package com.vsraven.route;

import com.vsraven.model.Customer;
import com.vsraven.repository.Repository;

public class CustomerRoutes extends ModelRoutes<Customer> {

    public CustomerRoutes(Repository<Customer> repository) {
        super(repository, Customer.class);
    }
}
