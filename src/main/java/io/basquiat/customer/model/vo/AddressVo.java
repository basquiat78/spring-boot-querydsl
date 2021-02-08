package io.basquiat.customer.model.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.basquiat.common.model.SearchDateType;
import io.basquiat.customer.model.entity.Address;
import lombok.Getter;
import lombok.Setter;

/**
 * 검색 조건 값을 담는 VO
 */
@Getter
@Setter
public class AddressVo {

    /** 고객 주소 설정명 */
    private String name;

    /** 우편번호 */
    private String postcode;

    /** 메인 주소 */
    private String info;

    /** 상세 주소 */
    private String detail;

    /** 기본 디폴트 주소로 설정 여부 */
    private Address.AddressDefaultStatus addressDefaultStatus;

}
