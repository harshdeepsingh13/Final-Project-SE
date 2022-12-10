package com.example.finalproject.repositories;

import com.example.finalproject.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    public default Map<String, String> findReservedCustomers(){
        Map<String, String> result = new HashMap<>();
        List<Customer> customers = findAll();
        for (Customer customer:customers) {
            result.put(customer.getSeatno(), customer.getName());
        }
        return result;
    };
}
