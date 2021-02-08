package io.basquiat.customer.repository.custom;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.basquiat.customer.model.dto.CustomerDto;
import io.basquiat.customer.model.entity.Address;
import io.basquiat.customer.model.entity.Customer;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static io.basquiat.customer.model.entity.QAddress.address;
import static io.basquiat.customer.model.entity.QCustomer.customer;

/**
 * created by basquiat
 */
@RequiredArgsConstructor
public class QueryCustomerRepositoryImpl implements QueryCustomerRepository {

    private final JPAQueryFactory query;

    /**
     * DTO에 담아서 반환하기
     * @return List<CustomerDto>
     */
    @Override
    public List<CustomerDto> findAllCustomerDto() {
        Map<Customer, List<Address>> map = query.from(customer)
                                                .leftJoin(address).on(customer.id.eq(address.customerId))
                                                .transform(groupBy(customer).as(list(address)));
        return map.entrySet().stream()
                             .map(obj -> new CustomerDto(obj.getKey(), obj.getValue()))
                             .collect(Collectors.toList());
    }

}
