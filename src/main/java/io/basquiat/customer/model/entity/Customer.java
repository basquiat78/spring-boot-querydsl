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
