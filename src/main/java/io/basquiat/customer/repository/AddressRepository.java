package io.basquiat.customer.repository;

import io.basquiat.common.repository.BaseRepository;
import io.basquiat.customer.model.entity.Address;
import io.basquiat.customer.repository.custom.QueryAddressRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * address repository
 * created by basquiat
 */
public interface AddressRepository extends BaseRepository<Address, Long>, QueryAddressRepository {


    @Query(value = "SELECT a FROM Address a JOIN FETCH a.customer WHERE a.customer.id = :customerId ORDER BY a.id DESC",
           countQuery = "SELECT COUNT(a) FROM Address a WHERE a.customer.id = :customerId"
          )
    Page<Address> findAddressListByCustomerId(@Param("customerId")Long customerId, Pageable pageable);

}
