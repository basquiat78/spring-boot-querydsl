# 심플한 페이징 처리

가장 심플하게 먼저 페이징 처리를 한번 해볼까 한다.     

실제 회원 관리 화면을 만든다면 이런 시나리오를 가져볼 수 있다.    

'회원 관리 리스트에서는 한 페이지에 10명 또는 20명을 보여준다.'      

mySql의 경우에는 페이징 처리를 위해서 LIMIT라는 예약어를 사용할 수 있는데 쿼리로 한번 살펴보자.

```
SELECT * FROM 
	basquiat_customer
  LIMIT 0, 10;
```

이렇게 작성해 볼수 있다.

물론 회원 관리 화면이기 때문에 최근 가입일 순서대로 내림차순을 하게 될텐데 id생성이 auto_increment이기 때문에 id를 기준으로 내림차순을 하면 가입일 순서대로 내림차순과 같은 효과를 낸다.     

```
SELECT * FROM 
	basquiat_customer
 ORDER BY id DESC
LIMIT 0, 10;
```
이런 방식으로 쿼리를 날리게 된다.

그럼 지금까지 만들어 온 것을 토대로 페이징을 처리해 보자.

## 기본 JPA를 이용해서 페이징을 하자.

자 그럼 지금처럼 정말 심플하게 고객의 정보만 가져온다고 한다면 queryDSL을 이용할 이유가 없는데 이런 심플한 경우에는 다음과 같이 작성해 볼 수 있다.

```
@Test
public void selectSimpleCustomerPagination() {
    //int page = 0; // 첫번째 페이지
    int page = 1; // 두번째 페이지
    int size = 10; // 10개씩 보여주기
    Pageable pageable = PageRequest.of(page, size);
    System.out.println(convertJsonStringFromObject(customerRepository.findAll(pageable)));
}
```
하지만 페이징과 정렬을 같이 하고 싶다면 findAll의 경우에는 다음과 같이 처리해야 한다.     

파라미터 시그니처가 Sort나 pageable중 하나만 받을 수 있기 때문에 다음과 같이 PageRequest에 이 Sort를 설정해 준다.

소팅의 경우에는 멀티 컬럼으로 소팅을 할 수도 있는데 이런 경우에는 .and()를 통해 그 다음에 소팅할 컬럼을 명시하면 된다.

```
e.g. Sort.by("id").descending().and(Sort.by("customer_name"));
```

```
@Test
public void selectSimpleCustomerPaginationAndOrder() {
    //int page = 0; // 첫번째 페이지
    int page = 1; // 두번째 페이지
    int size = 10; // 10개씩 보여주기
    Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending()); // 내림차순
    //Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending()); // 오름차순
    System.out.println(convertJsonStringFromObject(customerRepository.findAll(pageable)));
}

result: 
Hibernate: 
    /* select
        generatedAlias0 
    from
        Customer as generatedAlias0 
    order by
        generatedAlias0.id desc */ select
            customer0_.id as id1_1_,
            customer0_.created_at as created_2_1_,
            customer0_.updated_at as updated_3_1_,
            customer0_.customer_email as customer4_1_,
            customer0_.customer_mobile as customer5_1_,
            customer0_.customer_name as customer6_1_ 
        from
            basquiat.basquiat_customer customer0_ 
        order by
            customer0_.id desc limit ?,
            ?

{
  "content": [
    {
      "createdAt": {
        "month": "FEBRUARY",
        "dayOfWeek": "MONDAY",
        "dayOfYear": 39,
        "nano": 0,
        "year": 2021,
        "monthValue": 2,
        "dayOfMonth": 8,
        "hour": 11,
        "minute": 15,
        "second": 31,
        "chronology": {
          "id": "ISO",
          "calendarType": "iso8601"
        }
      },
      "updatedAt": null,
      "id": 10,
      "customerEmail": "basquiat_email_10",
      "customerName": "basquiat_name_10",
      "customerMobile": "my_mobile_10"
    },
    {
      "createdAt": {
        "month": "FEBRUARY",
        "dayOfWeek": "MONDAY",
        "dayOfYear": 39,
        "nano": 0,
        "year": 2021,
        "monthValue": 2,
        "dayOfMonth": 8,
        "hour": 11,
        "minute": 15,
        "second": 31,
        "chronology": {
          "id": "ISO",
          "calendarType": "iso8601"
        }
      },
      "updatedAt": null,
      "id": 9,
      "customerEmail": "basquiat_email_9",
      "customerName": "basquiat_name_9",
      "customerMobile": "my_mobile_9"
    },
    {
      "createdAt": {
        "month": "FEBRUARY",
        "dayOfWeek": "MONDAY",
        "dayOfYear": 39,
        "nano": 0,
        "year": 2021,
        "monthValue": 2,
        "dayOfMonth": 8,
        "hour": 11,
        "minute": 15,
        "second": 31,
        "chronology": {
          "id": "ISO",
          "calendarType": "iso8601"
        }
      },
      "updatedAt": null,
      "id": 8,
      "customerEmail": "basquiat_email_8",
      "customerName": "basquiat_name_8",
      "customerMobile": "my_mobile_8"
    },
    {
      "createdAt": {
        "month": "FEBRUARY",
        "dayOfWeek": "MONDAY",
        "dayOfYear": 39,
        "nano": 0,
        "year": 2021,
        "monthValue": 2,
        "dayOfMonth": 8,
        "hour": 11,
        "minute": 15,
        "second": 31,
        "chronology": {
          "id": "ISO",
          "calendarType": "iso8601"
        }
      },
      "updatedAt": null,
      "id": 7,
      "customerEmail": "basquiat_email_7",
      "customerName": "basquiat_name_7",
      "customerMobile": "my_mobile_7"
    },
    {
      "createdAt": {
        "month": "FEBRUARY",
        "dayOfWeek": "MONDAY",
        "dayOfYear": 39,
        "nano": 0,
        "year": 2021,
        "monthValue": 2,
        "dayOfMonth": 8,
        "hour": 11,
        "minute": 15,
        "second": 31,
        "chronology": {
          "id": "ISO",
          "calendarType": "iso8601"
        }
      },
      "updatedAt": null,
      "id": 6,
      "customerEmail": "basquiat_email_6",
      "customerName": "basquiat_name_6",
      "customerMobile": "my_mobile_6"
    },
    {
      "createdAt": {
        "month": "FEBRUARY",
        "dayOfWeek": "MONDAY",
        "dayOfYear": 39,
        "nano": 0,
        "year": 2021,
        "monthValue": 2,
        "dayOfMonth": 8,
        "hour": 11,
        "minute": 15,
        "second": 31,
        "chronology": {
          "id": "ISO",
          "calendarType": "iso8601"
        }
      },
      "updatedAt": null,
      "id": 5,
      "customerEmail": "basquiat_email_5",
      "customerName": "basquiat_name_5",
      "customerMobile": "my_mobile_5"
    },
    {
      "createdAt": {
        "month": "FEBRUARY",
        "dayOfWeek": "MONDAY",
        "dayOfYear": 39,
        "nano": 0,
        "year": 2021,
        "monthValue": 2,
        "dayOfMonth": 8,
        "hour": 11,
        "minute": 15,
        "second": 31,
        "chronology": {
          "id": "ISO",
          "calendarType": "iso8601"
        }
      },
      "updatedAt": null,
      "id": 4,
      "customerEmail": "basquiat_email_4",
      "customerName": "basquiat_name_4",
      "customerMobile": "my_mobile_4"
    },
    {
      "createdAt": {
        "month": "FEBRUARY",
        "dayOfWeek": "MONDAY",
        "dayOfYear": 39,
        "nano": 0,
        "year": 2021,
        "monthValue": 2,
        "dayOfMonth": 8,
        "hour": 11,
        "minute": 15,
        "second": 31,
        "chronology": {
          "id": "ISO",
          "calendarType": "iso8601"
        }
      },
      "updatedAt": null,
      "id": 3,
      "customerEmail": "basquiat_email_3",
      "customerName": "basquiat_name_3",
      "customerMobile": "my_mobile_3"
    },
    {
      "createdAt": {
        "month": "FEBRUARY",
        "dayOfWeek": "MONDAY",
        "dayOfYear": 39,
        "nano": 0,
        "year": 2021,
        "monthValue": 2,
        "dayOfMonth": 8,
        "hour": 11,
        "minute": 15,
        "second": 31,
        "chronology": {
          "id": "ISO",
          "calendarType": "iso8601"
        }
      },
      "updatedAt": null,
      "id": 2,
      "customerEmail": "basquiat_email_2",
      "customerName": "basquiat_name_2",
      "customerMobile": "my_mobile_2"
    },
    {
      "createdAt": {
        "month": "FEBRUARY",
        "dayOfWeek": "MONDAY",
        "dayOfYear": 39,
        "nano": 0,
        "year": 2021,
        "monthValue": 2,
        "dayOfMonth": 8,
        "hour": 11,
        "minute": 15,
        "second": 30,
        "chronology": {
          "id": "ISO",
          "calendarType": "iso8601"
        }
      },
      "updatedAt": {
        "month": "FEBRUARY",
        "dayOfWeek": "MONDAY",
        "dayOfYear": 39,
        "nano": 0,
        "year": 2021,
        "monthValue": 2,
        "dayOfMonth": 8,
        "hour": 11,
        "minute": 16,
        "second": 35,
        "chronology": {
          "id": "ISO",
          "calendarType": "iso8601"
        }
      },
      "id": 1,
      "customerEmail": "basquiat_email_1",
      "customerName": "basquiat_name_1",
      "customerMobile": "000-000-0002"
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "pageNumber": 1,
    "pageSize": 10,
    "offset": 10,
    "paged": true,
    "unpaged": false
  },
  "last": true,
  "totalPages": 2,
  "totalElements": 20,
  "numberOfElements": 10,
  "number": 1,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "first": false,
  "size": 10,
  "empty": false
}
```
json형식으로 로그를 찍으면 한줄로 길게 나와서 포멧을 적용해서 위에 로그를 붙여봤다.           

하지만 이 Pageable을 적용하게 되면 기존의 봐왔던 json형식과 뭔가 다르다는 것을 알 수 있다.    

테스트에서는 직접 jsonString으로 변환해서 인식을 못할 수 있는데 실제로

```
Page<Customer> page = customerRepository.findAll(pageable);
```
반환 타입이 다르기 때문이다.     

Page 인터페이스를 타고 들어가서 구현체인 PageImpl까지 따라가면 

```
/**
 * Constructor of {@code PageImpl}.
 *
 * @param content the content of this page, must not be {@literal null}.
 * @param pageable the paging information, must not be {@literal null}.
 * @param total the total amount of items available. The total might be adapted considering the length of the content
 *          given, if it is going to be the content of the last page. This is in place to mitigate inconsistencies.
 */
public PageImpl(List<T> content, Pageable pageable, long total) {

    super(content, pageable);

    this.total = pageable.toOptional().filter(it -> !content.isEmpty())//
            .filter(it -> it.getOffset() + it.getPageSize() > total)//
            .map(it -> it.getOffset() + content.size())//
            .orElse(total);
}
```
에서 알수 있듯이 위와 같은 방식으로 반환을 한다.

또한 나 모르게 몰래 count쿼리를 날리는 것을 볼 수 있다.      
```
Hibernate: 
    /* select
        count(generatedAlias0) 
    from
        Customer as generatedAlias0 */ select
            count(customer0_.id) as col_0_0_ 
        from
            basquiat.basquiat_customer customer0_
```
jpa의 스펙을 활용해서 페이징을 처리하게 될 경우 위와 같이 페이징 처리 정보와 관련된 내용을 채우기 위해서 저렇게 뒤로 몰래 쿼리를 한번 더 날린다.    

지금이야 어떤 조인도 걸려있지 않아서 이것이 문제될것이 없지만 실무에서 만일 꽤 많은 테이블들과의 조인을 하게 되는 경우가 있을텐데 만일 이것을 그대로 사용하게 된다면 속도에 문제가 발생할 수 있다.      

예를 들면 A라는 테이블을 기준으로 B,C,D,E라는 테이블과 조인이 걸려 있다고 생각을 해보자.      

카운트의 경우에는 물론 where절에서 어떤 방식으로 조회되느냐에 따라서 조인된 상태에서 카운트 쿼리를 가져와야 하는 경우도 있지만 굳이 저 테이블들과 조인하지 않아도 되는 경우도 있을 수 있기 때문에 복잡한 조인을 하는 경우에는 카운터 쿼리를 따로 날리게 해야한다.      

만일 실제 조회 쿼리와 카운트 쿼리가 성능 최적화를 위해 카운트 쿼리를 따로 날려야 하는 경우라면 지금 딱 떠오르는 방법은 @Query를 이용해 JPQL로 작성을 하고 countQuery 속성에 카운트 쿼리를 따로 작성하는 방법이다.    

범위에 벗어나긴 하지만 그냥 내친김에 한번 코드를 짜보자.     

```
package io.basquiat.customer.repository;

import io.basquiat.common.repository.BaseRepository;
import io.basquiat.customer.model.entity.Customer;
import io.basquiat.customer.repository.custom.QueryCustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * customer repository
 * created by basquiat
 */
public interface CustomerRepository extends BaseRepository<Customer, Long>, QueryCustomerRepository {

    @EntityGraph(attributePaths = "addresses")
    @Query(value = "SELECT c FROM Customer c", countQuery = "SELECT COUNT(c) FROM Customer c WHERE c.id > 5")
    Page<Customer> findAllQuery(Pageable pageable);

    @EntityGraph(attributePaths = "addresses")
    @Query(value = "SELECT c FROM Customer c")
    Page<Customer> findAllQueryAndCountQuery(Pageable pageable);

    @Query(value = "SELECT DISTINCT c FROM Customer c left join fetch c.addresses", countQuery = "SELECT COUNT(c) FROM Customer c")
    Page<Customer> findAllQueryAndCountQueryOne(Pageable pageable);

}
``` 
JPQL을 활요해서 하는 방식과 @EntityGraph를 이용해서 counQuery가 없는 녀석과 하나는 countQuery를 따로 작성한 메소드 그리고  두개 만들어 놓고 한번 테스트 해보자.

JPQL의 join fetch는 기본적으로 위에서 언급했듯이 inner join이기 때문에 현재 데이터를 기준으로 뭔 짓을 해도 주소가 존재하는 한명의 고객만 조회되거나 페이징에 따라서 null로 반환된다.     

그래서 left join fetch를 사용했으며 join fetch 사용과 page and sort를 같이 사용할 경우 countQuery를 작성하지 않으면 

```
query specified join fetching, but the owner of the fetched association was not present in the select list...
```
요런 뭐시기 에러를 마주하게 된다.

@EntityGraph의 경우에는 기본적으로 left join을 수행하게 된다.     


```
@Test
public void selectSimpleCustomerQuery() {
    //int page = 0; // 첫번째 페이지
    int page = 1; // 두번째 페이지
    int size = 10; // 10개씩 보여주기
    Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending()); // 내림차순
    //Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending()); // 오름차순
    System.out.println(convertJsonStringFromObject(customerRepository.findAllQuery(pageable)));
}

result:
Hibernate: 
    /* SELECT
        c 
    FROM
        Customer c 
    order by
        c.id desc */ select
            customer0_.id as id1_1_0_,
            addresses1_.id as id1_0_1_,
            customer0_.created_at as created_2_1_0_,
            customer0_.updated_at as updated_3_1_0_,
            customer0_.customer_email as customer4_1_0_,
            customer0_.customer_mobile as customer5_1_0_,
            customer0_.customer_name as customer6_1_0_,
            addresses1_.created_at as created_2_0_1_,
            addresses1_.updated_at as updated_3_0_1_,
            addresses1_.is_default as is_defau4_0_1_,
            addresses1_.addr_detail as addr_det5_0_1_,
            addresses1_.addr as addr6_0_1_,
            addresses1_.addr_name as addr_nam7_0_1_,
            addresses1_.addr_postcode as addr_pos8_0_1_,
            addresses1_.customer_id as customer9_0_1_,
            addresses1_.customer_id as customer9_0_0__,
            addresses1_.id as id1_0_0__ 
        from
            basquiat.basquiat_customer customer0_ 
        left outer join
            basquiat.basquiat_address addresses1_ 
                on customer0_.id=addresses1_.customer_id 
        order by
            customer0_.id desc

Hibernate: 
    /* SELECT
        COUNT(c) 
    FROM
        Customer c 
    WHERE
        c.id > 5 */ select
            count(customer0_.id) as col_0_0_ 
        from
            basquiat.basquiat_customer customer0_ 
        where
            customer0_.id>5

@Test
public void selectSimpleCustomerQueryAndCountQuery() {
    //int page = 0; // 첫번째 페이지
    int page = 1; // 두번째 페이지
    int size = 10; // 10개씩 보여주기
    Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending()); // 내림차순
    //Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending()); // 오름차순
    System.out.println(convertJsonStringFromObject(customerRepository.findAllQueryAndCountQuery(pageable)));
}

result:
Hibernate: 
    /* SELECT
        c 
    FROM
        Customer c 
    order by
        c.id desc */ select
            customer0_.id as id1_1_0_,
            addresses1_.id as id1_0_1_,
            customer0_.created_at as created_2_1_0_,
            customer0_.updated_at as updated_3_1_0_,
            customer0_.customer_email as customer4_1_0_,
            customer0_.customer_mobile as customer5_1_0_,
            customer0_.customer_name as customer6_1_0_,
            addresses1_.created_at as created_2_0_1_,
            addresses1_.updated_at as updated_3_0_1_,
            addresses1_.is_default as is_defau4_0_1_,
            addresses1_.addr_detail as addr_det5_0_1_,
            addresses1_.addr as addr6_0_1_,
            addresses1_.addr_name as addr_nam7_0_1_,
            addresses1_.addr_postcode as addr_pos8_0_1_,
            addresses1_.customer_id as customer9_0_1_,
            addresses1_.customer_id as customer9_0_0__,
            addresses1_.id as id1_0_0__ 
        from
            basquiat.basquiat_customer customer0_ 
        left outer join
            basquiat.basquiat_address addresses1_ 
                on customer0_.id=addresses1_.customer_id 
        order by
            customer0_.id desc
Hibernate: 
    /* SELECT
        COUNT(c) 
    FROM
        Customer c 
    WHERE
        c.id > 5 */ select
            count(customer0_.id) as col_0_0_ 
        from
            basquiat.basquiat_customer customer0_ 
        where
            customer0_.id>5

@Test
public void selectSimpleCustomerQueryAndCountQueryJoinFetch() {
    //int page = 0; // 첫번째 페이지
    int page = 0; // 두번째 페이지
    int size = 10; // 10개씩 보여주기
    Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending()); // 내림차순
    //Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending()); // 오름차순
    System.out.println(convertJsonStringFromObject(customerRepository.findAllQueryAndCountQueryOne(pageable)));
}

result:
Hibernate: 
    /* SELECT
        DISTINCT c 
    FROM
        Customer c 
    left join
        fetch c.addresses 
    order by
        c.id desc */ select
            distinct customer0_.id as id1_1_0_,
            addresses1_.id as id1_0_1_,
            customer0_.created_at as created_2_1_0_,
            customer0_.updated_at as updated_3_1_0_,
            customer0_.customer_email as customer4_1_0_,
            customer0_.customer_mobile as customer5_1_0_,
            customer0_.customer_name as customer6_1_0_,
            addresses1_.created_at as created_2_0_1_,
            addresses1_.updated_at as updated_3_0_1_,
            addresses1_.is_default as is_defau4_0_1_,
            addresses1_.addr_detail as addr_det5_0_1_,
            addresses1_.addr as addr6_0_1_,
            addresses1_.addr_name as addr_nam7_0_1_,
            addresses1_.addr_postcode as addr_pos8_0_1_,
            addresses1_.customer_id as customer9_0_1_,
            addresses1_.customer_id as customer9_0_0__,
            addresses1_.id as id1_0_0__ 
        from
            basquiat.basquiat_customer customer0_ 
        left outer join
            basquiat.basquiat_address addresses1_ 
                on customer0_.id=addresses1_.customer_id 
        order by
            customer0_.id desc
Hibernate: 
    /* SELECT
        COUNT(c) 
    FROM
        Customer c */ select
            count(customer0_.id) as col_0_0_ 
        from
            basquiat.basquiat_customer customer0_
```
테스트 결과를 토대로 countQuery를 정의하면 그에 맞춰서 쿼리 카운트가 날아가는 것을 알 수 있다.

이와 관련해서 다음과 같이 left join 을 그냥 기본으로 inner join으로 테스트를 진행해 보면

```
    @Query(value = "SELECT DISTINCT c FROM Customer c join fetch c.addresses", countQuery = "SELECT COUNT(c) FROM Customer c")
    Page<Customer> findAllQueryAndCountQueryOne(Pageable pageable);
```
내부적으로 최적화를 진행해서 쿼리를 날리지 않는다.

이제는 queryDSL을 한번 살펴보기로 하자. 

# queryDSL을 활용한 심플한 페이징
참고로 이전 깃헙에서도 언급했고 이 이야기는 어디에서도 들을 수 있는 말이긴 한데 queryDSL은 결국 JPQL을 편하게 쓰기 위한 query builder이다.     

이 말인즉, JPQL에서 안되면 queryDSL에서도 안되고 결국 queryDSL은 JPQL이다~~ 라고 생각해야 한다.     

이제부터 기존에 만들어 논 QueryCustomerRepository와 구현체에서 계속 작성을 해보자.     

역시 위에서 진행했던 저 위에 것들을 한번 그대로 진행하자.

```
List<Customer> findAllCustomer(Pageable pageable);
```
를 추가하고 구현체를 만들자.

```
@Override
public List<Customer> findAllCustomer(Pageable pageable) {
    JPAQuery<Customer> customerQuery = query.selectFrom(customer)
                                            .orderBy(customer.id.desc());
    if(pageable != null) {
        customerQuery.offset(pageable.getOffset())
                     .limit(pageable.getPageSize());
    }
    return customerQuery.fetch();
}
```
정말 간단하다.     

멀티 컬럼 소팅은 

```
.orderBy(customer.id.desc(), customer.customerName.asc());
```      
다음과 같이 작성하면 된다.      

어 근데 저는 저 orderBy도 뭔가.... 동적으로 만들고 싶은데요???      

사실 대부분 이런 소팅의 경우에는 고정된 경우가 많지만 이런 요건사항이 오면 당연히 다이나믹하게 만들고 싶어진다.     

그럼 이제 이 부분도 한번 해보자.

.orderBy()를 따라가다 보면
```

 /**
 * Add order expressions
 *
 * @param o order
 * @return the current object
 */
public Q orderBy(OrderSpecifier<?>... o) {
    return queryMixin.orderBy(o);
}
```
spread syntax를 활용하고 있다. 하긴 

```
.orderBy(customer.id.desc(), customer.customerName.asc());
```
이 코드를 보면 알겠지만 구분자 ','로 spread sytax로 파라미터를 받는다.     

자 그럼 이제 우리는 이것을 활용해 볼 생각이다.

CustomerSqlExtension.java에 위임할 메소드를 작성하자.

```
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
```
spread syntax로 파라미터를 받기 때문에 최종적으로는 OrderSpecifier[] 형태로 반환한다.      

앞서 Pageable객체를 생성할때 소트와 관련된 부분에서 정보를 빼내와 루프를 돌면서 순서대로 다이나믹하게 만들어 내는게 목적이다.      

그리고 이제 테스트를 해보자.

```
@Test
public void queryDSLWithPaginationWithDynamicSOrt() {
    int page = 0; // 첫번째 페이지
    //int page = 1; // 두번째 페이지
    int size = 10; // 10개씩 보여주기
    Pageable pageable = PageRequest.of(page, size);
    //Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending().and(Sort.by("customerName").ascending())); // 오름차순
    System.out.println(convertJsonStringFromObject(customerRepository.findAllCustomerWithDynamicSort(pageable)));
}
```
실제로 해당 테스트를 위 주석 처리 부분을 변경하면서 테스팅하면 넘어온 정보에 따라서 Sort정보가 없다면 orderBy를 하지 않을 것이다.      

위에서처럼 멀티 컬럼으로 소팅을 할 경우에는 그에 맞춰서 쿼리가 작성되서 나가는 것을 확인할 수 있다.      

사실 이런 식의 코드는 사용할 일이 없어서 소팅 부분은 다이나믹하게 처리해 본적이 없는데 한번 작성해 봤다. ~~잘돼네???~~     

자...여기까지는 정말 정석적인 코드이다. 물론 페이징 처리, 소트도 참 잘된다.       

그러나 실제 서비스되는 어플리케이션이 이럴리가 없다. 절대로      

# 바보같은 생각

회원관리 화면을 한번 생각해보자.

어떤 그리드에는 회원 정보가 나올것이고 회원 상세 정보를 보기 위해 어떤 액션을 하게 되면 상세 페이지로 넘어갈 것이다.     

이때 상세 페이지에는 회원의 정보와 주소 리스트를 가져와야 한다.     

처음 JPA를 막 시작할 때는 이런 생각을 했었다.

'특정 회원을 조회할때 주소 정보를 가져온다. 페이징 처리는 5개씩 보여주기로 하자.'       

뭐 사실 8개로 제한한 마당에 페이징 처리가 필요하겠냐마는 어째든 회원이 아닌 다른 도메인을 떠올려봐도 될 것이다.      

그전에 지금 테스트 데이터는 마지막 생성한 회원의 경우에 주소 리스트가 딸랑 하나이기 때문에 유의미한 데이터를 밀어 넣어보자.     

하지만 지금 상태에서 다음과 같이 총 8개까지 가질 수 있으니 다음과 같이 7개의 데이터를 새로 밀어넣을려고 한다면

```
Optional<Customer> optional = customerRepository.findById(customerId);
Customer customer = optional.get();
IntStream.range(1, 8)
         .forEach(i -> {
            Address address = Address.builder()
                                     .addressName("대표 주소_" + i)
                                     .addressPostcode("postcode_" + i)
                                     .addressInfo("나는 서울에서 산다._" + i)
                                     .addressDetail("서울에 있는 내집_" + i)
                                     .addressDefaultStatus(Address.AddressDefaultStatus.N)
                                     .customer(customer)
                                     .build();
            customerRepository.save(customer);
         });
``` 

```
PersistentObjectException: detached entity passed to persist:
```

이런 예러를 만나게 된다. 이유은 우리가 회원을 처음 생성할 때 주소를 생성하면서 함께 들어갈 수 있도록 Address엔티티에서 영속성 전이 옵션을 줬었다.    

보통은 CascadeType.ALL 또는 CascadeType.PERSIST나 배열로 필요한 몇개만 넣었을 텐데 이 옵션때문에 발생한다.     

근데 새로 생성하는건 문제가 안되는데 기존의 Customer정보가 존재하는 경우 Spring-Data-jpa에서 중복으로 에러를 발생하기 때문이다.     

해결법은 두가지이다. 현재의 옵션을 ALL이나 PERSIST가 아닌 MERGE/DETACH로 설정하거나 아예 옵션을 주지 않으면 된다.     

하지만 이건 처음 액션을 위해 줬던 옵션이기 때문에 이럴 경우에는 초기 방법으로 한번에 데이터를 생성할 수 없다.     

그래서 나는 다음과 같이 Service객체를 하나 만들었다.     

```
package io.basquiat.customer.service;

import io.basquiat.customer.model.entity.Address;
import io.basquiat.customer.model.entity.Customer;
import io.basquiat.customer.model.vo.AddressVo;
import io.basquiat.customer.repository.AddressRepository;
import io.basquiat.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AddressService {

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void save(Long customerId, AddressVo addressVo) {
        Optional<Customer> optional = customerRepository.findById(customerId);
        Customer customer = optional.get();
        customer = em.merge(customer);
        Address address = Address.builder()
                                 .addressName(addressVo.getName())
                                 .addressPostcode(addressVo.getPostcode())
                                 .addressInfo(addressVo.getInfo())
                                 .addressDetail(addressVo.getDetail())
                                 .addressDefaultStatus(addressVo.getAddressDefaultStatus())
                                 .customer(customer)
                                 .build();
        addressRepository.save(address);
    }

}
```
다음과 같이 하이버네이트의 EntityManager객체를 통해서 detach를 시키거나 엔티티를 merge화 시켜서 넣는 방법이 있다.      

여러분의 선택은 둘 중 하나를 선택해야 한다.      

1. 현재 엔티티의 옵션을 변경한다.
2. 현재 옵션을 그대로 간다면 위와 같이 헤당 엔티티의 영속성 컨텍스트 상태를 merge로 코드레벨로 변경한다.

나는 2번을 선택했다.     

```
@Test
public void insertAddress_Two() {
    IntStream.range(1, 8)
             .forEach(i -> {
                     Long customerId = 20L;
                     AddressVo addressVo = new AddressVo();
                     addressVo.setName("새로운 주소_" + i);
                     addressVo.setPostcode("새로운우편번호_" + i);
                     addressVo.setInfo("서울");
                     addressVo.setDetail("우리집_" + i);
                     addressVo.setAddressDefaultStatus(Address.AddressDefaultStatus.N);
                     addressService.save(customerId, addressVo);
             });
}
```

어째든 코딩을 하기 시작하자.    

id로 검색 조건이 없으니 CustomerSqlExtension.java에 다음을 넣는다.     

실제로 화면상에서 api로 요청할 때는 id로 던질것이기 때문이다.     
```
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
```

나의 생각은 이렇다.

```
SELECT customer.*,
       addresses.*
    FROM basquiat.basquiat_customer customer 
    LEFT JOIN basquiat.basquiat_address addresses ON customer.id = addresses.customer_id 
   WHERE customer.id = 20 
   ORDER BY addresses.id desc
   LIMIT 5;
```
쿼리로는 이렇게 생각을 했다. 이렇게 하면 원하는 데이터를 아래 이미지처럼 수집할 수 있기 때문에 

![실행이미지](https://github.com/basquiat78/spring-boot-querydsl/blob/query-dsl-paging/capture/capture1.png)


```
CustomerDto findCustomerWithAddressList(Long customerId, Pageable pageable);
```

```
@Override
public CustomerDto findCustomerWithAddressList(Long customerId, Pageable pageable) {
    JPAQuery<Customer> customerQuery = query.selectFrom(customer)
                                            .leftJoin(customer.addresses, address)
                                            .fetchJoin()
                                            .where(
                                                customer.condCustomerById(customerId)
                                            )
                                            .orderBy(address.id.desc());
    if(pageable != null) {
        customerQuery.offset(pageable.getOffset())
                     .limit(pageable.getPageSize());
    }
    Customer selected = customerQuery.fetchOne();
    return new CustomerDto(selected, selected.getAddresses());
}
```
다음과 같이 작성을 했다. fetchJoin은 N+1문제를 회피하기 하기 위해 한번에 가져와서 페이징을 처리하겠다는 의미였다.     

하지만 결과는?

```
@Test
public void queryDSLWithAddressList() {
    int page = 0; // 첫번째 페이지
    int size = 5; // 5개씩 보여주기
    Long customerId = 20L;
    Pageable pageable = PageRequest.of(page, size);
    CustomerDto selected = customerRepository.findCustomerWithAddressList(customerId, pageable);
    System.out.println(convertJsonStringFromObject(selected));
}

result:

HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!

Hibernate: 
    /* select
        customer 
    from
        Customer customer   
    left join
        fetch customer.addresses as address 
    where
        customer.id = ?1 
    order by
        address.id desc */ select
            customer0_.id as id1_1_0_,
            addresses1_.id as id1_0_1_,
            customer0_.created_at as created_2_1_0_,
            customer0_.updated_at as updated_3_1_0_,
            customer0_.customer_email as customer4_1_0_,
            customer0_.customer_mobile as customer5_1_0_,
            customer0_.customer_name as customer6_1_0_,
            addresses1_.created_at as created_2_0_1_,
            addresses1_.updated_at as updated_3_0_1_,
            addresses1_.is_default as is_defau4_0_1_,
            addresses1_.addr_detail as addr_det5_0_1_,
            addresses1_.addr as addr6_0_1_,
            addresses1_.addr_name as addr_nam7_0_1_,
            addresses1_.addr_postcode as addr_pos8_0_1_,
            addresses1_.customer_id as customer9_0_1_,
            addresses1_.customer_id as customer9_0_0__,
            addresses1_.id as id1_0_0__ 
        from
            basquiat.basquiat_customer customer0_ 
        left outer join
            basquiat.basquiat_address addresses1_ 
                on customer0_.id=addresses1_.customer_id 
        where
            customer0_.id=? 
        order by
            addresses1_.id desc


{
  "addresses": [
    {
      "createdAt": {
        "month": "FEBRUARY",
        "dayOfWeek": "THURSDAY",
        "dayOfYear": 42,
        "nano": 0,
        "year": 2021,
        "monthValue": 2,
        "dayOfMonth": 11,
        "hour": 13,
        "minute": 59,
        "second": 38,
        "chronology": {
          "id": "ISO",
          "calendarType": "iso8601"
        }
      },
      "updatedAt": null,
      "address_id": 8,
      "addr_name": "새로운 주소_7",
      "addr_postcode": "새로운우편번호_7",
      "addr_info": "서울",
      "addr_detail": "우리집_7",
      "default": "N"
    },
    {
      "createdAt": {
        "month": "FEBRUARY",
        "dayOfWeek": "THURSDAY",
        "dayOfYear": 42,
        "nano": 0,
        "year": 2021,
        "monthValue": 2,
        "dayOfMonth": 11,
        "hour": 13,
        "minute": 59,
        "second": 38,
        "chronology": {
          "id": "ISO",
          "calendarType": "iso8601"
        }
      },
      "updatedAt": null,
      "address_id": 7,
      "addr_name": "새로운 주소_6",
      "addr_postcode": "새로운우편번호_6",
      "addr_info": "서울",
      "addr_detail": "우리집_6",
      "default": "N"
    },
    {
      "createdAt": {
        "month": "FEBRUARY",
        "dayOfWeek": "THURSDAY",
        "dayOfYear": 42,
        "nano": 0,
        "year": 2021,
        "monthValue": 2,
        "dayOfMonth": 11,
        "hour": 13,
        "minute": 59,
        "second": 38,
        "chronology": {
          "id": "ISO",
          "calendarType": "iso8601"
        }
      },
      "updatedAt": null,
      "address_id": 6,
      "addr_name": "새로운 주소_5",
      "addr_postcode": "새로운우편번호_5",
      "addr_info": "서울",
      "addr_detail": "우리집_5",
      "default": "N"
    },
    {
      "createdAt": {
        "month": "FEBRUARY",
        "dayOfWeek": "THURSDAY",
        "dayOfYear": 42,
        "nano": 0,
        "year": 2021,
        "monthValue": 2,
        "dayOfMonth": 11,
        "hour": 13,
        "minute": 59,
        "second": 38,
        "chronology": {
          "id": "ISO",
          "calendarType": "iso8601"
        }
      },
      "updatedAt": null,
      "address_id": 5,
      "addr_name": "새로운 주소_4",
      "addr_postcode": "새로운우편번호_4",
      "addr_info": "서울",
      "addr_detail": "우리집_4",
      "default": "N"
    },
    {
      "createdAt": {
        "month": "FEBRUARY",
        "dayOfWeek": "THURSDAY",
        "dayOfYear": 42,
        "nano": 0,
        "year": 2021,
        "monthValue": 2,
        "dayOfMonth": 11,
        "hour": 13,
        "minute": 59,
        "second": 38,
        "chronology": {
          "id": "ISO",
          "calendarType": "iso8601"
        }
      },
      "updatedAt": null,
      "address_id": 4,
      "addr_name": "새로운 주소_3",
      "addr_postcode": "새로운우편번호_3",
      "addr_info": "서울",
      "addr_detail": "우리집_3",
      "default": "N"
    },
    {
      "createdAt": {
        "month": "FEBRUARY",
        "dayOfWeek": "THURSDAY",
        "dayOfYear": 42,
        "nano": 0,
        "year": 2021,
        "monthValue": 2,
        "dayOfMonth": 11,
        "hour": 13,
        "minute": 59,
        "second": 38,
        "chronology": {
          "id": "ISO",
          "calendarType": "iso8601"
        }
      },
      "updatedAt": null,
      "address_id": 3,
      "addr_name": "새로운 주소_2",
      "addr_postcode": "새로운우편번호_2",
      "addr_info": "서울",
      "addr_detail": "우리집_2",
      "default": "N"
    },
    {
      "createdAt": {
        "month": "FEBRUARY",
        "dayOfWeek": "THURSDAY",
        "dayOfYear": 42,
        "nano": 0,
        "year": 2021,
        "monthValue": 2,
        "dayOfMonth": 11,
        "hour": 13,
        "minute": 59,
        "second": 38,
        "chronology": {
          "id": "ISO",
          "calendarType": "iso8601"
        }
      },
      "updatedAt": null,
      "address_id": 2,
      "addr_name": "새로운 주소_1",
      "addr_postcode": "새로운우편번호_1",
      "addr_info": "서울",
      "addr_detail": "우리집_1",
      "default": "N"
    },
    {
      "createdAt": {
        "month": "FEBRUARY",
        "dayOfWeek": "MONDAY",
        "dayOfYear": 39,
        "nano": 0,
        "year": 2021,
        "monthValue": 2,
        "dayOfMonth": 8,
        "hour": 11,
        "minute": 18,
        "second": 44,
        "chronology": {
          "id": "ISO",
          "calendarType": "iso8601"
        }
      },
      "updatedAt": null,
      "address_id": 1,
      "addr_name": "대표 주소",
      "addr_postcode": "postcode",
      "addr_info": "나는 서울에서 산다.",
      "addr_detail": "서울에 있는 내집",
      "default": "Y"
    }
  ],
  "createdAt": {
    "month": "FEBRUARY",
    "dayOfWeek": "MONDAY",
    "dayOfYear": 39,
    "nano": 0,
    "year": 2021,
    "monthValue": 2,
    "dayOfMonth": 8,
    "hour": 11,
    "minute": 18,
    "second": 44,
    "chronology": {
      "id": "ISO",
      "calendarType": "iso8601"
    }
  },
  "updatedAt": null,
  "customer_id": 20,
  "email": "basquiat_email",
  "name": null,
  "mobile": "my_mobile"
}
```
어라? 근데 나간 쿼리에 limt가 빠져있고 실제 가져온 정보는 8개의 정보를 전부 가져왔다.

````
HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!
````
이런 경고성 로그도 볼 수 있다. 내용의 요지는 페이징된 것이 없으니 릴레이션 정보를 전부 가져와서 메모리에 올린다는 것이다.    

이 페이징은 customer의 결과를 페이징 처리하게 되어 있다.       

결국 customer 테이블을 기준으로 페이징을 처리하고 address테이블에서는 풀스캔이 발생한 것이다.      

물론 지금같은 경우에는 성능에 얼마나 무리를 주겠냐마는 만일 고객과 주문테이블이 있다고 생각한다면 전혀 달라질 것이다.      

실제로 queryDSL을 처음 배우고 프로젝트를 할 때 이 문제때문에 몇일을 고생한 적이 있었다.     

'아니 왜 안돼? 뭐가 문제인데??????'      

이 이슈로 고민하고 있는데 같이 프로젝트했던 사수님이 보시더니     

'야 그럼 이걸 Address에서 생각하면 해결되지 않겠어?? 딱 봐도 그런데?'     

생각의 방향을 customer를 기준으로 쿼리로 그 결과를 상상하며 코딩을 했던 나에게는 머리를 한대 맞은 느낌이었다.     

실제로 JPA의 경우 @OneToMany인 경우 fetchJoin을 사용할때 이 offset/limit를 사용할 때는 주의를 요하고 있다.    

또한 LIMIT라는 예약어는 어떻게 보면 특정 rdbms에 종속된 예약어로 실제 JPQL에서는 LIMIT를 사용할 수 없다. 

따라서 회원 관리 페이지 시나리오를 정리를 해보자.

1. 회원 리스트 화면에 진입시 회원의 정보만 가져온다.

2. 특정 회원을 클릭해서 상세 정보를 볼때는 그 회원의 주소 정보를 요청해서 가져온다.

3. 이 때 요청하는 정보는 Address도메인을 기준으로 하자.

그냥 고객 아이디로 조회해서 페이징 처리를 해도 되기 때문이다.

첫 번째는 JPQL을 활용해서 한번 해 보자.

```
package io.basquiat.customer.repository;

import io.basquiat.common.repository.BaseRepository;
import io.basquiat.customer.model.entity.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * address repository
 * created by basquiat
 */
public interface AddressRepository extends BaseRepository<Address, Long> {

    @Query(value = "SELECT a FROM Address a JOIN FETCH a.customer WHERE a.customer.id = :customerId ORDER BY a.id DESC",
           countQuery = "SELECT COUNT(a) FROM Address a WHERE a.customer.id = :customerId")
    Page<Address> findAddressListByCustomerId(@Param("customerId")Long customerId, Pageable pageable);

}
```
위에서 언급했던 내용인데 countQuery의 경우에는 지금처럼 JOIN FETCH를 사용했다면 카운트 쿼리를 따로 작성해야 한다.     

자 이렇게 해서 테스트를 해보자.

```
package io.basquiat.customer;

import io.basquiat.customer.model.entity.Address;
import io.basquiat.customer.model.entity.Customer;
import io.basquiat.customer.model.vo.AddressVo;
import io.basquiat.customer.repository.AddressRepository;
import io.basquiat.customer.repository.CustomerRepository;
import io.basquiat.customer.service.AddressService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
public class AddressRepositoryTest {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressService addressService;

.
.
.
.


    @Test
    public void getAddressListByCustomerId() {
        Long customerId = 20L;
        int page = 0; // 첫번째 페이지
        int size = 5; // 5개씩 보여주기
        Pageable pageable = PageRequest.of(page, size);
        Page<Address> addresses = addressRepository.findAddressListByCustomerId(customerId, pageable);
        System.out.println(addresses.getContent());
        System.out.println(addresses.getPageable());

    }

}

result: 

Hibernate: 
    /* SELECT
        a 
    FROM
        Address a 
    JOIN
        FETCH a.customer 
    WHERE
        a.customer.id = :customerId 
    ORDER BY
        a.id DESC */ select
            address0_.id as id1_0_0_,
            customer1_.id as id1_1_1_,
            address0_.created_at as created_2_0_0_,
            address0_.updated_at as updated_3_0_0_,
            address0_.is_default as is_defau4_0_0_,
            address0_.addr_detail as addr_det5_0_0_,
            address0_.addr as addr6_0_0_,
            address0_.addr_name as addr_nam7_0_0_,
            address0_.addr_postcode as addr_pos8_0_0_,
            address0_.customer_id as customer9_0_0_,
            customer1_.created_at as created_2_1_1_,
            customer1_.updated_at as updated_3_1_1_,
            customer1_.customer_email as customer4_1_1_,
            customer1_.customer_mobile as customer5_1_1_,
            customer1_.customer_name as customer6_1_1_ 
        from
            basquiat.basquiat_address address0_ 
        inner join
            basquiat.basquiat_customer customer1_ 
                on address0_.customer_id=customer1_.id 
        where
            address0_.customer_id=? 
        order by
            address0_.id DESC limit ?
Hibernate: 
    /* SELECT
        COUNT(a) 
    FROM
        Address a 
    WHERE
        a.customer.id = :customerId */ select
            count(address0_.id) as col_0_0_ 
        from
            basquiat.basquiat_address address0_ 
        where
            address0_.customer_id=?
2021-02-13 15:36:32.416 TRACE 35406 --- [    Test worker] o.h.type.descriptor.sql.BasicBinder      : binding parameter [1] as [BIGINT] - [20]
2021-02-13 15:36:32.418 TRACE 35406 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([col_0_0_] : [BIGINT]) - [8]
2021-02-13 15:36:32.418  INFO 35406 --- [    Test worker] i.StatisticalLoggingSessionEventListener : Session Metrics {
    50992 nanoseconds spent acquiring 1 JDBC connections;
    0 nanoseconds spent releasing 0 JDBC connections;
    293745 nanoseconds spent preparing 1 JDBC statements;
    849116 nanoseconds spent executing 1 JDBC statements;
    0 nanoseconds spent executing 0 JDBC batches;
    0 nanoseconds spent performing 0 L2C puts;
    0 nanoseconds spent performing 0 L2C hits;
    0 nanoseconds spent performing 0 L2C misses;
    0 nanoseconds spent executing 0 flushes (flushing a total of 0 entities and 0 collections);
    0 nanoseconds spent executing 0 partial-flushes (flushing a total of 0 entities and 0 collections)
}
[Address(id=8, addressName=새로운 주소_7, addressPostcode=새로운우편번호_7, addressInfo=서울, addressDetail=우리집_7, addressDefaultStatus=N), Address(id=7, addressName=새로운 주소_6, addressPostcode=새로운우편번호_6, addressInfo=서울, addressDetail=우리집_6, addressDefaultStatus=N), Address(id=6, addressName=새로운 주소_5, addressPostcode=새로운우편번호_5, addressInfo=서울, addressDetail=우리집_5, addressDefaultStatus=N), Address(id=5, addressName=새로운 주소_4, addressPostcode=새로운우편번호_4, addressInfo=서울, addressDetail=우리집_4, addressDefaultStatus=N), Address(id=4, addressName=새로운 주소_3, addressPostcode=새로운우편번호_3, addressInfo=서울, addressDetail=우리집_3, addressDefaultStatus=N)]
Page request [number: 0, size 5, sort: UNSORTED]

```
원하는 데이터가 나왔다.      

이제는 queryDSL로 한번 바꿔보자.      

지금까지 해왔던 대로

QueryAddressRepository.java를 만들고 
```
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

}

```

이 녀석을 구현한 QueryAddressRepositoryImpl.java를 만들고

```
package io.basquiat.customer.repository.custom;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.basquiat.customer.model.entity.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

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
        return addressQuery.fetch();
    }

}

```

AddressRepository.java를 다음과 같이 수정한다.
```
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

```

나중을 위해

```
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
```
AddressSqlExtension를 따로 만들어 줬다. 이 경우에는 다시 build > clean, other > compileQuerydsl을 해주는 것을 잊지 말자.    


테스트를 해보면 


```
@Test
    public void getAddressListByCustomerIdUsingQueryDSL() {
        Long customerId = 20L;
        int page = 0; // 첫번째 페이지
        int size = 5; // 5개씩 보여주기
        Pageable pageable = PageRequest.of(page, size);
        List<Address> addressList = addressRepository.findAddressList(customerId, pageable);
        System.out.println(addressList);
    }

result:
Hibernate: 
    /* select
        address 
    from
        Address address   
    inner join
        fetch address.customer as customer 
    where
        address.customer.id = ?1 
    order by
        address.id desc */ select
            address0_.id as id1_0_0_,
            customer1_.id as id1_1_1_,
            address0_.created_at as created_2_0_0_,
            address0_.updated_at as updated_3_0_0_,
            address0_.is_default as is_defau4_0_0_,
            address0_.addr_detail as addr_det5_0_0_,
            address0_.addr as addr6_0_0_,
            address0_.addr_name as addr_nam7_0_0_,
            address0_.addr_postcode as addr_pos8_0_0_,
            address0_.customer_id as customer9_0_0_,
            customer1_.created_at as created_2_1_1_,
            customer1_.updated_at as updated_3_1_1_,
            customer1_.customer_email as customer4_1_1_,
            customer1_.customer_mobile as customer5_1_1_,
            customer1_.customer_name as customer6_1_1_ 
        from
            basquiat.basquiat_address address0_ 
        inner join
            basquiat.basquiat_customer customer1_ 
                on address0_.customer_id=customer1_.id 
        where
            address0_.customer_id=? 
        order by
            address0_.id desc limit ?

[Address(id=8, addressName=새로운 주소_7, addressPostcode=새로운우편번호_7, addressInfo=서울, addressDetail=우리집_7, addressDefaultStatus=N), Address(id=7, addressName=새로운 주소_6, addressPostcode=새로운우편번호_6, addressInfo=서울, addressDetail=우리집_6, addressDefaultStatus=N), Address(id=6, addressName=새로운 주소_5, addressPostcode=새로운우편번호_5, addressInfo=서울, addressDetail=우리집_5, addressDefaultStatus=N), Address(id=5, addressName=새로운 주소_4, addressPostcode=새로운우편번호_4, addressInfo=서울, addressDetail=우리집_4, addressDefaultStatus=N), Address(id=4, addressName=새로운 주소_3, addressPostcode=새로운우편번호_3, addressInfo=서울, addressDetail=우리집_3, addressDefaultStatus=N)
```

원하는 정보가 나왔다.      

하지만 특정 회원 페이지로 진입할때 이 부분을 DTO로 반환하는게 좋을 것이다.     

address의 정보에서 결국 customer는 어디에서 꺼내오든 같은 회원일테니 

```
CustomerDto findAddressListOne(Long customerId, Pageable pageable);
```
하나를 추가하고 

```
@Override
public CustomerDto findAddressListOne(Long customerId, Pageable pageable) {
    JPAQuery<?> addressQuery = query.from(address)
                                    .leftJoin(address.customer, customer)
                                    .fetchJoin()
                                    .where(
                                        customer.condCustomerById(customerId)
                                    )
                                    .orderBy(address.id.desc());
    if(pageable != null) {
        addressQuery.offset(pageable.getOffset())
                    .limit(pageable.getPageSize());
    }

    return addressQuery.transform(groupBy(address.customer.id).as(list(address)))
                       .entrySet()
                       .stream()
                       .map(obj -> new CustomerDto(obj.getValue().get(0).getCustomer(), obj.getValue()))
                       .findFirst().orElse(null);
}
```
다음과 같이 코딩을 했다.     

코드에서 고객의 id로 group by한 이유는 실제로 @OneToMany에서는 해당 엔티티로 묶을 수 있지만 지금같은 @ManyToOne의 기준에서는 customer으로 묶을 수 없다.     

물론 어짜피 고객 아이디로 그룹 바이를 해도 Address객체에서 customer를 꺼내오면 상관없다.      

테스트를 하면 

```
@Test
public void getAddressListByCustomerIdUsingQueryDSL_ONE() {
    Long customerId = 20L;
    int page = 0; // 첫번째 페이지
    int size = 5; // 5개씩 보여주기
    Pageable pageable = PageRequest.of(page, size);
    CustomerDto dto = addressRepository.findAddressListOne(customerId, pageable);
    System.out.println(convertJsonStringFromObject(dto));
}

result:
Hibernate: 
    /* select
        address.customer.id,
        address 
    from
        Address address   
    left join
        fetch address.customer as customer 
    where
        customer.id = ?1 
    order by
        address.id desc */ select
            address0_.customer_id as col_0_0_,
            address0_.id as col_1_0_,
            customer1_.id as id1_1_1_,
            address0_.id as id1_0_0_,
            address0_.created_at as created_2_0_0_,
            address0_.updated_at as updated_3_0_0_,
            address0_.is_default as is_defau4_0_0_,
            address0_.addr_detail as addr_det5_0_0_,
            address0_.addr as addr6_0_0_,
            address0_.addr_name as addr_nam7_0_0_,
            address0_.addr_postcode as addr_pos8_0_0_,
            address0_.customer_id as customer9_0_0_,
            customer1_.created_at as created_2_1_1_,
            customer1_.updated_at as updated_3_1_1_,
            customer1_.customer_email as customer4_1_1_,
            customer1_.customer_mobile as customer5_1_1_,
            customer1_.customer_name as customer6_1_1_ 
        from
            basquiat.basquiat_address address0_ 
        left outer join
            basquiat.basquiat_customer customer1_ 
                on address0_.customer_id=customer1_.id 
        where
            customer1_.id=? 
        order by
            address0_.id desc limit ?
{
  "addresses": [
    {
      "createdAt": {
        "month": "FEBRUARY",
        "dayOfWeek": "THURSDAY",
        "dayOfYear": 42,
        "nano": 0,
        "year": 2021,
        "monthValue": 2,
        "dayOfMonth": 11,
        "hour": 13,
        "minute": 59,
        "second": 38,
        "chronology": {
          "id": "ISO",
          "calendarType": "iso8601"
        }
      },
      "updatedAt": null,
      "address_id": 8,
      "addr_name": "새로운 주소_7",
      "addr_postcode": "새로운우편번호_7",
      "addr_info": "서울",
      "addr_detail": "우리집_7",
      "default": "N"
    },
    {
      "createdAt": {
        "month": "FEBRUARY",
        "dayOfWeek": "THURSDAY",
        "dayOfYear": 42,
        "nano": 0,
        "year": 2021,
        "monthValue": 2,
        "dayOfMonth": 11,
        "hour": 13,
        "minute": 59,
        "second": 38,
        "chronology": {
          "id": "ISO",
          "calendarType": "iso8601"
        }
      },
      "updatedAt": null,
      "address_id": 7,
      "addr_name": "새로운 주소_6",
      "addr_postcode": "새로운우편번호_6",
      "addr_info": "서울",
      "addr_detail": "우리집_6",
      "default": "N"
    },
    {
      "createdAt": {
        "month": "FEBRUARY",
        "dayOfWeek": "THURSDAY",
        "dayOfYear": 42,
        "nano": 0,
        "year": 2021,
        "monthValue": 2,
        "dayOfMonth": 11,
        "hour": 13,
        "minute": 59,
        "second": 38,
        "chronology": {
          "id": "ISO",
          "calendarType": "iso8601"
        }
      },
      "updatedAt": null,
      "address_id": 6,
      "addr_name": "새로운 주소_5",
      "addr_postcode": "새로운우편번호_5",
      "addr_info": "서울",
      "addr_detail": "우리집_5",
      "default": "N"
    },
    {
      "createdAt": {
        "month": "FEBRUARY",
        "dayOfWeek": "THURSDAY",
        "dayOfYear": 42,
        "nano": 0,
        "year": 2021,
        "monthValue": 2,
        "dayOfMonth": 11,
        "hour": 13,
        "minute": 59,
        "second": 38,
        "chronology": {
          "id": "ISO",
          "calendarType": "iso8601"
        }
      },
      "updatedAt": null,
      "address_id": 5,
      "addr_name": "새로운 주소_4",
      "addr_postcode": "새로운우편번호_4",
      "addr_info": "서울",
      "addr_detail": "우리집_4",
      "default": "N"
    },
    {
      "createdAt": {
        "month": "FEBRUARY",
        "dayOfWeek": "THURSDAY",
        "dayOfYear": 42,
        "nano": 0,
        "year": 2021,
        "monthValue": 2,
        "dayOfMonth": 11,
        "hour": 13,
        "minute": 59,
        "second": 38,
        "chronology": {
          "id": "ISO",
          "calendarType": "iso8601"
        }
      },
      "updatedAt": null,
      "address_id": 4,
      "addr_name": "새로운 주소_3",
      "addr_postcode": "새로운우편번호_3",
      "addr_info": "서울",
      "addr_detail": "우리집_3",
      "default": "N"
    }
  ],
  "createdAt": {
    "month": "FEBRUARY",
    "dayOfWeek": "MONDAY",
    "dayOfYear": 39,
    "nano": 0,
    "year": 2021,
    "monthValue": 2,
    "dayOfMonth": 8,
    "hour": 11,
    "minute": 18,
    "second": 44,
    "chronology": {
      "id": "ISO",
      "calendarType": "iso8601"
    }
  },
  "updatedAt": null,
  "customer_id": 20,
  "email": "basquiat_email",
  "name": null,
  "mobile": "my_mobile"
}
```
원하는대로 나왔다. 하지만 이 코드는 살짝 문제가 있다.     

만일 주소가 없는 customerId가 2인 녀석으로 테스트 해보자. 그러면 바로 null이 떨어진다.       

물론 주소가 없기 때문에 null인건 알겠는데 CustomerDto 자체가 null이라 이렇게 되면 어떤 회원을 조회 했었는지 전혀 알수가 없다.     

이것때문에 고민을 했다가 역시 사수의 도움을 받았다.      

'Address 테이블과 Customer테이블이 조인되는 상황에서 Address정보가 없으면 left join을 해도 조회되는 정보가 전혀 없어.    
이런 경우 null이 떨어지면 그냥 customerId를 받은게 있으니 코드레벨에서 null이후 customerId로 고객 정보를 조회하고 그 주소 리스트를 빈 배열로 세팅해서 보내거나 이게 아쉬우면 좀 그렇긴 하지만 right join을 걸어'

그렇다면 right join을 걸어서 코드를 작성하자.

```
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
```       
right join으로 바꾸고 우측 테이블을 기준으로 조건절을 걸었다.      

그리고 가만히 생각해 보면 이 경우에는 fetchJoin을 사용할 이유가 없다.       

일반적으로 @OneToMany에서 N+1를 회피하며 객체 그래프 탐색을 위해서 사용하는데 이 경우에는 굳이 쓸 필요가 없기 때문이다. 

어째든 이럴 경우에는 right join된 customer 엔티티를 키값으로 group by를 할 수 있다.

CustomeDto도 AddressList에 대해서 다음과 같이

```
public CustomerDto(Customer customer, List<Address> addresses) {
    this.id = customer.getId();
    this.customerEmail = customer.getCustomerEmail();
    this.customerMobile = customer.getCustomerMobile();
    this.createdAt = customer.getCreatedAt();
    this.updatedAt = customer.getUpdatedAt();
    if(!isEmpty(addresses)) {
        this.addresses = addresses.stream().map(addr -> new AddressDto(addr))
                                           .collect(Collectors.toList());
    } else {
        this.addresses = (List<AddressDto>) Collections.EMPTY_LIST;
    }
}
```
생성자에서 null일 경우 빈 배열을 생성하게 처리를 하자.

그리고 테스트를 다시 해보면

```
{
  "addresses": [
    
  ],
  "createdAt": {
    "nano": 0,
    "year": 2021,
    "monthValue": 2,
    "dayOfMonth": 8,
    "hour": 11,
    "minute": 15,
    "second": 31,
    "month": "FEBRUARY",
    "dayOfWeek": "MONDAY",
    "dayOfYear": 39,
    "chronology": {
      "calendarType": "iso8601",
      "id": "ISO"
    }
  },
  "updatedAt": null,
  "customer_id": 2,
  "email": "basquiat_email_2",
  "name": null,
  "mobile": "my_mobile_2"
}
```
원하는 데이터가 나왔다.      

그럼 기존의 잘 나왔던 테스트도 다시 한번 살펴보자.

```
{
  "addresses": [
    {
      "createdAt": {
        "nano": 0,
        "year": 2021,
        "monthValue": 2,
        "dayOfMonth": 11,
        "hour": 13,
        "minute": 59,
        "second": 38,
        "month": "FEBRUARY",
        "dayOfWeek": "THURSDAY",
        "dayOfYear": 42,
        "chronology": {
          "id": "ISO",
          "calendarType": "iso8601"
        }
      },
      "updatedAt": null,
      "address_id": 8,
      "addr_name": "새로운 주소_7",
      "addr_postcode": "새로운우편번호_7",
      "addr_info": "서울",
      "addr_detail": "우리집_7",
      "default": "N"
    },
    {
      "createdAt": {
        "nano": 0,
        "year": 2021,
        "monthValue": 2,
        "dayOfMonth": 11,
        "hour": 13,
        "minute": 59,
        "second": 38,
        "month": "FEBRUARY",
        "dayOfWeek": "THURSDAY",
        "dayOfYear": 42,
        "chronology": {
          "id": "ISO",
          "calendarType": "iso8601"
        }
      },
      "updatedAt": null,
      "address_id": 7,
      "addr_name": "새로운 주소_6",
      "addr_postcode": "새로운우편번호_6",
      "addr_info": "서울",
      "addr_detail": "우리집_6",
      "default": "N"
    },
    {
      "createdAt": {
        "nano": 0,
        "year": 2021,
        "monthValue": 2,
        "dayOfMonth": 11,
        "hour": 13,
        "minute": 59,
        "second": 38,
        "month": "FEBRUARY",
        "dayOfWeek": "THURSDAY",
        "dayOfYear": 42,
        "chronology": {
          "id": "ISO",
          "calendarType": "iso8601"
        }
      },
      "updatedAt": null,
      "address_id": 6,
      "addr_name": "새로운 주소_5",
      "addr_postcode": "새로운우편번호_5",
      "addr_info": "서울",
      "addr_detail": "우리집_5",
      "default": "N"
    },
    {
      "createdAt": {
        "nano": 0,
        "year": 2021,
        "monthValue": 2,
        "dayOfMonth": 11,
        "hour": 13,
        "minute": 59,
        "second": 38,
        "month": "FEBRUARY",
        "dayOfWeek": "THURSDAY",
        "dayOfYear": 42,
        "chronology": {
          "id": "ISO",
          "calendarType": "iso8601"
        }
      },
      "updatedAt": null,
      "address_id": 5,
      "addr_name": "새로운 주소_4",
      "addr_postcode": "새로운우편번호_4",
      "addr_info": "서울",
      "addr_detail": "우리집_4",
      "default": "N"
    },
    {
      "createdAt": {
        "nano": 0,
        "year": 2021,
        "monthValue": 2,
        "dayOfMonth": 11,
        "hour": 13,
        "minute": 59,
        "second": 38,
        "month": "FEBRUARY",
        "dayOfWeek": "THURSDAY",
        "dayOfYear": 42,
        "chronology": {
          "id": "ISO",
          "calendarType": "iso8601"
        }
      },
      "updatedAt": null,
      "address_id": 4,
      "addr_name": "새로운 주소_3",
      "addr_postcode": "새로운우편번호_3",
      "addr_info": "서울",
      "addr_detail": "우리집_3",
      "default": "N"
    }
  ],
  "createdAt": {
    "nano": 0,
    "year": 2021,
    "monthValue": 2,
    "dayOfMonth": 8,
    "hour": 11,
    "minute": 18,
    "second": 44,
    "month": "FEBRUARY",
    "dayOfWeek": "MONDAY",
    "dayOfYear": 39,
    "chronology": {
      "id": "ISO",
      "calendarType": "iso8601"
    }
  },
  "updatedAt": null,
  "customer_id": 20,
  "email": "basquiat_email",
  "name": null,
  "mobile": "my_mobile"
}
```
이 경우도 원하는 정보대로 나온다.      

# At A Glance

대부분 엔티티간의 릴레이션에 따라서 어떻게 사용해야 할지에 대해서 아주 작지만 가장 많이 만나게 되는 케이스를 위주로 코드를 작성해 봤다.     

뭔가 두서가 없었던건 사실이고 그냥 의식의 흐름대로 진행하다보니 길어지기도 했는데 다음에는 이를 토대로 서비스, 컨트롤로와 화면 구성을 통해서 한 챕터를 마무리해볼까 한다.     


