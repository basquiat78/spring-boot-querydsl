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
