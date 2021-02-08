package io.basquiat.customer.repository.custom;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.basquiat.customer.model.dto.CustomerDto;
import io.basquiat.customer.model.entity.Address;
import io.basquiat.customer.model.entity.Customer;
import io.basquiat.customer.model.vo.SearchVo;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static io.basquiat.common.utils.CommonUtils.isEmpty;
import static io.basquiat.customer.model.entity.QAddress.address;
import static io.basquiat.customer.model.entity.QCustomer.customer;

/**
 * created by basquiat
 */
@RequiredArgsConstructor
public class QueryCustomerRepositoryImpl implements QueryCustomerRepository {

    private final JPAQueryFactory query;

    /**
     * 그냥 엔티티 자체로 반환하기.
     * @return List<Customer>
     */
    @Override
    public List<Customer> findAllCustomer() {
        JPAQuery<Customer> customerQuery = query.selectFrom(customer)
                                                .leftJoin(customer.addresses, address)
                                                .fetchJoin();
        return customerQuery.fetch();
    }

    /**
     * DTO에 담아서 반환하기
     * @return List<CustomerDto>
     */
    @Override
    public List<CustomerDto> findAllCustomerDto() {
        Map<Customer, List<Address>> map = query.from(customer)
                                                .leftJoin(customer.addresses, address)
                                                .transform(groupBy(customer).as(list(address)));
        return map.entrySet().stream()
                             .map(obj -> new CustomerDto(obj.getKey(), obj.getValue()))
                             .collect(Collectors.toList());
    }

    @Override
    public Customer findCustomerBySearchValue(SearchVo searchVo) {
        JPAQuery<Customer> customerQuery = query.selectFrom(customer)
                                                .where(
                                                    customer.customerEmail.eq(searchVo.getEmail()),
                                                    customer.customerName.eq(searchVo.getName())
                                                );
        return customerQuery.fetchOne();
    }

    @Override
    public Customer findCustomerBySearchValueUsginBooleanBuilder(SearchVo searchVo) {
        JPAQuery<Customer> customerQuery = query.selectFrom(customer)
                                                .where(
                                                    customer.condCustomerByName(searchVo.getName()),
                                                    customer.condCustomerByEmail(searchVo.getEmail())
                                                );
        return customerQuery.fetchOne();
    }

    @Override
    public List<Customer> findCustomerBySearchValueUsginQueryDelegate(SearchVo searchVo) {
        JPAQuery<Customer> customerQuery = query.selectFrom(customer)
                                                .where(
                                                        customer.condCustomerByName(searchVo.getName()),
                                                        customer.condCustomerByEmail(searchVo.getEmail()),
                                                        customer.condCustomerByDate(searchVo)
                                                );
        return customerQuery.fetch();
    }

    private BooleanBuilder makeSearchContition(SearchVo searchVo) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if(!isEmpty(searchVo.getEmail())) {
            booleanBuilder.and(customer.customerEmail.eq(searchVo.getEmail()));
        }
        if(!isEmpty(searchVo.getName())) {
            booleanBuilder.and(customer.customerName.eq(searchVo.getName()));
        }
        return booleanBuilder;
    }

    private Predicate condByName(String name) {
        return isEmpty(name) ? null : customer.customerName.eq(name);
    }

    private Predicate condByEmail(String email) {
        return isEmpty(email) ? null : customer.customerEmail.eq(email);
    }

}
