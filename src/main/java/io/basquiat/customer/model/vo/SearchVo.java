package io.basquiat.customer.model.vo;

import io.basquiat.common.model.SearchDateType;
import lombok.*;

/**
 * 검색 조건 값을 담는 VO
 */
@Getter
@Setter
public class SearchVo {

    private Long id;

    private String name;

    private String email;

    private SearchDateType searchDateType;

    private String start;

    private String end;

}
