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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static io.basquiat.common.utils.CommonUtils.isEmpty;

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
        if(!isEmpty(addresses)) {
            this.addresses = addresses.stream().map(addr -> new AddressDto(addr))
                                               .collect(Collectors.toList());
        } else {
            this.addresses = (List<AddressDto>) Collections.EMPTY_LIST;
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
