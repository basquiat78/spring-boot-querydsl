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
