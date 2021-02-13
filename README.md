# Spring Boot with queryDSL

이전에 queryDSL을 배웠었다.

[jpa-with-querydsl](https://github.com/basquiat78/jpa-with-querydsl)

하지만 실제로 대부분 자바/코틀린을 주 메인 언어로 어플리케이션을 만든다면 스프링 프레임워크와 함께 헬텐데 이 레파지토리는 바로 스프링 부트와 queryDSL을 연계해서와 연계한다.

## Prerequisites

OS: macOs Big Sur 버전 11.2 (20D64)   
Java: openjdk64-11.0.9.1    
IDE: IntelliJ 2020.2.4 (Community Edition)    
Framework: Spring Boot 2.4.2    
RDBMS: mySql 8.0.21    
build: gradle 6.7.1     
etc: lombok

## Get Start Step 1

기존에 잘 돌아가는 세팅된 프로젝트가 있다면 그것을 카피해서 시작할 수 있다.

하지만 만일 처음부터 시작한다면 스프링을 사용하는 분들에게는 가장 유용한 사이트인 다음 사이트에서 기본적인 틀을 생성해서 사용하자.

[Spring Initializr](https://start.spring.io/)

처음 들어가면 초기 세팅을 위한 선택지가 나온다. 여기서는 Gradle Project와 Java를 선택했다.    

그리고 Dependencies에서는 정말 가장 기본적인 것들만 우선 선택했다.

이 프로젝트 초기 설정은 다음과 같다.

2021년 2월 5일 기준

1. Project: Gradle Project
2. Language: Java
3. Spring Boot: 2.4.2
4. Project Metadata: 자신이 원하는 것을 설정하면 된다.
5. Packaging: Jar
6. Java Version: 11

Dependencies
1. Spring Boot DevTools
2. Lombok
3. Spring Configuration Processor
4. Spring Web
5. Spring Data JPA
6. Spring Data JDBC
7. Spring Boot Actuator

나머지는 필요하면 하나씩 살을 붙여나가는 것이 목표이다.

이렇게 선택하면 사이트 하단부의 [GENERATE (command + enter)]를 누르면 zip파일이 떨어진다.   
압축을 해제하고 인텔리제이에서 File > New > Project From Exising Source...를 선택해서 해당 폴더를 가져와서 시작을 하자.    

### 세팅이 잘된거야?

자 그럼 이제 기본적인 폴더 구조와 필요한 라이브러리를 끌어왔을텐데 해당 프로젝트 세팅을 확인해 보자.     

그냥 해당 프로젝트를 그레이들에서 bootRun을 시켜보자.

제대로 진행을 했다면 서버 콘솔 로그가 뜨게 된다.

```
.
.
.
2021-02-05 10:47:11.327  INFO 10885 --- [  restartedMain] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2021-02-05 10:47:11.327  INFO 10885 --- [  restartedMain] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.41]
2021-02-05 10:47:11.367  INFO 10885 --- [  restartedMain] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2021-02-05 10:47:11.367  INFO 10885 --- [  restartedMain] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 689 ms
2021-02-05 10:47:11.540  INFO 10885 --- [  restartedMain] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService 'applicationTaskExecutor'
2021-02-05 10:47:11.678  WARN 10885 --- [  restartedMain] o.s.b.d.a.OptionalLiveReloadServer       : Unable to start LiveReload server
2021-02-05 10:47:11.681  INFO 10885 --- [  restartedMain] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 2 endpoint(s) beneath base path '/actuator'
.
.

```

중간에 보면 Exposing 2 endpoint(s) beneath base path '/actuator' <- 요런 문구를 볼 수 있는데 Spring Initializr에서 초기 설정시 디펜던시로 actuator를 설정했을텐데 이것은 해당 어플리케이션의 헬쓰 체크 및 다양한 기능을 제공한다.    

자 그럼 제대로 진행을 했다면 아무 설정도 한게 없으니 다음 http://localhost:8080/actuator 로 접속을 해보면

```
{
  _links: {
    self: {
      href: "http://localhost:8080/actuator",
      templated: false
    },
    health: {
      href: "http://localhost:8080/actuator/health",
      templated: false
    },
    health-path: {
      href: "http://localhost:8080/actuator/health/{*path}",
      templated: true
    },
    info: {
      href: "http://localhost:8080/actuator/info",
      templated: false
    }
  }
}
```
http://localhost:8080/actuator/health 링크를 따라가면 json형식의 응답을 볼 수 있다. 키 값이 status인 "UP"을 확인 할 수 있다. ~~싸라있네!~~

나중에 AWS상에서 어플리케이션의 생존 여부를 위해 저 경로로 세팅하면 된다.

여기까지 잘 왔다면 이제 본격 Spring Boot와 queryDSL를 시작할 수 있는 초석은 완성다.

## Get Start Step 2
이제부터 필요한 것들을 build.gradle에 설정해야하는데 방법은 maven repository에서 검색해서 나오는 스크립트를 복사하거나 [Spring Initializr](https://start.spring.io/)에서 처음부터 전부 설정해서 가져와도 좋다.

여기까지 [Spring Initializr]를 통해서 세팅한 정보는 다음과 같다.     
```
plugins {
	id 'org.springframework.boot' version '2.4.2'
	id 'io.spring.dependency-management' version '1.0.11.RELEASE'
	id 'java'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '11'

configurations {
	compileOnly {
		extendsFrom annotationProcessor
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-actuator'
	implementation 'org.springframework.boot:spring-boot-starter-data-jdbc'
	implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
	implementation 'org.springframework.boot:spring-boot-starter-web'

	compileOnly 'org.projectlombok:lombok'

	developmentOnly 'org.springframework.boot:spring-boot-devtools'

	runtimeOnly 'mysql:mysql-connector-java'

	annotationProcessor 'org.springframework.boot:spring-boot-configuration-processor'
	annotationProcessor 'org.projectlombok:lombok'

	testImplementation('org.springframework.boot:spring-boot-starter-test') {
		exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'
	}
}

test {
	useJUnitPlatform()
}


```

기본적으로 mySQL이 설치되었다는 가정하에 시작하는 프로젝트이기 때문에 mySQL이 설치되지 않았다면 설치를 해야한다.
 
만일 회사에서 aws에서 테스트 용도로 사용하는 rdbms가 있다면 그것을 사용해도 좋다. 

사실 mySql이 아니더라도 상관없다. H2, HSQL같은 in-memory DB도 좋고 oracle, postGre가 깔려 있다면 그것도 상관없다.
 
하지만 여기서는 mySQL을 사용한다. ~~대부분은 mySQL나 mariaDB를 사용하지 않을까?~~   

이제부터는 데이터베이스의 스키마, 데이터베이스 생성은 여러분들이 만들고 싶은 어플리케이션의 내용에 따라 달라질 것이다.     

여기서부터는 이 정보를 토대로 여러분들이 하고 싶은 것들을 적용하면 된다.    

여기서는 나의 관심사인 악기와 관련된 도메인을 주로 할 것이다.     

## Get Start Step 3: 시나리오 설정

실제로는 기획이 나오면 그 기획에 맞춰 큰 그림을 그리듯이 개발을 하는 경우가 많다.     

하지만 여기서는 한단계 한단계 만들어 나가며 떠오르는 아이디어들을 하나씩 추가해 나가며 그때 그때 적용을 하는것이 목표이다.    

여러분들도 이것을 기준으로 자신의 아이디어가 떠오른다면 그것들을 어떻게 하면 우아하게 적용할 수 있는지 집중을 하는 것이 좋다.     

### 시나리오

여러분들도 관심대상에 대한 시나리오를 작성해 보는 것도 좋다. 이유는 그 속에서 요구사항을 디테일하게 잡을 수 있고 이것을 토대로 잘 만듨 수 있다.    

이렇게 하다보면 확장 포인트도 잡을 수 있다. 또한 설계시에 한번 더 고래해 볼 이슈들을 체크하는데 도움이 된다.     

물론 이것은 혼자 만드는 어플리케이션이지만 우리는 회사에서 일을 하거나 할 것이다.     

시나리오를 작성할 DDD에서 말하는 Bounded Context와 관련해서 많은 것을 고민해 불 수 있는 시간이다. 물론 개똥철학이다.     

루띠어: 악기 마스터 빌더
커스터머: 악기 주문자

루띠어는 자신의 악기를 만드는 사람이다. 그리고 자신만의 브랜드를 가지고 있으며 그 브랜드는 루띠어가 소리에 대한 자신만의 찰학을 담은 디자인 중심으로 모델군을 가지고 있다.

커스터머는 여러 브랜드중에서 가격과 그 브랜드만의 디자인이나 사운드의 특징에 대한 취향이 존재한다. 원하는 브랜드/루띠어의 악기를 주문한다.

1. 고객 (주문자 또는 견적 요청자(잠재적 주문자))이 존재한다.
2. 루띠어가 존재한다.
   - 브랜드와 루띠어는 연결되어 있다.

3. 커스터머는 브랜드/루띠어를 선택한다.
4. 루띠어마다 모델에 따른 가격 정책과 추가 스펙에 대한 옵션 가격등을 가지고 있다.

5. 공통의 오더 스펙를 정의하는 오더 양식이 있다. 
6. 루띠어에게 오더 양식을 작성해서 견적 요청을 한다.

7. 루띠어는 해당 견적 요청서를 보고 총 견적비용을 산정한다.
8. 견적 요청자에게 답을 준다.

## Get Start Step 4: 고객 모델

그 전에 도메인에 대해 고민하자.

1. 고객

일단 주문자 (consumer or orderer), 또는 견적 요청자 - 잠재적인 주문 - (price quote requester)인 경우에는 관점에 따라 달라지지만 기본적으로 고객이라는 도메인이라고 할 수 있다.     

확실한 것은 에릭 에반스의 책이나 마틴 파울러의 글을 읽어보면 이와 관련된 내용중 하나가 이런 것이 있다.

 - 같은 대상이라도 행위가 달라서 그 대상을 표현하는 용어가 다를 수 있다. eg. 주문자, 견적 요청자

내가 에릭 에반스의 책이나 마틴 파울러의 글들을 통해서 얼마나 깊게 이해하고 있는지 나는 잘 모른다.     

하지만 이것을 보면 고객이라는 하나의 Bounded Context에 총 주문자, 견적 요청자(잠재적 고객) 총 2개의 도메인이 존재한다.

어찌되었든 모두 고객이다.    

나는 이것을 그냥 Customer라고 표현하겠다.

erd는 최대한 단순하게 가고자 한다. 

쇼핑몰의 경우에는 한명의 사용자가 여러개의 배송지를 작성할 수 있게 되어 있다.     

물론 지금 만드는 어플리케이션는 사용자 (주문자 또는 견적 요청자)와 루띠어를 연결해 주는 것이 주 목적이다.    

![실행이미지](https://github.com/basquiat78/spring-boot-querydsl/blob/master/capture/customer.png)

참고로 디비 ERD는 [ERDCloud](https://www.erdcloud.com/)를 통해서 작성한 것이다. ~~설정 구멍이 좀 있는건 함정~~

주소는 있을수도 없을수도 있고 하나일수도 여러개일 수도 있다. 왜냐하면 처음 견적 요청자의 입장에서 주소를 입력할 이유가 있을까? 이메일 또는 전화번호면 충분하다.    

주소가 필요한 경우는 견적대로 또는 추가 견적이나 견적 변경이후 실제로 오더를 넣고 예약금 (deposit)을 걸어 뒀을 때 일것이다.     

아니면 루띠어가 악기 완성이후 배송을 위해서 주소 요청을 하거나 둘중 하나이다.     

하지만 여기서는 오더를 넣을 때 주소를 입력하게 할 것이며 필요하면 최대 8개까지만 등록하게 제한을 둘 것이다.     

이유는 최초 주소 등록후에 혹시 와이프님 - 일명 내무부 장관님 -의 등짝 스매싱이 무서워서 회사로 배송지를 변경하고 싶을 수도 있다.     

기존의 주소를 변경하기보다는 새로운 주소를 등록하고 그 새로운 주소를 대표 주소로 설정하게 만들 것이다.     

어찌되었든 루띠어는 최종 악기 마무리이후 배송을 위해서 주소 체크시 대표 주소를 보게 될것이니깐~           

따라서 Entity Relationship은 'Zero or Many'로 설정.    

아 이제 이것을 기준으로 schema를 생성하자.

```
// basquiat라는 데이터베이스를 생성한다.
CREATE DATABASE basquiat default CHARACTER SET UTF8;

CREATE TABLE `basquiat`.`basquiat_customer` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '고객 유니크 아이디',
  `customer_email` varchar(100) DEFAULT NULL COMMENT '고객 이메일',
  `customer_name` varchar(100) DEFAULT NULL COMMENT '고객 명',
  `customer_mobile` varchar(50) DEFAULT NULL COMMENT '고객 폰 넘버',
  `address_id` bigint(20) unsigned NOT NULL COMMENT '주소 아이디',
  `created_at` datetime DEFAULT NULL COMMENT '가입일',
  `updated_at` datetime DEFAULT NULL COMMENT '수정일',
  PRIMARY KEY (`id`),
  KEY `idx_email` (`customer_email`),
  KEY `idx_address_id` (`address_id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


CREATE TABLE `basquiat`.`basquiat_address` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '고객 주소 유니크 아이디',
  `customer_id` bigint(20) unsigned NOT NULL COMMENT '고객 유니크 아이디',
  `addr_name` varchar(20) DEFAULT NULL COMMENT '설정 주소명',
  `addr_postcode` varchar(10) DEFAULT NULL COMMENT '우편번호',
  `addr` varchar(200) DEFAULT NULL COMMENT '메인 주소',
  `addr_detail` varchar(200) DEFAULT NULL COMMENT '상세 주소',
  `is_default` char(1) DEFAULT NULL COMMENT '대표 주소 설정 Y|N',
  `created_at` datetime DEFAULT NULL COMMENT '생성일',
  `updated_at` datetime DEFAULT NULL COMMENT '수정일',
  PRIMARY KEY (`id`),
  KEY `idx_customer_id` (`customer_id`),
  CONSTRAINT `fk_basquiat_customer` FOREIGN KEY (`customer_id`) REFERENCES `basquiat_customer` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

```

일단 나의 application.yml의 기본 설정은 다음과 같다.

```
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/basquiat?useSSL=false&useUnicode=yes&characterEncoding=UTF-8&allowMultiQueries=true&serverTimezone=Asia/Seoul
    username: root
    password: '@1234qwer'
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      connection-test-query: 'select 1'
      connection-timeout: 10000
      maximum-pool-size: 10
      pool-name: basquiat-pool
  jpa:
    database: mysql
    hibernate:
      ddl-auto: none
      naming:
        physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
    properties:
      hibernate:
        format_sql: true
        generate_statistics: true
        use_sql_comments: true
    show-sql: true
    database-platform: org.hibernate.dialect.MySQL57InnoDBDialect
    allow-bean-definition-overriding: true

# loggin level setting
logging:
  level:
    org:
      hibernate:
        type:
          descriptor:
            sql: trace
```

정확한 콘솔 로그를 보기 위해 format_sql, show-sql, generate_statistics, use_sql_comments 옵션을 준다.     
로그에서 쿼리시 매핑되는 파라미터 정보 역시 체크하기 위해 loggin level에서 'trace'옵션도 줬다.

물론...운영에서 저 옵션은 왠만하면 빼는게 좋다. show-sql정도만 남기고....     

어느 프로젝트에서는 logging level -> trace정도는 줬던 기억이 나는데 이게 생각보다 로그양이 많아져서이다.

딱 필요한 옵션만 운영에서는 살려두자.

# JpaSpecificationExecutor vs queryDSL Search Condition

기본적인 방식으로 어떤 테이블에서 조건을 통한 검색을 하기 위해서는 JpaSpecificationExecutor 인터페이스를 활용하는 방식이다.

모든 Repository에서 공통적으로 상속받아 사용하기 위해서 common이라는 패키지를 만들었다.     

이 패키지에서는 공통적으로 사용할 수 있는 DateTimeCommon이라는 객체를 하나 만들었고 BaseRepository를 하나 만들었다.

DateTimeCommon.java
```
package io.basquiat.common.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

/**
 * 공통적으로 생성일, 수정일을 담당하기 위한 공통 MappedSuperClass
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class DateTimeCommon {

    /** db row 생성일 */
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /** db row 수정일 */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

}

```

@MappedSuperclass사용법은 깃헙이나 구글신을 통해서 충분히 알 수 있고 JPA Auditing을 활용하고 있다.

그 중에 updatedAd은 @LastModifedDate를 사용할 수 있지만 이것은 새로운 로우 생성시에 updatedAt컬럼에도 값이 들어가기 때문이다.     
 
나는 이 값이 최초 생성시에는 null로 세팅하고 싶기 때문에 이 부분만 @PreUpdate로 설정했다.    

'나는 그냥 상관없는데??' 라고 하며 코드를 심플하게 가져가고 싶다면

```
package io.basquiat.common.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

/**
 * 공통적으로 생성일, 수정일을 담당하기 위한 공통 MappedSuperClass
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class DateTimeCommon {

    /** db row 생성일 */
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /** db row 수정일 */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
```

요렇게 하면 된다.    

BaseRepository.java
```
package io.basquiat.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * pageable 처리를 위해 JpaRepository와 JpaSpecificationExecutor를 상속받은 공통 리파지토리를 만든다.
 * @param <M>
 * @param <I>
 */
@NoRepositoryBean
public interface BaseRepository<M, I extends Serializable> extends JpaRepository<M, I>, JpaSpecificationExecutor<M> {
}

```

조건 검색을 위한 criteriaBuilder를 사용하기 위해서는 일반적으로 다음과 같은 방식으로 Repository를 구성하게 된다.

```
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    // do something
}
```
일반적인 방식이긴 한데 저렇게 객체로 받을 수 있게 껍데기를 만들고 필요에 따라서 Repository에서 상속받아 사용할 수 있게 공통으로 빼놓는다.

## 회원 관련 엔티티와 Repository를 생성하자.

Customer.java
```
package io.basquiat.customer.model.entity;

import io.basquiat.common.model.DateTimeCommon;
import lombok.*;

import javax.persistence.*;
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

    /** 고객 주소 리스트 */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
    private List<Address> addresses = new ArrayList<>();

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

import io.basquiat.common.model.DateTimeCommon;
import lombok.*;

import javax.persistence.*;

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
    public Address(String addressName, String addressPostcode, String addressInfo, String addressDetail,
                   AddressDefaultStatus addressDefaultStatus, Customer customer) {
        this.addressName = addressName;
        this.addressPostcode = addressPostcode;
        this.addressInfo = addressInfo;
        this.addressDetail = addressDetail;
        this.addressDefaultStatus = addressDefaultStatus;
        this.customer = customer;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

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

    /** 고객 */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    /**
     * 대표 주소 여부 enum
     */
    public enum AddressDefaultStatus {
        Y,
        N;
    }

}

```

대표주소 여부와 관련 enum클래스는 따로 빼놓을 수 있지만 해당 객체에 종속시 명확함을 주기 위해 일부로 위와 같이 작성을 했는데 이것은 취향이다.

고객 정보와 주소 정보가 같이 넘어왔을 때 주소 정보를 생성시에 영속성 전이를 통해서 고객의 정보도 같이 생성할 수 있도록 cascade옵션을 줬다.    

Customer의 경우에는 이메일과 핸드폰 번호가 변경될 수 있기 때문에 변경할 수 있는 메소드를 만들었으며 주소의 경우에는 새로 생성할 수 있기 때문에 변경보다는 새로운 주소를 생성하도록 유도한다.    

사실 빌드 패턴과 관련해서 저렇게 엔티티를 구성할때는 빌드패턴의 생성자내부에 검증 코드를 넣는 것이 좋다.     

예를 들면 

```
@Builder
public Address(String addressName, String addressPostcode, String addressInfo, String addressDetail,
               AddressDefaultStatus addressDefaultStatus, Customer customer) {
    Assert.hasText(addressName, "Address Name is Mandatory");
    Assert.hasText(addressPostcode, "Address Postcode is Mandatory");
    Assert.hasText(address, "Address is Mandatory");
    this.addressName = addressName;
    this.addressPostcode = addressPostcode;
    this.addressInfo = addressInfo;
    this.addressDetail = addressDetail;
    this.addressDefaultStatus = addressDefaultStatus;
    this.customer = customer;
}

```
처럼 필수로 들어와야 하는 변수에 대해서는 저렇게 한번 검증해 주는것이 좋은데... 여기서는 그냥 패스하겠다. ~~저렇게 이미 작성하고 안하는건 뭔지...~~

Repository는 다음과 같다.

AddressRepository.java
```
package io.basquiat.customer.repository;

import io.basquiat.common.repository.BaseRepository;
import io.basquiat.customer.model.entity.Address;

/**
 * address repository
 * created by basquiat
 */
public interface AddressRepository extends BaseRepository<Address, Long> {

}
```

CustomerRepository.java
```
package io.basquiat.customer.repository;

import io.basquiat.common.repository.BaseRepository;
import io.basquiat.customer.model.entity.Customer;

/**
 * customer repository
 * created by basquiat
 */
public interface CustomerRepository extends BaseRepository<Customer, Long> {

}
```

정말 심플하다.

그럼 테스트부터 해보자.

```
package io.basquiat.customer;

import io.basquiat.customer.model.entity.Address;
import io.basquiat.customer.model.entity.Customer;
import io.basquiat.customer.repository.CustomerRepository;
import io.basquiat.customer.repository.spec.CustomerSpec;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void insertCustomer() {
        IntStream.range(1, 20)
                 .forEach(i -> {
                     Customer customer = Customer.builder()
                                                 .customerEmail("basquiat_email_" + i)
                                                 .customerName("basquiat_name_" + i)
                                                 .customerMobile("my_mobile_" + i)
                                                 .build();
                     customerRepository.save(customer);
                 });
    }

    @Test
    public void selectCustomer() {
        Optional<Customer> optional = customerRepository.findById(1L);
        if(optional.isPresent()) {
            Customer customer = optional.get();
            assertThat("basquiat_name_1").isEqualTo(customer.getCustomerName());
        }
    }

    @Test
    public void updateCustomer() {
        Optional<Customer> optional = customerRepository.findById(1L);
        if(optional.isPresent()) {
            Customer customer = optional.get();
            customer.changeCustomerMobile("000-000-0002");
            customerRepository.save(customer);
        }
    }

    @Test
    public void selectCustomerAfterUpdate() {
        Optional<Customer> optional = customerRepository.findById(1L);
        if(optional.isPresent()) {
            Customer customer = optional.get();
            assertThat("000-000-0002").isEqualTo(customer.getCustomerMobile());
        }
    }

```
@Transactional를 붙이 테스트 완료이후 롤백이 된다. 이 데이터를 토대로 앞으로 무언가를 해볼 생각이기 때문에 어노테이션을 달지 않았다.    

아마도 정상적으로 잘 따라오거나 이미 잘 세팅된 프로젝트를 가지고 테스트 했다면 저 위의 테스트는 성공할 것이다.


```
Hibernate: 
    select
        customer0_.id as id1_1_0_,
        customer0_.created_at as created_2_1_0_,
        customer0_.updated_at as updated_3_1_0_,
        customer0_.customer_email as customer4_1_0_,
        customer0_.customer_mobile as customer5_1_0_,
        customer0_.customer_name as customer6_1_0_ 
    from
        basquiat.basquiat_customer customer0_ 
    where
        customer0_.id=?
2021-02-07 19:39:30.236 TRACE 14667 --- [    Test worker] o.h.type.descriptor.sql.BasicBinder      : binding parameter [1] as [BIGINT] - [1]
2021-02-07 19:39:30.246 TRACE 14667 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([created_2_1_0_] : [TIMESTAMP]) - [2021-02-07T19:08:13]
2021-02-07 19:39:30.246 TRACE 14667 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([updated_3_1_0_] : [TIMESTAMP]) - [2021-02-07T19:09:07]
2021-02-07 19:39:30.246 TRACE 14667 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([customer4_1_0_] : [VARCHAR]) - [basquiat_email_1]
2021-02-07 19:39:30.247 TRACE 14667 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([customer5_1_0_] : [VARCHAR]) - [000-000-0002]
2021-02-07 19:39:30.247 TRACE 14667 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([customer6_1_0_] : [VARCHAR]) - [basquiat_name_1]
2021-02-07 19:39:30.254  INFO 14667 --- [    Test worker] i.StatisticalLoggingSessionEventListener : Session Metrics {
    38879 nanoseconds spent acquiring 1 JDBC connections;
    0 nanoseconds spent releasing 0 JDBC connections;
    13780564 nanoseconds spent preparing 1 JDBC statements;
    789625 nanoseconds spent executing 1 JDBC statements;
    0 nanoseconds spent executing 0 JDBC batches;
    0 nanoseconds spent performing 0 L2C puts;
    0 nanoseconds spent performing 0 L2C hits;
    0 nanoseconds spent performing 0 L2C misses;
    0 nanoseconds spent executing 0 flushes (flushing a total of 0 entities and 0 collections);
    0 nanoseconds spent executing 0 partial-flushes (flushing a total of 0 entities and 0 collections)
}
```
마지막 테스트의 콘솔 로그는 다음과 같이 뜰 것이다.

그러면 주소 관련 테스트도 해보자

```
package io.basquiat.customer;

import io.basquiat.customer.model.entity.Address;
import io.basquiat.customer.model.entity.Customer;
import io.basquiat.customer.repository.AddressRepository;
import io.basquiat.customer.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AddressRepositoryTest {

    @Autowired
    private AddressRepository addressRepository;

    @Test
    public void insertAddress() {
        Customer customer = Customer.builder()
                                    .customerEmail("basquiat_email")
                                    .customerName("basquiat_name")
                                    .customerMobile("my_mobile")
                                    .build();

        Address address = Address.builder()
                                 .addressName("대표 주소")
                                 .addressPostcode("postcode")
                                 .addressInfo("나는 서울에서 산다.")
                                 .addressDetail("서울에 있는 내집")
                                 .addressDefaultStatus(Address.AddressDefaultStatus.Y)
                                 .customer(customer)
                                 .build();
        addressRepository.save(address);
    }

}

```
application.yml에 설정한 옵션을 통해서 실제로 인서트되는 로그를 볼 수가 있다.

위 Address테스트를 통해 새로운 고객과 주소 정보를 인서트했으니 한번 코드로 검증해 보자.
```
@Test
@Transactional
public void selectCustomerWithAddress() {
    Optional<Customer> optional = customerRepository.findById(20L);
    if(optional.isPresent()) {
        Customer customer = optional.get();
        assertThat("basquiat_email").isEqualTo(customer.getCustomerEmail());
        assertThat("basquiat_name").isEqualTo(customer.getCustomerName());
        List<Address> addressList = customer.getAddresses();
        assertThat("대표 주소").isEqualTo(addressList.get(0).getAddressName());
    }
}
```
Lazy를 활용하기 때문에 해당 테스트에는 @Transactional을 달아놨다. 없으면 org.hibernate.LazyInitializationException 에러를 마주할 것이다.

### Search using Specification

Spring Data에서 제공하는 것중 하나가 Specification이다. 이것은 DB에서 검색할 조건을 특정 스펙으로 코드 레벨에서 처리할 수 있도록 도와준다.    

물론 이런 생각도 할 것이다.

'JPQL을 쓰면 되는거 아닌가?'      

하지만 동적인 쿼리 생성을 하기에는 JPQL은 좀 한계가 있거나 굉장히 번거로울 것이다.     

JpaSpecificationExecutor 클래스를 따라가서 Specification 클래스까지 가면 내부적으로 구현된 메소드를 통해서 동적인 쿼리를 생성하는 것을 볼 수 있다.     

예를 들면 회원 검색을 할때 이름, 이메일로 검색할 수 있다. 핸드펀 번호도 가능하겠지만 이건 그냥 뒤로 하고 이 2개로만 제한을 해보자.    

그럼 이제 한번 코드를 살펴보자.    

Specification 클래스를 잘 살펴보면 
```
/**
	 * Creates a WHERE clause for a query of the referenced entity in form of a {@link Predicate} for the given
	 * {@link Root} and {@link CriteriaQuery}.
	 *
	 * @param root must not be {@literal null}.
	 * @param query must not be {@literal null}.
	 * @param criteriaBuilder must not be {@literal null}.
	 * @return a {@link Predicate}, may be {@literal null}.
	 */
	@Nullable
	Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder);
```
이런 녀석이 있다. Predicate을 이용해서 한번 이런 것을 만들어보자.

CustomerSpec.java
```
package io.basquiat.customer.repository.spec;

import io.basquiat.customer.model.entity.Customer;
import org.springframework.data.jpa.domain.Specification;

/**
 * Condition Spec using Specification
 * createc by baquiat
 *
 */
public class CustomerSpec {

    /**
     * 이메일로 검색하기
     * @param customerEmail
     * @return Specification<Customer>
     */
    public static Specification<Customer> condByEmail(String customerEmail) {
        return (Specification<Customer>) ((root, query, builder) ->
                builder.equal(root.get("customerEmail"), customerEmail)
        );
    }
}

```

테스트에서 

```
@Test
public void selectCustomerByEmail() {
    Optional<Customer> optional = customerRepository.findOne(CustomerSpec.condByEmail("basquiat_email_4"));
    if(optional.isPresent()) {
        Customer customer = optional.get();
        assertThat("basquiat_email_4").isEqualTo(customer.getCustomerEmail());
    }
}

result
Hibernate: 
    /* select
        generatedAlias0 
    from
        Customer as generatedAlias0 
    where
        generatedAlias0.customerEmail=:param0 */ select
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
2021-02-07 20:08:53.970 TRACE 14825 --- [    Test worker] o.h.type.descriptor.sql.BasicBinder      : binding parameter [1] as [VARCHAR] - [basquiat_email_4]
2021-02-07 20:08:53.973 TRACE 14825 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([id1_1_] : [BIGINT]) - [4]
2021-02-07 20:08:53.978 TRACE 14825 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([created_2_1_] : [TIMESTAMP]) - [2021-02-07T19:08:13]
2021-02-07 20:08:53.978 TRACE 14825 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([updated_3_1_] : [TIMESTAMP]) - [null]
2021-02-07 20:08:53.978 TRACE 14825 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([customer4_1_] : [VARCHAR]) - [basquiat_email_4]
2021-02-07 20:08:53.979 TRACE 14825 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([customer5_1_] : [VARCHAR]) - [my_mobile_4]
2021-02-07 20:08:53.979 TRACE 14825 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([customer6_1_] : [VARCHAR]) - [basquiat_name_4]
2021-02-07 20:08:53.985  INFO 14825 --- [    Test worker] i.StatisticalLoggingSessionEventListener : Session Metrics {
    35450 nanoseconds spent acquiring 1 JDBC connections;
    0 nanoseconds spent releasing 0 JDBC connections;
    11095679 nanoseconds spent preparing 1 JDBC statements;
    742972 nanoseconds spent executing 1 JDBC statements;
    0 nanoseconds spent executing 0 JDBC batches;
    0 nanoseconds spent performing 0 L2C puts;
    0 nanoseconds spent performing 0 L2C hits;
    0 nanoseconds spent performing 0 L2C misses;
    0 nanoseconds spent executing 0 flushes (flushing a total of 0 entities and 0 collections);
    14423 nanoseconds spent executing 1 partial-flushes (flushing a total of 0 entities and 0 collections)
}
```
그렇다면 고객 이름으로 검색하는 것도 딱 이런 생각을 할 것이다.

```
package io.basquiat.customer.repository.spec;

import io.basquiat.customer.model.entity.Customer;
import org.springframework.data.jpa.domain.Specification;

/**
 * Condition Spec using Specification
 * createc by baquiat
 *
 */
public class CustomerSpec {

    /**
     * 이메일로 검색하기
     * @param customerEmail
     * @return Specification<Customer>
     */
    public static Specification<Customer> condByEmail(String customerEmail) {
        return (Specification<Customer>) ((root, query, builder) ->
                builder.equal(root.get("customerEmail"), customerEmail)
        );
    }

    /**
     * 이름으로 검색하기
     * @param customerName
     * @return Specification<Customer>
     */
    public static Specification<Customer> condByName(String customerName) {
        return (Specification<Customer>) ((root, query, builder) ->
                builder.equal(root.get("customerName"), customerName)
        );
    }
}

```
그런데 문제가 있다. 아마도 검색할 키 값에 따라 분기를 타야 한다는 것이다.

코드를 예로 들면

```
public Customer findCustomer(String key, String value) {
    if("customerEmail".equals(key) && value != null) {
        Optional<Customer> optional = customerRepository.findOne(CustomerSpec.condByEmail(value));
        if(optional.isPresent()) {
            return optional.get();
        }
    } else if("customerName".equals(key) && value != null) {
        Optional<Customer> optional = customerRepository.findOne(CustomerSpec.condByName(value));
        .
        .
    }
}
```

물론 2개의 조건을 한번에 걸 수 있을것이다.     

Specification은 컴포지트 패턴을 활용한 녀석이기 때문에 검색 조건을 조합해서 다음과 같이 조합해서 사용이 가능하다.     

```
Specification<Customer> spec = Specification.where(CustomerSpec.condByEmail(email));
spec = spec.and(CustomerSpec.condByName(name)); // and는 앞에 선언된 조건에 AND 조건으로 조합한다는 의미이고 or은 OR 조건으로 조합한다.
Optional<Customer> optional = customerRepository.findOne(spec);

```

그럼 테스트 해보면?
```
@Test
public void selectCustomerByEmailAndName() {
    String email = "basquiat_email_2";
    String name = "basquiat_name_2";
    Specification<Customer> spec = Specification.where(CustomerSpec.condByEmail(email));
    spec = spec.and(CustomerSpec.condByName(name));
    Optional<Customer> optional = customerRepository.findOne(spec);
    if(optional.isPresent()) {
        Customer customer = optional.get();
        assertThat("basquiat_email_2").isEqualTo(customer.getCustomerEmail());
        assertThat("basquiat_name_2").isEqualTo(customer.getCustomerName());
    }
}
result:
Hibernate: 
    /* select
        generatedAlias0 
    from
        Customer as generatedAlias0 
    where
        (
            generatedAlias0.customerName=:param0 
        ) 
        and (
            generatedAlias0.customerEmail=:param1 
        ) */ select
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
            and customer0_.customer_email=?
2021-02-07 20:32:00.954 TRACE 14998 --- [    Test worker] o.h.type.descriptor.sql.BasicBinder      : binding parameter [1] as [VARCHAR] - [basquiat_name_2]
2021-02-07 20:32:00.954 TRACE 14998 --- [    Test worker] o.h.type.descriptor.sql.BasicBinder      : binding parameter [2] as [VARCHAR] - [basquiat_email_2]
2021-02-07 20:32:00.957 TRACE 14998 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([id1_1_] : [BIGINT]) - [2]
2021-02-07 20:32:00.962 TRACE 14998 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([created_2_1_] : [TIMESTAMP]) - [2021-02-07T19:08:13]
2021-02-07 20:32:00.962 TRACE 14998 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([updated_3_1_] : [TIMESTAMP]) - [null]
2021-02-07 20:32:00.962 TRACE 14998 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([customer4_1_] : [VARCHAR]) - [basquiat_email_2]
2021-02-07 20:32:00.962 TRACE 14998 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([customer5_1_] : [VARCHAR]) - [my_mobile_2]
2021-02-07 20:32:00.962 TRACE 14998 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([customer6_1_] : [VARCHAR]) - [basquiat_name_2]
2021-02-07 20:32:00.969  INFO 14998 --- [    Test worker] i.StatisticalLoggingSessionEventListener : Session Metrics {
    35402 nanoseconds spent acquiring 1 JDBC connections;
    0 nanoseconds spent releasing 0 JDBC connections;
    11529564 nanoseconds spent preparing 1 JDBC statements;
    753676 nanoseconds spent executing 1 JDBC statements;
    0 nanoseconds spent executing 0 JDBC batches;
    0 nanoseconds spent performing 0 L2C puts;
    0 nanoseconds spent performing 0 L2C hits;
    0 nanoseconds spent performing 0 L2C misses;
    0 nanoseconds spent executing 0 flushes (flushing a total of 0 entities and 0 collections);
    17246 nanoseconds spent executing 1 partial-flushes (flushing a total of 0 entities and 0 collections)

```

어째든 조건에 따라 늘어날 소지가 아주아주 다분한 이 코드는 enum클래스와 각 스펙이 구현된 Predicate객체를 키에 맞춰서 반환하게 만들어서 없앨 수 있다.      

하지만 이 방식을 보면서 무슨 생각이 들지 난 궁금하다.     

아마도 JPA의 전반적인 스펙, 즉 JPA Criteria를 잘 알고 다룰 수 있는 사람이라면 별거 아닌 코드일 수 있고 저 위의 코드들은 김영한님의 책이나 Baeldong이나 구글을 통해서 얼마든지 얻을 수 있는 코드이다.     

지금같이 단순한 검색조건이라면 상관없지만 무언가 확장을 하거나 좀더 우아하게 코드를 만들기 위해서 JPA Criteria의 특징과 스펙을 잘 알아야 한다.    

어...근데 이거 왜하지? queryDSL하는거 아니였어???

### Search using queryDSL

사실 이 예제의 경우에는 queryDSL을 굳이 이용할 사이즈는 아니다. 하지만 queryDSL이라면 어떻게 처리할까? 한번 알아보는것이 목표이다.     

간단하고 쉽게 할 수 있는 예제부터 살을 붙이는게 최종 목표이니....      

이제는 build.gradle에 다음과 같이 세팅을 추가해 주자.

```
plugins {
	id 'org.springframework.boot' version '2.4.2'
	id 'io.spring.dependency-management' version '1.0.11.RELEASE'
	// queryDSL plugin
	id 'com.ewerk.gradle.plugins.querydsl' version '1.0.10'
	id 'java'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '11'

configurations {
	compileOnly {
		extendsFrom annotationProcessor
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-actuator'
	implementation 'org.springframework.boot:spring-boot-starter-data-jdbc'
	implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
	implementation 'org.springframework.boot:spring-boot-starter-web'
	implementation 'org.projectlombok:lombok:1.18.16'
	implementation 'com.querydsl:querydsl-jpa'
	
	compileOnly 'org.projectlombok:lombok'

	developmentOnly 'org.springframework.boot:spring-boot-devtools'

	runtimeOnly 'mysql:mysql-connector-java'

	annotationProcessor 'org.springframework.boot:spring-boot-configuration-processor'
	annotationProcessor 'org.projectlombok:lombok'

	testImplementation('org.springframework.boot:spring-boot-starter-test') {
		exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'
	}
}

test {
	useJUnitPlatform()
}

//queryDSL Config
def querydslDir = "$buildDir/generated/querydsl"
querydsl {
	jpa = true
	querydslSourcesDir = querydslDir
}

sourceSets {
	main.java.srcDir querydslDir
}

configurations {
	querydsl.extendsFrom compileClasspath
}

compileQuerydsl {
	options.annotationProcessorPath = configurations.querydsl
}
```
변경을 하고 IntelliJ의 우측 그레이들에서 새로 고침을 하자. 그러면 기존 그레이들의 구조에 추가되는 것들을 볼 수 있다.

build > clean을 눌러서 한번 눌러주고 other > compileQuerydsl을 클릭해서 엔티티의 Q클래스를 생성하자.     

콘솔 로그가 쭉 올라오면 좌측 프로젝트에서 build> generated에 엔티티에 해당하는 Q클래스가 생성된 것을 볼 수 있다.

![실행이미지](https://github.com/basquiat78/spring-boot-querydsl/blob/master/capture/capture1.png)

```
EntityManagerFactory emf = Persistence.createEntityManagerFactory("basquiat");
EntityManager em = emf.createEntityManager();
EntityTransaction tx = em.getTransaction();
JPAQueryFactory query = new JPAQueryFactory(em);
```

위 코드는 queryDSL을 사용하기 위한 과정이다. 하지만 매번 저렇게 사용할 수 없기 때문에 빈을 하나 등록하려고 한다.

QueryDSLConfiguration.java
```
package io.basquiat.common.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

/**
 *
 * JPAQueryFactory @Bean으로 등록하기
 * created by basquiat
 *
 */
@Configuration
public class QueryDSLConfiguration {

    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager em) {
        return new JPAQueryFactory(em);
    }

}
```
queryDSL를 사용하기 위해 이제부터 다음과 같은 인터페이스를 하나 만들어보자.

QueryCustomerRepository.java
```
package io.basquiat.customer.repository.custom;

import io.basquiat.customer.model.entity.Customer;

import java.util.List;

/**
 * created by basquiat
 */
public interface QueryCustomerRepository {

    List<Customer> findAllCustomer();

}

```
그리고 queryDSL을 이용한 구현체도 작업한다.

QueryCustomerRepositoryImpl.java
```
package io.basquiat.customer.repository.custom;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.basquiat.customer.model.entity.Customer;
import io.basquiat.customer.model.entity.QCustomer;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.basquiat.customer.model.entity.QCustomer.customer;
import static io.basquiat.customer.model.entity.QAddress.address;

/**
 * created by basquiat
 */
@RequiredArgsConstructor
public class QueryCustomerRepositoryImpl implements QueryCustomerRepository {

    private final JPAQueryFactory query;

    @Override
    public List<Customer> findAllCustomer() {
        JPAQuery<Customer> customerQuery = query.selectFrom(customer)
                                                .leftJoin(customer.addresses, address)
                                                .fetchJoin();
        return customerQuery.fetch();
    }
}

```
@OneToMany의 경우에는 기본적으로 innerJoin을 사용한다.     

inner join의 경우에는 좌측 테이블, 즉 Address테이블에 데이터가 없으면 해당 회원 정보도 조회되지 않기 때문에 의도적으로 leftJoin을 해야한다.     

그 코드는 위와 같다. leftJoin을 활용해서 어떤 테이블인지 명시를 한다.     

이때 fetchJoin을 같이 걸어줘야 하는데 그렇지 않으면 다음과 같은 Lazy Loading관련 에러를 마주하게 된다.

```
failed to lazily initialize a collection of role: io.basquiat.customer.model.entity.Customer.addresses, could not initialize proxy - no Session
org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: io.basquiat.customer.model.entity.Customer.addresses, could not initialize proxy - no Session
	at org.hibernate.collection.internal.AbstractPersistentCollection.throwLazyInitializationException(AbstractPersistentCollection.java:606)
	.
    .
    .
```

이제는 테스트를 통해서 결과를 한번 알아보자. 정보를 찍기 위해서 println으로 확인해보자.

```
@Test
public void selectCustomerByQueryDSL() {
    List<Customer> customers = customerRepository.findAllCustomer();
    System.out.println(customers.toString());
}

result
Hibernate: 
    /* select
        customer 
    from
        Customer customer   
    left join
        fetch customer.addresses as address */ select
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
2021-02-08 15:33:02.824 TRACE 17738 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([id1_1_0_] : [BIGINT]) - [1]
2021-02-08 15:33:02.824 TRACE 17738 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([id1_0_1_] : [BIGINT]) - [null]
.
.
.
.
.
.
2021-02-08 15:33:02.853 TRACE 17738 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([id1_0_0__] : [BIGINT]) - [1]
2021-02-08 15:33:02.864  INFO 17738 --- [    Test worker] i.StatisticalLoggingSessionEventListener : Session Metrics {
    36358 nanoseconds spent acquiring 1 JDBC connections;
    0 nanoseconds spent releasing 0 JDBC connections;
    9861286 nanoseconds spent preparing 1 JDBC statements;
    1504429 nanoseconds spent executing 1 JDBC statements;
    0 nanoseconds spent executing 0 JDBC batches;
    0 nanoseconds spent performing 0 L2C puts;
    0 nanoseconds spent performing 0 L2C hits;
    0 nanoseconds spent performing 0 L2C misses;
    0 nanoseconds spent executing 0 flushes (flushing a total of 0 entities and 0 collections);
    0 nanoseconds spent executing 0 partial-flushes (flushing a total of 0 entities and 0 collections)
}
[Customer(id=1, customerEmail=basquiat_email_1, customerName=basquiat_name_1, customerMobile=000-000-0002, addresses=[]), Customer(id=2, customerEmail=basquiat_email_2, customerName=basquiat_name_2, customerMobile=my_mobile_2, addresses=[]), Customer(id=3, customerEmail=basquiat_email_3, customerName=basquiat_name_3, customerMobile=my_mobile_3, addresses=[]), Customer(id=4, customerEmail=basquiat_email_4, customerName=basquiat_name_4, customerMobile=my_mobile_4, addresses=[]), Customer(id=5, customerEmail=basquiat_email_5, customerName=basquiat_name_5, customerMobile=my_mobile_5, addresses=[]), Customer(id=6, customerEmail=basquiat_email_6, customerName=basquiat_name_6, customerMobile=my_mobile_6, addresses=[]), Customer(id=7, customerEmail=basquiat_email_7, customerName=basquiat_name_7, customerMobile=my_mobile_7, addresses=[]), Customer(id=8, customerEmail=basquiat_email_8, customerName=basquiat_name_8, customerMobile=my_mobile_8, addresses=[]), Customer(id=9, customerEmail=basquiat_email_9, customerName=basquiat_name_9, customerMobile=my_mobile_9, addresses=[]), Customer(id=10, customerEmail=basquiat_email_10, customerName=basquiat_name_10, customerMobile=my_mobile_10, addresses=[]), Customer(id=11, customerEmail=basquiat_email_11, customerName=basquiat_name_11, customerMobile=my_mobile_11, addresses=[]), Customer(id=12, customerEmail=basquiat_email_12, customerName=basquiat_name_12, customerMobile=my_mobile_12, addresses=[]), Customer(id=13, customerEmail=basquiat_email_13, customerName=basquiat_name_13, customerMobile=my_mobile_13, addresses=[]), Customer(id=14, customerEmail=basquiat_email_14, customerName=basquiat_name_14, customerMobile=my_mobile_14, addresses=[]), Customer(id=15, customerEmail=basquiat_email_15, customerName=basquiat_name_15, customerMobile=my_mobile_15, addresses=[]), Customer(id=16, customerEmail=basquiat_email_16, customerName=basquiat_name_16, customerMobile=my_mobile_16, addresses=[]), Customer(id=17, customerEmail=basquiat_email_17, customerName=basquiat_name_17, customerMobile=my_mobile_17, addresses=[]), Customer(id=18, customerEmail=basquiat_email_18, customerName=basquiat_name_18, customerMobile=my_mobile_18, addresses=[]), Customer(id=19, customerEmail=basquiat_email_19, customerName=basquiat_name_19, customerMobile=my_mobile_19, addresses=[]), Customer(id=20, customerEmail=basquiat_email, customerName=basquiat_name, customerMobile=my_mobile, addresses=[Address(id=1, addressName=대표 주소, addressPostcode=postcode, addressInfo=나는 서울에서 산다., addressDetail=서울에 있는 내집, addressDefaultStatus=Y)])]
```

하지만 우리는 DTO를 통해서 반환을 해야할 필요성이 있는데 이럴 경우에는 지금과는 다른 방식으로 접근을 해야 한다.

그 이유는 고객을 중심으로 놓고 볼때 주소의 정보는 리스트로 나올 수 있기 때문이다. 따라서 우리는 Aggregation을 이용 즉, transform을 이용해야 한다.

그러면 DTO를 한번 만들어 보자.

CustomerDto.java
```
package io.basquiat.customer.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.basquiat.common.model.DateTimeCommon;
import io.basquiat.customer.model.entity.Address;
import io.basquiat.customer.model.entity.Customer;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * 고객 정보를 담은 DTO엔티티
 * created by basquiat
 */
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public class CustomerDto {

    public CustomerDto(Customer customer, List<Address> addresses) {
        this.id = customer.getId();
        this.customerEmail = customer.getCustomerEmail();
        this.customerMobile = customer.getCustomerMobile();
        this.createdAt = customer.getCreatedAt();
        this.updatedAt = customer.getUpdatedAt();
        if(addresses != null) {
            this.addresses = addresses.stream().map(addr -> new AddressDto(addr))
                    .collect(Collectors.toList());
        }
    }

    /** 고객 유니크 아이디 */
    @JsonProperty("customer_id")
    private Long id;

    /** 고객 이메일 */
    @JsonProperty("email")
    private String customerEmail;

    /** 고객 명 */
    @JsonProperty("name")
    private String customerName;

    /** 고객 모바일 */
    @JsonProperty("mobile")
    private String customerMobile;

    private List<AddressDto> addresses;

    /** 생성일 */
    private LocalDateTime createdAt;

    /** 수정일 */
    private LocalDateTime updatedAt;

}

```

AddressDto.java
```
package io.basquiat.customer.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.basquiat.customer.model.entity.Address;
import io.basquiat.customer.model.entity.Customer;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 고객 정보를 담은 DTO엔티티
 * created by basquiat
 */
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public class AddressDto {

    public AddressDto(Address address) {
        this.id = address.getId();
        this.addressName = address.getAddressName();
        this.addressPostcode = address.getAddressPostcode();
        this.addressInfo = address.getAddressInfo();
        this.addressDetail = address.getAddressDetail();
        this.addressDefaultStatus = address.getAddressDefaultStatus().name();
        this.createdAt = address.getCreatedAt();
        this.updatedAt = address.getUpdatedAt();
    }

    @JsonProperty("address_id")
    private Long id;

    /** 고객 주소 설정명 */
    @JsonProperty("addr_name")
    private String addressName;

    /** 우편번호 */
    @JsonProperty("addr_postcode")
    private String addressPostcode;

    /** 메인 주소 */
    @JsonProperty("addr_info")
    private String addressInfo;

    /** 상세 주소 */
    @JsonProperty("addr_detail")
    private String addressDetail;

    /** 기본 디폴트 주소로 설정 여부 */
    @JsonProperty("default")
    private String addressDefaultStatus;

    /** 생성일 */
    private LocalDateTime createdAt;

    /** 수정일 */
    private LocalDateTime updatedAt;

}

```
Dto의 경우 @Getter만 만들어 놓는다.

이제부터 transform을 이용한 Result Aggregation을 구현해보자.

```
package io.basquiat.customer.repository.custom;

import io.basquiat.customer.model.dto.CustomerDto;
import io.basquiat.customer.model.entity.Customer;

import java.util.List;

/**
 * created by basquiat
 */
public interface QueryCustomerRepository {

    List<Customer> findAllCustomer();
    List<CustomerDto> findAllCustomerDto();

}

```
언터페이스에 껍데기 메소드 하나를 더 만들고 해당 메소드를 구현해보자.

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

}

```
코드를 보면 left outer join이후 고객을 중심으로 생성되는 주소에 대한 정보를 컬렉션 객체로 만들어 준다는 것을 알 수 있다.

이것은 myBatis에서는 xml에서 Collection태그를 통한 설정과 같은 방식으로 작동하게 된다.

이 코드는 빈번하게 사용되는 방식으로 관련 문서는 다음을 참고해 보자.
[Result aggregation](http://www.querydsl.com/static/querydsl/4.4.0/reference/html_single/#d0e2233)

그럼 실제 코드 테스트를 해보자

```
@Test
public void selectCustomerByQueryDSLUsingTransform() {
    List<CustomerDto> customers = customerRepository.findAllCustomerDto();
    System.out.println(convertJsonStringFromObject(customers));
}

result: 

Hibernate: 
    /* select
        customer,
        address 
    from
        Customer customer   
    left join
        customer.addresses as address */ select
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
            addresses1_.customer_id as customer9_0_1_ 
        from
            basquiat.basquiat_customer customer0_ 
        left outer join
            basquiat.basquiat_address addresses1_ 
                on customer0_.id=addresses1_.customer_id
2021-02-08 16:24:22.556 TRACE 18012 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([id1_1_0_] : [BIGINT]) - [1]
2021-02-08 16:24:22.556 TRACE 18012 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([id1_0_1_] : [BIGINT]) - [null]
2021-02-08 16:24:22.560 TRACE 18012 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([created_2_1_0_] : [TIMESTAMP]) - [2021-02-08T11:15:30]
2021-02-08 16:24:22.561 TRACE 18012 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([updated_3_1_0_] : [TIMESTAMP]) - [2021-02-08T11:16:35]
2021-02-08 16:24:22.561 TRACE 18012 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([customer4_1_0_] : [VARCHAR]) - [basquiat_email_1]
.
.
.
.
2021-02-08 16:24:22.593 TRACE 18012 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([addr6_0_1_] : [VARCHAR]) - [나는 서울에서 산다.]
2021-02-08 16:24:22.593 TRACE 18012 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([addr_nam7_0_1_] : [VARCHAR]) - [대표 주소]
2021-02-08 16:24:22.593 TRACE 18012 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([addr_pos8_0_1_] : [VARCHAR]) - [postcode]
2021-02-08 16:24:22.593 TRACE 18012 --- [    Test worker] o.h.type.descriptor.sql.BasicExtractor   : extracted value ([customer9_0_1_] : [BIGINT]) - [20]
[{"addresses":[],"createdAt":{"month":"FEBRUARY","dayOfWeek":"MONDAY","dayOfYear":39,"nano":0,"year":2021,"monthValue":2,"dayOfMonth":8,"hour":11,"minute":15,"second":30,"chronology":{"id":"ISO","calendarType":"iso8601"}},"updatedAt":{"month":"FEBRUARY","dayOfWeek":"MONDAY","dayOfYear":39,"nano":0,"year":2021,"monthValue":2,"dayOfMonth":8,"hour":11,"minute":16,"second":35,"chronology":{"id":"ISO","calendarType":"iso8601"}},"customer_id":1,"email":"basquiat_email_1","name":null,"mobile":"000-000-0002"},{"addresses":[],"createdAt":{"month":"FEBRUARY","dayOfWeek":"MONDAY","dayOfYear":39,"nano":0,"year":2021,"monthValue":2,"dayOfMonth":8,"hour":11,"minute":15,"second":31,"chronology":{"id":"ISO","calendarType":"iso8601"}},"updatedAt":null,"customer_id":2,"email":"basquiat_email_2","name":null,"mobile":"my_mobile_2"},{"addresses":[],"createdAt":{"month":"FEBRUARY","dayOfWeek":"MONDAY","dayOfYear":39,"nano":0,"year":2021,"monthValue":2,"dayOfMonth":8,"hour":11,"minute":15,"second":31,"chronology":{"id":"ISO","calendarType":"iso8601"}},"updatedAt":null,"customer_id":3,"email":"basquiat_email_3","name":null,"mobile":"my_mobile_3"},{"addresses":[],"createdAt":{"month":"FEBRUARY","dayOfWeek":"MONDAY","dayOfYear":39,"nano":0,"year":2021,"monthValue":2,"dayOfMonth":8,"hour":11,"minute":15,"second":31,"chronology":{"id":"ISO","calendarType":"iso8601"}},"updatedAt":null,"customer_id":4,"email":"basquiat_email_4","name":null,"mobile":"my_mobile_4"},{"addresses":[],"createdAt":{"month":"FEBRUARY","dayOfWeek":"MONDAY","dayOfYear":39,"nano":0,"year":2021,"monthValue":2,"dayOfMonth":8,"hour":11,"minute":15,"second":31,"chronology":{"id":"ISO","calendarType":"iso8601"}},"updatedAt":null,"customer_id":5,"email":"basquiat_email_5","name":null,"mobile":"my_mobile_5"},{"addresses":[],"createdAt":{"month":"FEBRUARY","dayOfWeek":"MONDAY","dayOfYear":39,"nano":0,"year":2021,"monthValue":2,"dayOfMonth":8,"hour":11,"minute":15,"second":31,"chronology":{"id":"ISO","calendarType":"iso8601"}},"updatedAt":null,"customer_id":6,"email":"basquiat_email_6","name":null,"mobile":"my_mobile_6"},{"addresses":[],"createdAt":{"month":"FEBRUARY","dayOfWeek":"MONDAY","dayOfYear":39,"nano":0,"year":2021,"monthValue":2,"dayOfMonth":8,"hour":11,"minute":15,"second":31,"chronology":{"id":"ISO","calendarType":"iso8601"}},"updatedAt":null,"customer_id":7,"email":"basquiat_email_7","name":null,"mobile":"my_mobile_7"},{"addresses":[],"createdAt":{"month":"FEBRUARY","dayOfWeek":"MONDAY","dayOfYear":39,"nano":0,"year":2021,"monthValue":2,"dayOfMonth":8,"hour":11,"minute":15,"second":31,"chronology":{"id":"ISO","calendarType":"iso8601"}},"updatedAt":null,"customer_id":8,"email":"basquiat_email_8","name":null,"mobile":"my_mobile_8"},{"addresses":[],"createdAt":{"month":"FEBRUARY","dayOfWeek":"MONDAY","dayOfYear":39,"nano":0,"year":2021,"monthValue":2,"dayOfMonth":8,"hour":11,"minute":15,"second":31,"chronology":{"id":"ISO","calendarType":"iso8601"}},"updatedAt":null,"customer_id":9,"email":"basquiat_email_9","name":null,"mobile":"my_mobile_9"},{"addresses":[],"createdAt":{"month":"FEBRUARY","dayOfWeek":"MONDAY","dayOfYear":39,"nano":0,"year":2021,"monthValue":2,"dayOfMonth":8,"hour":11,"minute":15,"second":31,"chronology":{"id":"ISO","calendarType":"iso8601"}},"updatedAt":null,"customer_id":10,"email":"basquiat_email_10","name":null,"mobile":"my_mobile_10"},{"addresses":[],"createdAt":{"month":"FEBRUARY","dayOfWeek":"MONDAY","dayOfYear":39,"nano":0,"year":2021,"monthValue":2,"dayOfMonth":8,"hour":11,"minute":15,"second":31,"chronology":{"id":"ISO","calendarType":"iso8601"}},"updatedAt":null,"customer_id":11,"email":"basquiat_email_11","name":null,"mobile":"my_mobile_11"},{"addresses":[],"createdAt":{"month":"FEBRUARY","dayOfWeek":"MONDAY","dayOfYear":39,"nano":0,"year":2021,"monthValue":2,"dayOfMonth":8,"hour":11,"minute":15,"second":31,"chronology":{"id":"ISO","calendarType":"iso8601"}},"updatedAt":null,"customer_id":12,"email":"basquiat_email_12","name":null,"mobile":"my_mobile_12"},{"addresses":[],"createdAt":{"month":"FEBRUARY","dayOfWeek":"MONDAY","dayOfYear":39,"nano":0,"year":2021,"monthValue":2,"dayOfMonth":8,"hour":11,"minute":15,"second":31,"chronology":{"id":"ISO","calendarType":"iso8601"}},"updatedAt":null,"customer_id":13,"email":"basquiat_email_13","name":null,"mobile":"my_mobile_13"},{"addresses":[],"createdAt":{"month":"FEBRUARY","dayOfWeek":"MONDAY","dayOfYear":39,"nano":0,"year":2021,"monthValue":2,"dayOfMonth":8,"hour":11,"minute":15,"second":31,"chronology":{"id":"ISO","calendarType":"iso8601"}},"updatedAt":null,"customer_id":14,"email":"basquiat_email_14","name":null,"mobile":"my_mobile_14"},{"addresses":[],"createdAt":{"month":"FEBRUARY","dayOfWeek":"MONDAY","dayOfYear":39,"nano":0,"year":2021,"monthValue":2,"dayOfMonth":8,"hour":11,"minute":15,"second":31,"chronology":{"id":"ISO","calendarType":"iso8601"}},"updatedAt":null,"customer_id":15,"email":"basquiat_email_15","name":null,"mobile":"my_mobile_15"},{"addresses":[],"createdAt":{"month":"FEBRUARY","dayOfWeek":"MONDAY","dayOfYear":39,"nano":0,"year":2021,"monthValue":2,"dayOfMonth":8,"hour":11,"minute":15,"second":31,"chronology":{"id":"ISO","calendarType":"iso8601"}},"updatedAt":null,"customer_id":16,"email":"basquiat_email_16","name":null,"mobile":"my_mobile_16"},{"addresses":[],"createdAt":{"month":"FEBRUARY","dayOfWeek":"MONDAY","dayOfYear":39,"nano":0,"year":2021,"monthValue":2,"dayOfMonth":8,"hour":11,"minute":15,"second":31,"chronology":{"id":"ISO","calendarType":"iso8601"}},"updatedAt":null,"customer_id":17,"email":"basquiat_email_17","name":null,"mobile":"my_mobile_17"},{"addresses":[],"createdAt":{"month":"FEBRUARY","dayOfWeek":"MONDAY","dayOfYear":39,"nano":0,"year":2021,"monthValue":2,"dayOfMonth":8,"hour":11,"minute":15,"second":31,"chronology":{"id":"ISO","calendarType":"iso8601"}},"updatedAt":null,"customer_id":18,"email":"basquiat_email_18","name":null,"mobile":"my_mobile_18"},{"addresses":[],"createdAt":{"month":"FEBRUARY","dayOfWeek":"MONDAY","dayOfYear":39,"nano":0,"year":2021,"monthValue":2,"dayOfMonth":8,"hour":11,"minute":15,"second":31,"chronology":{"id":"ISO","calendarType":"iso8601"}},"updatedAt":null,"customer_id":19,"email":"basquiat_email_19","name":null,"mobile":"my_mobile_19"},{"addresses":[{"createdAt":{"month":"FEBRUARY","dayOfWeek":"MONDAY","dayOfYear":39,"nano":0,"year":2021,"monthValue":2,"dayOfMonth":8,"hour":11,"minute":18,"second":44,"chronology":{"id":"ISO","calendarType":"iso8601"}},"updatedAt":null,"address_id":1,"addr_name":"대표 주소","addr_postcode":"postcode","addr_info":"나는 서울에서 산다.","addr_detail":"서울에 있는 내집","default":"Y"}],"createdAt":{"month":"FEBRUARY","dayOfWeek":"MONDAY","dayOfYear":39,"nano":0,"year":2021,"monthValue":2,"dayOfMonth":8,"hour":11,"minute":18,"second":44,"chronology":{"id":"ISO","calendarType":"iso8601"}},"updatedAt":null,"customer_id":20,"email":"basquiat_email","name":null,"mobile":"my_mobile"}]
```
자 그러면 이제부터 무엇을 해야 하는가? 바로 앞서 봤던 동적 쿼리를 할 시간이다.     

그런데 첫 브랜치에서 너무 긴 내용을 했기 때문에 동적 쿼리는 다음 시간에...

[* 번외 : 외래키 설정이 없고 relation을 사용하지 않는다면 어떨까?](https://github.com/basquiat78/spring-boot-querydsl/tree/no-relation-eg)

[1. 동적 쿼리](https://github.com/basquiat78/spring-boot-querydsl/tree/dynamic-search-dsl)

[2. 페이징](https://github.com/basquiat78/spring-boot-querydsl/tree/query-dsl-paging)