package com.yoga.firesafety.backend.domain.service;

import com.yoga.firesafety.backend.domain.entity.Customer;
import com.yoga.firesafety.backend.domain.repository.CustomerRepository;
import com.yoga.firesafety.backend.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;

    public List<Customer> getAllCustomers() {
        return repository.findAll();
    }

    public Customer getCustomerById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    @Transactional
    public Customer createCustomer(Customer customer) {
        return repository.save(customer);
    }

    @Transactional
    public Customer updateCustomer(UUID id, Customer customerDetails) {
        Customer customer = getCustomerById(id);
        customer.setName(customerDetails.getName());
        customer.setContactPerson(customerDetails.getContactPerson());
        customer.setEmail(customerDetails.getEmail());
        customer.setPhone(customerDetails.getPhone());
        customer.setBillingAddress(customerDetails.getBillingAddress());
        return repository.save(customer);
    }

    @Transactional
    public void deleteCustomer(UUID id) {
        Customer customer = getCustomerById(id);
        repository.delete(customer);
    }
}
