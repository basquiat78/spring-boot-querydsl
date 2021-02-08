package io.basquiat.customer.repository.custom;

import io.basquiat.customer.model.dto.CustomerDto;
import io.basquiat.customer.model.entity.Address;
import io.basquiat.customer.model.entity.Customer;
import io.basquiat.customer.model.vo.SearchVo;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * created by basquiat
 */
public interface QueryAddressRepository {

    List<Address> findAddressList(Long customerId, Pageable pageable);

    CustomerDto findAddressListOne(Long customerId, Pageable pageable);

}
