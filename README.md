# 동적 쿼리를 만들어 보자.

이전 브랜치에 이어서 이제는 동적 쿼리를 queryDSL에서 어떻게 할 수 있는지 확인해 볼 시간이다.     

이전에 남겼던 깃헙 주소인 [jpa-with-querydsl](https://github.com/basquiat78/jpa-with-querydsl)에서도 이와 관련 진행을 했었다.     

사실 Spring boot상에서 코드 작업은 크게 변화가 없다.     

그러면 이제 몇 가지 방법을 통해서 이 동적 쿼리를 작성하자.     

## 1. Where절에 직접 쿼리 조건 작성하기

Customer기준으로 단순하게 조건을 통해서 where절에 직접 쿼리를 작성하는 방식이다.

예를 들면 이전 JpaSpecificationExecutor의 Specification를 활용해서 CustomerSpec 클래스를 작성하고 그 안에 쿼리 조건을 작성을 했다.

queryDSL에서 where절에 직접 쿼리 조건을 넣는 방식은 마치 쿼리를 작성하는 것과 같은 방식과 거의 유사하다.

QueryCustomerRepository.java에 다음과 같이 메소드를 하나 만들고     
```
package io.basquiat.customer.repository.custom;

import io.basquiat.customer.model.dto.CustomerDto;
import io.basquiat.customer.model.entity.Customer;

import java.util.List;
import java.util.Map;

/**
 * created by basquiat
 */
public interface QueryCustomerRepository {
    
    Customer findCustomerBySearchValue(SearchVo searchVo);

    List<Customer> findAllCustomer();
    List<CustomerDto> findAllCustomerDto();

}

```

이것을 구현해 보자. 넘어오는 파라미터 정보는 개별적인 String 타입으로 넘어올 수도 있고 VO로 넘어올 수도 있지만 여기서는 SearchVo객체를 받는다.      

SearchVo.java
```
package io.basquiat.customer.model.vo;

import lombok.*;

/**
 * 검색 조건 값을 담는 VO
 */
@Getter
@Setter
public class SearchVo {

    private String name;

    private String email;

}

```

처음에는 가장 심플하게 가자.     

이것은 컨트롤러 단에서 파라미터 정보를 어떻게 받느냐에 따라서 달라질 수 있을 것이다.     

또한 값의 null 또는 빈값을 체크하기 위해서 이것을 체크하는 유틸을 하나 만들었다.

그리고 가장 흔하디 흔한 형식으로 한번 일단 작성을 해보자.
```
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

    .
    .
    .

    @Override
    public Customer findCustomerBySearchValue(SearchVo searchVo) {
        JPAQuery<Customer> customerQuery = query.selectFrom(customer)
                                                .where(
                                                    customer.customerEmail.eq(searchVo.getEmail()),
                                                    customer.customerName.eq(searchVo.getName())
                                                );
        return customerQuery.fetch();
    }

}
```

뭔가 쿼리같은 형식이다.     

그럼 교과서적인 테스트를 작성해보자.

```
@Test
@Transactional
public void selectCustomerBySearchVO() {
    SearchVo searchVo = new SearchVo();
    searchVo.setName("basquiat_name_15");
    searchVo.setEmail("basquiat_email_15");
    Customer selected = customerRepository.findCustomerBySearchValue(searchVo);
    System.out.println(convertJsonStringFromObject(selected));
}

result: 
Hibernate: 
    /* select
        customer 
    from
        Customer customer 
    where
        customer.customerEmail = ?1 
        and customer.customerName = ?2 */ select
            customer0_.id as id1_1_,
            customer0_.created_at as created_2_1_,
            customer0_.updated_at as updated_3_1_,
            customer0_.customer_email as customer4_1_,
            customer0_.customer_mobile as customer5_1_,
            customer0_.customer_name as customer6_1_ 
        from
            basquiat.basquiat_customer customer0_ 
        where
            customer0_.customer_email=? 
            and customer0_.customer_name=?
2021-02-09 10:38:24.393 TRACE 24028 --- [    Test worker] o.h.type.descriptor.sql.BasicBinder      : binding parameter [1] as [VARCHAR] - [basquiat_email_15]
2021-02-09 10:38:24.394 TRACE 24028 --- [    Test worker] o.h.type.descriptor.sql.BasicBinder      : binding parameter [2] as [VARCHAR] - [basquiat_name_15]
2021-02-09 10:38:24.400 TRACE 24028 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([id1_1_] : [BIGINT]) - [15]
2021-02-09 10:38:24.405 TRACE 24028 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([created_2_1_] : [TIMESTAMP]) - [2021-02-08T11:15:31]
2021-02-09 10:38:24.405 TRACE 24028 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([updated_3_1_] : [TIMESTAMP]) - [null]
2021-02-09 10:38:24.405 TRACE 24028 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([customer4_1_] : [VARCHAR]) - [basquiat_email_15]
2021-02-09 10:38:24.405 TRACE 24028 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([customer5_1_] : [VARCHAR]) - [my_mobile_15]
2021-02-09 10:38:24.405 TRACE 24028 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([customer6_1_] : [VARCHAR]) - [basquiat_name_15]
Hibernate: 
    select
        addresses0_.customer_id as customer9_0_0_,
        addresses0_.id as id1_0_0_,
        addresses0_.id as id1_0_1_,
        addresses0_.created_at as created_2_0_1_,
        addresses0_.updated_at as updated_3_0_1_,
        addresses0_.is_default as is_defau4_0_1_,
        addresses0_.addr_detail as addr_det5_0_1_,
        addresses0_.addr as addr6_0_1_,
        addresses0_.addr_name as addr_nam7_0_1_,
        addresses0_.addr_postcode as addr_pos8_0_1_,
        addresses0_.customer_id as customer9_0_1_ 
    from
        basquiat.basquiat_address addresses0_ 
    where
        addresses0_.customer_id=?
2021-02-09 10:38:24.462 TRACE 24028 --- [    Test worker] o.h.type.descriptor.sql.BasicBinder      : binding parameter [1] as [BIGINT] - [15]
{"createdAt":{"nano":0,"year":2021,"monthValue":2,"dayOfMonth":8,"hour":11,"minute":15,"second":31,"month":"FEBRUARY","dayOfWeek":"MONDAY","dayOfYear":39,"chronology":{"id":"ISO","calendarType":"iso8601"}},"updatedAt":null,"id":15,"customerEmail":"basquiat_email_15","customerName":"basquiat_name_15","customerMobile":"my_mobile_15","addresses":[]}
2021-02-09 10:38:24.485  INFO 24028 --- [    Test worker] i.StatisticalLoggingSessionEventListener : Session Metrics {
    52231 nanoseconds spent acquiring 1 JDBC connections;
    0 nanoseconds spent releasing 0 JDBC connections;
    13287910 nanoseconds spent preparing 2 JDBC statements;
    13016255 nanoseconds spent executing 2 JDBC statements;
    0 nanoseconds spent executing 0 JDBC batches;
    0 nanoseconds spent performing 0 L2C puts;
    0 nanoseconds spent performing 0 L2C hits;
    0 nanoseconds spent performing 0 L2C misses;
    0 nanoseconds spent executing 0 flushes (flushing a total of 0 entities and 0 collections);
    22269 nanoseconds spent executing 1 partial-flushes (flushing a total of 0 entities and 0 collections)
}

```
그냥 조회한 Customer를 json형식으로 찍었기 때문에 Lazy Loading이 발생했지만 의도한대로 쿼리가 날아갔다.

하지만 이 로직은 문제강 있다. 왜냐하면 의도한 것은 만일 어떤 값이 null이면 조건절에 들어가지 않길 원했던 것인데    

```
@Test
@Transactional
public void selectCustomerBySearchVO() {
    SearchVo searchVo = new SearchVo();
    searchVo.setName("basquiat_name_15");
    // searchVo.setEmail("basquiat_email_15");
    Customer selected = customerRepository.findCustomerBySearchValue(searchVo);
    0 System.out.println(convertJsonStringFromObject(selected));
}     
```
55
만일 빈값 ""을 넣으면 어떻게 될까?

```

@Test
@Transactional
public void selectCustomerBySearchVO() {
    SearchVo searchVo = new SearchVo();
    searchVo.setName("basquiat_name_15");
    searchVo.setEmail("");
    Customer selected = customerRepository.findCustomerBySearchValue(searchVo);
    System.out.println(convertJsonStringFromObject(selected));
}

result:

Hibernate: 
    /* select
        customer 
    from
        Customer customer 
    where
        customer.customerEmail = ?1 
        and customer.customerName = ?2 */ select
            customer0_.id as id1_1_,
            customer0_.created_at as created_2_1_,
            customer0_.updated_at as updated_3_1_,
            customer0_.customer_email as customer4_1_,
            customer0_.customer_mobile as customer5_1_,
            customer0_.customer_name as customer6_1_ 
        from
            basquiat.basquiat_customer customer0_ 
        where
            customer0_.customer_email=? 
            and customer0_.customer_name=?
```
조건절에 저렇게 붙어서 나갈것이다.     

결과는 저 두개의 조건이 붙어서 쿼리를 하기 때문에 반환되는 객체는 null이 된다.    

그래서 보통 이런 방식의 경우에는 필수적으로 값이 들어온다는 가정하에 작성하게 된다. 또는 컨트롤러 단에서 이미 유효성 검증이나 체크를 할 것이다.     

그래서 이런 경우에는 BooleanBuilder를 활용해서 한다.     

그럼 코드를 한번 짜보자.

```
QueryCustomerRepository.java

package io.basquiat.customer.repository.custom;

import io.basquiat.customer.model.dto.CustomerDto;
import io.basquiat.customer.model.entity.Customer;
import io.basquiat.customer.model.vo.SearchVo;

import java.util.List;

/**
 * created by basquiat
 */
public interface QueryCustomerRepository {

    Customer findCustomerBySearchValue(SearchVo searchVo);
    Customer findCustomerBySearchValueUsginBooleanBuilder(SearchVo searchVo);

    List<Customer> findAllCustomer();
    List<CustomerDto> findAllCustomerDto();

}


QueryCustomerRepositoryImpl.java

package io.basquiat.customer.repository.custom;

import com.querydsl.core.BooleanBuilder;
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
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if(!isEmpty(searchVo.getEmail())) {
            booleanBuilder.and(customer.customerEmail.eq(searchVo.getEmail()));
        }
        if(!isEmpty(searchVo.getName())) {
            booleanBuilder.and(customer.customerName.eq(searchVo.getName()));
        }
        JPAQuery<Customer> customerQuery = query.selectFrom(customer)
                                                .where(booleanBuilder);
        return customerQuery.fetchOne();
    }

}
```

이 조건을 재사용하기 위해 따로 메소드로 빼놓고 사용해도 무방하다.

```
@Override
public Customer findCustomerBySearchValueUsginBooleanBuilder(SearchVo searchVo) {
    JPAQuery<Customer> customerQuery = query.selectFrom(customer)
                                            .where(makeSearchContition(searchVo));
    return customerQuery.fetchOne();
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
```

컴포지트 패턴으로 이렇게 조합으로 사용해서 where절에 사용할 수 있다.

그럼 바로 테스트를 해보자.

```
@Test
@Transactional
public void selectCustomerBySearchVOUsginBooleanBuilder() {
    SearchVo searchVo = new SearchVo();
    searchVo.setName("basquiat_name_15");
    searchVo.setEmail("");
    Customer selected = customerRepository.findCustomerBySearchValueUsginBooleanBuilder(searchVo);
    System.out.println(convertJsonStringFromObject(selected));
}

result:
Hibernate: 
    /* select
        customer 
    from
        Customer customer 
    where
        customer.customerName = ?1 */ select
            customer0_.id as id1_1_,
            customer0_.created_at as created_2_1_,
            customer0_.updated_at as updated_3_1_,
            customer0_.customer_email as customer4_1_,
            customer0_.customer_mobile as customer5_1_,
            customer0_.customer_name as customer6_1_ 
        from
            basquiat.basquiat_customer customer0_ 
        where
            customer0_.customer_name=?
```
결과만 놓고 보자면 우리가 원하는 조건으로 검색이 되는 것을 확인할 수 있다.

지금 방식은 하나의 메소드에 분기를 통해서 동적 쿼리를 생성하고 있는데       

'나는 where조건절에 어떤 조건이 걸려있는지 바로 확인하고 싶고 그게 동적 쿼리로 동작하면 좋겠어'       

라고 한다면 이때 [jpa-with-querydsl](https://github.com/basquiat78/jpa-with-querydsl) 에서도 언급하긴 했지만 Predicate 인터페이스를 활용하면 된다.

```
@Override
    public Customer findCustomerBySearchValueUsginBooleanBuilder(SearchVo searchVo) {
        JPAQuery<Customer> customerQuery = query.selectFrom(customer)
                                                .where(
                                                    condByName(searchVo.getName()),
                                                    condByEmail(searchVo.getEmail())
                                                );
        return customerQuery.fetchOne();
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
```
위 코드처럼 where절에 이름, 이메일로 조건이 걸려 있는 것을 확인할 수 있다.     

저 위의 .where를 타고 타고 들어가면 Predicate가 null일 경우에는 조건절을 생성하지 않는다. 따라서 값의 유무에 따라서 조건이 걸릴수도 있고 안걸릴 수도 있다.      

myBatis에서 <if>태그로 조건을 걸면 값에 따라서 조건이 걸리는 것과 비슷하게 동적 쿼리를 생성한다.      

이렇게 분리하면 이런 조각들을 조합해서 사용하기 용이해진다.

하지만 나는 개인적으로는 이러한 메소드가 하나의 클래스에 펴져 있는 것을 그다지 좋아하지 않아서 하나의 객체에 위임하는 방식으로 대부분 진행한다.

위와 같은 방식을 써야 할 때는 크게 확장될 도메인이 아니고 조건 자체가 하나에서 두개의 단순한 경우 또는 2개 이상의 Q클래스를 통해서 조건을 생성할 때 사용한다.         

이유는 queryDSL의 Q클래스를 제너레이터하는 과정중에 위임 클래스에서 생성 순서에 따라 인식을 하지 못하는 경우가 있다.     

서버에 배포할때는 문제가 안되는데 개발시 IDE에서 인식을 하지 못하는 경험이 있어서 이럴 경우에는 위와 같이 메소드를 만들어서 사용하기도 한다.

그럼 이제부터 @QueryEntity와 @QueryDelegate 어노테이션을 사용해서 한번 작성해 보자.

SearchVO 클래스에 변수를 추가했다.

```
package io.basquiat.customer.model.vo;

import io.basquiat.common.model.SearchDateType;
import lombok.*;

/**
 * 검색 조건 값을 담는 VO
 */
@Getter
@Setter
public class SearchVo {

    private String name;

    private String email;

    private SearchDateType searchDateType;

    private String start;

    private String end;

}
```
번외이긴 하지만 고객 정보를 생성일, 수정일로 조회할 수 있기 때문에 관련 변수를 추가했으며 그외 몇가지 유틸과 enum 클래스를 추가했다.     

그리고 다음과 model 패키지하위에 extensions라는 패키지를 만들고 클래스를 하나 추가한다.

CustomerSqlExtension.java
```
package io.basquiat.customer.model.extensions;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.annotations.QueryDelegate;
import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.types.dsl.Expressions;
import io.basquiat.common.model.SearchDateType;
import io.basquiat.customer.model.entity.Customer;
import io.basquiat.customer.model.entity.QCustomer;
import io.basquiat.customer.model.vo.SearchVo;
import org.springframework.util.StringUtils;

import java.util.Map;

import static io.basquiat.common.utils.CommonUtils.isEmpty;

/**
 * queryDSL Dynamic Query Condition
 *
 * created by basquiat
 */
@QueryEntity
public class CustomerSqlExtension {

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
```
날짜의 경우에는 between을 활용할 수 있지만 시작일과 마지막일이 전부 같이 들어온다는 가정하에 사용할 수 있을 것이다.     

따라서 위와 같이 분기로 처리한다. 또한 SQL Function을 활용하고 있다.      

@QueryEntity와 @QueryDelegate을 활용하게 되면 실제 해당 Q클래스로 로직을 위임하기 때문에 build > clean, other > compileQueryDsl을 한번 실행해서 코드를 다시 제너레이트 해야한다.     

자 그럼 테스트 코드를 짜 보자.

날짜로 비교하는 조건이 있기 때문에 리스트로 반환될 수 있다.

```
QueryCustomerRepository

public interface QueryCustomerRepository {

    Customer findCustomerBySearchValue(SearchVo searchVo);
    Customer findCustomerBySearchValueUsginBooleanBuilder(SearchVo searchVo);

    List<Customer> findAllCustomer();
    List<Customer> findCustomerBySearchValueUsginQueryDelegate(SearchVo searchVo);

    List<CustomerDto> findAllCustomerDto();

}


QueryCustomerRepositoryImpl

public class QueryCustomerRepositoryImpl implements QueryCustomerRepository {

    private final JPAQueryFactory query;

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

}
```
위와 같이 하나를 더 만들어서 코드를 추가한다.     

이건 개인취향이긴 한데 나는 코드가 훝었을 때 쿼리가 상상이 되거나 조회된 데이터가 어떻게 들어올지 눈에 보이는 것을 좋아한다.     

그래서 위와 같은 방식을 좀 선호하는 편이다. 이건 항상 얘기하는 거지만 여기에는 답이 없다.     

단지 퍼포먼스의 문제일뿐!

테스트 하다가 지금까지는 운좋게 Infinite Recursion (무한 참조) 에러를 만나지 않았는데 이 테스트 도중에 만나게 되었다.

이와 관련 링크를 하나 남긴다.      

[jpa infinite recursion](https://www.baeldung.com/jackson-bidirectional-relationships-and-infinite-recursion)

내용의 요지는 json객체로 변환시에 oneToMany, ManyToOne같이 양방향 매핑일 경우 서로가 서로를 무한으로 참조하는 에러가 발생한다.

롬복에서 @ToString(exclude = "customer")같이 스트링으로 변환할때 이 부분이 발생한 요소를 제외하는 것처럼 json으로 변환시에 어노테이션으로 방지할 수 있다.     

테스트!
```
@Test
@Transactional
public void selectCustomerBySearchVOUsginQueryDelegate() {
    LocalDateTime now = LocalDateTime.now();
    String start = DateUtils.localDateTimeToDateString(now.minusDays(10L), DateFormatType.y_M_d);
    //String end = DateUtils.localDateTimeToDateString(now, DateFormatType.y_M_d);
    String end = DateUtils.localDateTimeToDateString(now.minusDays(7L), DateFormatType.y_M_d);
    SearchVo searchVo = new SearchVo();
    //searchVo.setName("basquiat_name_15");
    searchVo.setSearchDateType(SearchDateType.CREATED_AT);
    searchVo.setStart(start);
    //searchVo.setEnd(end);
    List<Customer> selected = customerRepository.findCustomerBySearchValueUsginQueryDelegate(searchVo);
    System.out.println(convertJsonStringFromObject(selected));
}

result:
Hibernate: 
    /* select
        customer 
    from
        Customer customer 
    where
        DATE_FORMAT(customer.createdAt, ?1) >= ?2 */ select
            customer0_.id as id1_1_,
            customer0_.created_at as created_2_1_,
            customer0_.updated_at as updated_3_1_,
            customer0_.customer_email as customer4_1_,
            customer0_.customer_mobile as customer5_1_,
            customer0_.customer_name as customer6_1_ 
        from
            basquiat.basquiat_customer customer0_ 
        where
            date_format(customer0_.created_at, ?)>=?
```
end부분은 주석을 처리했기 때문에 의도한대로 쿼리가 나가는 것을 알 수 있다.     

값을 이리저리 세팅하고 주석처리하고 하면서 쿼리가 날아가는 것을 확인하며 동적으로 의도한대로 날아가는지 확인하는 것이 중요하다.      

개인적으로 하나의 클래스에서 그 클래스가 책임질 부분에 대해서만 집중하는게 좋다고 판단이 된다.     

SOLID의 법칙중 작은 의미에서 SRP (Single Responsibility Principle: 단일 책임 원칙)를 지키는 것인데 쿼리 부분은 말 그대로 쿼리에만 집중하고 테이블에 매핑되는 엔티티의 큐 클래스에서 이것을 위임해서 담당하게 해 유지보수의 컨택 포인트를 줄이는 것이 중요하다고 본다.     

뭐 나름 개똥철학이긴 하지만 이 방식이 나는 가장 괜찮다고 생각한다.     

앞으로 진행하면서 특별한 케이스가 아니면 이 방식으로 진행할 예정이다.

그렇다면 이제 동적 쿼리를 진행했으니 다음 브랜치에서는 페이징 처리를 한번 해볼 것이다.

# At A Glance
뭔가 정리가 잘 되지 않는 느낌이 없지 않아 있다. 그냥 딱 떠올랐던 아이디어 하나로 진행하기 때문에 좀 두서도 없고 그렇긴 하다. 하지만 적어도 이 레파지토리의 주 목적은 하나이다.     

하나의 아이디어에서 단계별로 코드를 어떻게 구성할 것인지 어떤 방식이 더 효율적이고 차후 유지보수에서 더 전략적인지 주석은 최소한으로 하고 코드가 한번에 읽혀지게 하기 위해 나부터 노력하는것이다.     

테스트 코드 활용과 그것을 토대로 바닥에서 위로 한단계씩 올라가는 것이 목표이다. 오타와 때론 놓친 코드 설정들이 있을 수 있지만 최대한 완성도를 높여보는게 최종 목표이다.      

     