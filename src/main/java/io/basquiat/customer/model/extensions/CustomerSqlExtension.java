package io.basquiat.customer.model.extensions;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.annotations.QueryDelegate;
import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import io.basquiat.customer.model.entity.Customer;
import io.basquiat.customer.model.entity.QCustomer;
import io.basquiat.customer.model.vo.SearchVo;
import org.springframework.data.domain.Sort;

import java.util.LinkedList;
import java.util.List;

import static io.basquiat.common.utils.CommonUtils.isEmpty;

/**
 * queryDSL Dynamic Query Condition
 *
 * created by basquiat
 */
@QueryEntity
public class CustomerSqlExtension {

    /**
     * Customer 엔티티에 걸리는 Where id 검색 조건절 생성
     * @param customer
     * @param id
     * @return BooleanBuilder
     */
    @QueryDelegate(Customer.class)
    public static BooleanBuilder condCustomerById(QCustomer customer, Long id) {
        BooleanBuilder builder = new BooleanBuilder();
        if(!isEmpty(id)) {
            builder.and(customer.id.eq(id));
        }
        return builder;
    }

    /**
     * Customer 엔티티에 걸리는 Where name 검색 조건절 생성
     * @param customer
     * @param name
     * @return BooleanBuilder
     */
    @QueryDelegate(Customer.class)
    public static BooleanBuilder condCustomerByName(QCustomer customer, String name) {
        BooleanBuilder builder = new BooleanBuilder();
        if(!isEmpty(name)) {
            // like 검색을 하자
            builder.and(customer.customerName.contains(name));
        }
        return builder;
    }

    /**
     * Customer 엔티티에 걸리는 Where email 검색 조건절 생성
     * @param customer
     * @param email
     * @return BooleanBuilder
     */
    @QueryDelegate(Customer.class)
    public static BooleanBuilder condCustomerByEmail(QCustomer customer, String email) {
        BooleanBuilder builder = new BooleanBuilder();
        if(!isEmpty(email)) {
            // like 검색을 하자
            builder.and(customer.customerEmail.contains(email));
        }
        return builder;
    }

    @QueryDelegate(Customer.class)
    public static OrderSpecifier[] dynamicSort(QCustomer customer, Sort sort) {
        // 순서가 중요하기 때문에
        final List<OrderSpecifier> orderBy = new LinkedList<>();
        sort.stream().forEach(order -> {
            PathBuilder<?> pathBuilder = new PathBuilder(QCustomer.customer.getType().getClass(), QCustomer.customer.toString());
            OrderSpecifier orderSpecifier = new OrderSpecifier(order.isAscending() ? Order.ASC : Order.DESC, pathBuilder.get(order.getProperty()));
            orderBy.add(orderSpecifier);
        });
        return orderBy.stream().toArray(OrderSpecifier[]::new);
    }

    /**
     * Customer 엔티티에 걸리는 Where date 검색 조건절 생성
     * 날짜 범위 검색
     * @param customer
     * @param searchVo
     * @return BooleanBuilder
     */
    @QueryDelegate(Customer.class)
    public static BooleanBuilder condCustomerByDate(QCustomer customer, SearchVo searchVo) {
        BooleanBuilder builder = new BooleanBuilder();
        // searchDateType
        if(!isEmpty(searchVo.getSearchDateType())) {
            switch(searchVo.getSearchDateType()) {
                case CREATED_AT:
                    if(!isEmpty(searchVo.getStart())) {
                        builder.and(Expressions.stringTemplate("DATE_FORMAT({0}, {1})", customer.createdAt, "%Y-%m-%d").goe(searchVo.getStart()));
                    }
                    if(!isEmpty(searchVo.getEnd())) {
                        builder.and(Expressions.stringTemplate("DATE_FORMAT({0}, {1})", customer.createdAt, "%Y-%m-%d").loe(searchVo.getEnd()));
                    }
                    break;
                case UPDATED_AT:
                    if(!isEmpty(searchVo.getStart())) {
                        builder.and(Expressions.stringTemplate("DATE_FORMAT({0}, {1})", customer.updatedAt, "%Y-%m-%d").goe(searchVo.getStart()));
                    }
                    if(!isEmpty(searchVo.getEnd())) {
                        builder.and(Expressions.stringTemplate("DATE_FORMAT({0}, {1})", customer.updatedAt, "%Y-%m-%d").loe(searchVo.getEnd()));
                    }
                    break;
                default:
                    break;
            }
        }
        return builder;
    }

}
