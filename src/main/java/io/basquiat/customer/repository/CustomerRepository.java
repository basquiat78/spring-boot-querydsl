package io.basquiat.customer.repository;

import io.basquiat.common.repository.BaseRepository;
import io.basquiat.customer.model.entity.Customer;
import io.basquiat.customer.repository.custom.QueryCustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

/**
 * customer repository
 * created by basquiat
 */
public interface CustomerRepository extends BaseRepository<Customer, Long>, QueryCustomerRepository {

    @EntityGraph(attributePaths = "addresses")
    @Query(value = "SELECT c FROM Customer c", countQuery = "SELECT COUNT(c) FROM Customer c WHERE c.id > 5")
    Page<Customer> findAllQuery(Pageable pageable);

    @EntityGraph(attributePaths = "addresses")
    @Query(value = "SELECT c FROM Customer c")
    Page<Customer> findAllQueryAndCountQuery(Pageable pageable);

    //@Query(value = "SELECT DISTINCT c FROM Customer c left join fetch c.addresses", countQuery = "SELECT COUNT(c) FROM Customer c")
    @Query(value = "SELECT DISTINCT c FROM Customer c join fetch c.addresses", countQuery = "SELECT COUNT(c) FROM Customer c")
    Page<Customer> findAllQueryAndCountQueryOne(Pageable pageable);

}
