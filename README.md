# 번외

## 테이블이 외래키 설정이 전혀 없는 경우는 어떻게 해야 하나요?

사실 회사의 테이블은 외래키 설정이 되어 있는 테이블이 없다. 찾아봤는데 보질 못했다.           

간단한 create/update/delete의 경우에는 간단하게 JPA를 활용하고 있으며 간단한 테이블 단위 조회를 제외하곤 조회의 경우에는 myBatis와 JdbcTemplate을 혼용하고 있기 때문이다.     

그렇다면 이런 상황이라면 어떨까?

## 개발 히스토리

'복잡한 릴레이션은 사용하지 않는다. 나머지는 JdbcTemplate과 myBatis에 올인!'      

단순 테이블 자체만 조회할때는 JPA를 활용하고 있지만 실제 운영되는 어플리케이션에서는 그런 경우는 많지 않다.      

그러면 현재 Address와 Customer 엔티티를 예로 들어서 어떻게 구성되어져 있냐면 다음과 같이 구성되어 있다.

Customer.java
```
package io.basquiat.customer.model.entity;

import com.querydsl.core.annotations.QueryProjection;
import io.basquiat.common.model.DateTimeCommon;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 고객 엔티티
 * created by basquiat
 */
@Entity
@Table(name = "basquiat_customer", catalog = "basquiat")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper=false)
@ToString
public class Customer extends DateTimeCommon {

    @Builder
    public Customer(String customerEmail, String customerName, String customerMobile) {
        this.customerEmail = customerEmail;
        this.customerName = customerName;
        this.customerMobile = customerMobile;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** 고객 이메일 */
    @Column(name = "customer_email", length = 100)
    private String customerEmail;

    /** 고객 명 */
    @Column(name = "customer_name", length = 100)
    private String customerName;

    /** 고객 모바일 */
    @Column(name = "customer_mobile", length = 50)
    private String customerMobile;

    public void changeCustomerEmail(String email) {
        this.customerEmail = email;
    }

    public void changeCustomerMobile(String mobile) {
        this.customerMobile = mobile;
    }

}

```

Address.java
```
package io.basquiat.customer.model.entity;

import com.querydsl.core.annotations.QueryProjection;
import io.basquiat.common.model.DateTimeCommon;
import lombok.*;

import javax.persistence.*;
import java.util.List;

/**
 * 고객 주소 엔티티
 * created by basquiat
 */
@Entity
@Table(name = "basquiat_address", catalog = "basquiat")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper=false)
@ToString(exclude = "customer")
public class Address extends DateTimeCommon {

    @Builder
    public Address(Long customerId, String addressName, String addressPostcode, String addressInfo, String addressDetail,
                   AddressDefaultStatus addressDefaultStatus) {
        this.customerId = customerId;
        this.addressName = addressName;
        this.addressPostcode = addressPostcode;
        this.addressInfo = addressInfo;
        this.addressDetail = addressDetail;
        this.addressDefaultStatus = addressDefaultStatus;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** 고객 아이디 */
    @Column(name = "customer_id")
    private Long customerId;

    /** 고객 주소 설정명 */
    @Column(name = "addr_name", length = 20)
    private String addressName;

    /** 우편번호 */
    @Column(name = "addr_postcode", length = 10)
    private String addressPostcode;

    /** 메인 주소 */
    @Column(name = "addr", length = 200)
    private String addressInfo;

    /** 상세 주소 */
    @Column(name = "addr_detail", length = 200)
    private String addressDetail;

    /** 기본 디폴트 주소로 설정 여부 */
    @Column(name = "is_default", length = 1)
    @Enumerated(EnumType.STRING)
    private AddressDefaultStatus addressDefaultStatus;

    /**
     * 대표 주소 여부 enum
     */
    public enum AddressDefaultStatus {
        Y,
        N;
    }

}

```
객체간 릴레이션은 사라지고 Long 타입의 customerId라는 변수가 존재한다. 상당히 테이블 중심의 엔티티라는 것을 알 수 있다.      

사실 이렇게 봐도 별 문제가 없다. 잘만 동작한다면 말이다.           

하지만 여기서는 릴레이션간의 복잡도보다는 모든 테이블을 심플하게 보고 조인을 통해서 조회를 하게 되어 있는데 이게 또 queryDSL과 상당히 조합이 좋다.

자 그럼 바로 시작하자.     

브랜치를 새로 땄기 때문에 이전 코드는 잊자. 이전 테스트 코드 역시 삭제했다.          

엔티티가 변경이 되었기 때문에 당연히 build > clean, other > compileQueryDsl을 클릭해서 Q클래스를 새로 생성해야 하는 것을 잊지 말자.     

이 경우에는 두 객체간의 연관관계가 설정되어 있지 않기 때문에 Customer 엔티티 자체로 주소 정보까지 반환할 수 있는 방법이 없다.      

물론 무식하게 customer 엔티티로 반환하고 이 녀석을 루프를 돌면서 customer_id로 일일히 주소의 정보를 조회해서 세팅해 주는 방법이 있다.      

그렇다면 이런 방식이 아닌 다른 방식으로 어떻게 해야할까?      

방법은 일전에 봤던 Result Aggregation을 이용해서 DTO에 담아내는 방식이다.

QueryCustomerRepository와 QueryCustomerRepositoryImpl 수정하자.
```
package io.basquiat.customer.repository.custom;

import io.basquiat.customer.model.dto.CustomerDto;
import io.basquiat.customer.model.entity.Customer;

import java.util.List;

/**
 * created by basquiat
 */
public interface QueryCustomerRepository {

    List<CustomerDto> findAllCustomerDto();

}




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

```
기존의 코드에서 엔티티 자체로 반환하는 녀석은 삭제했다.     

물론 프로젝션을 활용해서 특정 DTO에만 담아내는 코드를 사용할 수는 있겠지만 여기서는 그냥 삭제하고 진행한다.    

```
package io.basquiat.customer;

import io.basquiat.customer.model.dto.CustomerDto;
import io.basquiat.customer.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static io.basquiat.common.utils.CommonUtils.convertJsonStringFromObject;

@SpringBootTest
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void selectCustomerByQueryDSLUsingTransform() {
        List<CustomerDto> customers = customerRepository.findAllCustomerDto();
        System.out.println(convertJsonStringFromObject(customers));
    }

}
```
기존에 사용하던 코드는 그대로 사용해도 무방하게 작동한다.     

물론 외래키를 설정하고 연관관계 매핑을 해도 무방하지만 회사의 코드문화/개발 문화에 따라서 이렇게 변경할 수 있다.      

JPA를 활용하면 ORM/객체 지향적인 관점에서 봐야하지 않냐고 반문할 수도 있는데 개인적으로는 이 부분에 대해 답은 없다고 본다.     

빠른 포퍼먼스를 낼 수 있는 지향점 내에서 이런 방법론은 필요에 따라 선택할 수 있다고 본다.     
