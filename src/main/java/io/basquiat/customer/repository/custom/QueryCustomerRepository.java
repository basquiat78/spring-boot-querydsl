package io.basquiat.customer.repository.custom;

import io.basquiat.customer.model.dto.CustomerDto;
import io.basquiat.customer.model.entity.Customer;

import java.util.List;

/**
 * created by basquiat
 */
public interface QueryCustomerRepository {

    List<Customer> findAllCustomer();
    List<CustomerDto> findAllCustomerDto();

}
