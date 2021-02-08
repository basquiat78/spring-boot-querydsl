package io.basquiat.common.model;

import java.util.Arrays;

/**
 * searchDateType enum
 * created by basquiat
 */
public enum DateFormatType {

    y_M_d_H_m_s("yyyy-MM-dd HH:mm:ss"),

    y_M_d("yyyy-MM-dd"),

    yMdHms("yyyyMMddHHmmss");

    public String pattern;

    /** DateFormatType type constructor */
    DateFormatType(String pattern) {
        this.pattern = pattern;
    }

    /**
     * get Enum Object from pattern
     * @param pattern
     * @return SearchDateType
     */
    public static DateFormatType fromString(String pattern) {
        return Arrays.asList(DateFormatType.values())
                     .stream()
                     .filter( dateFormatType -> dateFormatType.pattern.equalsIgnoreCase(pattern) )
                     .findFirst().orElse(null);
    }

}
