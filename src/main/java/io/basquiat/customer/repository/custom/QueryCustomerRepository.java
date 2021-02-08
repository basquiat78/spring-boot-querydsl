package io.basquiat.customer.repository.custom;

import io.basquiat.customer.model.dto.CustomerDto;
import io.basquiat.customer.model.entity.Customer;
import io.basquiat.customer.model.vo.SearchVo;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * created by basquiat
 */
public interface QueryCustomerRepository {

    Customer findCustomerBySearchValue(SearchVo searchVo);
    Customer findCustomerBySearchValueUsginBooleanBuilder(SearchVo searchVo);

    List<Customer> findAllCustomer();
    List<Customer> findCustomerBySearchValueUsginQueryDelegate(SearchVo searchVo);

    List<CustomerDto> findAllCustomerDto();

}
