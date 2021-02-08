package io.basquiat.customer.repository.custom;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.basquiat.customer.model.dto.CustomerDto;
import io.basquiat.customer.model.entity.Address;
import io.basquiat.customer.model.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

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
public class QueryAddressRepositoryImpl implements QueryAddressRepository {

    private final JPAQueryFactory query;

    /**
     * 특정 회원의 주소 리스트를 반환한다.
     * @param customerId
     * @param pageable
     * @return List<Address>
     */
    @Override
    public List<Address> findAddressList(Long customerId, Pageable pageable) {
        JPAQuery<Address> addressQuery = query.selectFrom(address)
                                              .join(address.customer, customer)
                                              .fetchJoin()
                                              .where(
                                                    address.condAddressByCustomerId(customerId)
                                              )
                                              .orderBy(address.id.desc());
        if(pageable != null) {
            addressQuery.offset(pageable.getOffset())
                        .limit(pageable.getPageSize());
        }

        List<Address> list = addressQuery.fetch();
        return list;
    }

    @Override
    public CustomerDto findAddressListOne(Long customerId, Pageable pageable) {
        JPAQuery<?> addressQuery = query.from(address)
                                        .rightJoin(address.customer, customer)
                                        .where(
                                                customer.condCustomerById(customerId)
                                        )
                                        .orderBy(address.id.desc());
        if(pageable != null) {
            addressQuery.offset(pageable.getOffset())
                        .limit(pageable.getPageSize());
        }

        return addressQuery.transform(groupBy(customer).as(list(address)))
                           .entrySet()
                           .stream()
                           .map(obj -> new CustomerDto(obj.getKey(), obj.getValue()))
                           .findFirst().orElse(null);
    }

}
