package io.basquiat.customer.repository;

import io.basquiat.common.repository.BaseRepository;
import io.basquiat.customer.model.entity.Customer;
import io.basquiat.customer.repository.custom.QueryCustomerRepository;

/**
 * customer repository
 * created by basquiat
 */
public interface CustomerRepository extends BaseRepository<Customer, Long>, QueryCustomerRepository {

}
