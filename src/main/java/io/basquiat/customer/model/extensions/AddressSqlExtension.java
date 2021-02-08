package io.basquiat.customer.model.extensions;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.annotations.QueryDelegate;
import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import io.basquiat.customer.model.entity.Address;
import io.basquiat.customer.model.entity.Customer;
import io.basquiat.customer.model.entity.QAddress;
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
public class AddressSqlExtension {

    /**
     * Address 엔티티에 걸리는 Where id 검색 조건절 생성
     * @param address
     * @param id
     * @return
     */
    @QueryDelegate(Address.class)
    public static BooleanBuilder condAddressById(QAddress address, Long id) {
        BooleanBuilder builder = new BooleanBuilder();
        if(!isEmpty(id)) {
            builder.and(address.id.eq(id));
        }
        return builder;
    }

    /**
     * Address 엔티티에 걸리는 Where customer_id 검색 조건절 생성
     * @param customerId
     * @param customerId
     * @return BooleanBuilder
     */
    @QueryDelegate(Address.class)
    public static BooleanBuilder condAddressByCustomerId(QAddress address, Long customerId) {
        BooleanBuilder builder = new BooleanBuilder();
        if(!isEmpty(customerId)) {
            builder.and(address.customer.id.eq(customerId));
        }
        return builder;
    }

}
