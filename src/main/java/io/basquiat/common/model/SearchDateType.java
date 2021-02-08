package io.basquiat.common.model;

import java.util.Arrays;

/**
 * searchDateType enum
 * created by basquiat
 */
public enum SearchDateType {

    CREATED_AT("createdAt"),

    UPDATED_AT("updatedAt");

    public String key;

    /** SearchDateType type constructor */
    SearchDateType(String key) {
        this.key = key;
    }

    /**
     * get Enum Object from key
     * @param key
     * @return SearchDateType
     */
    public static SearchDateType fromString(String key) {
        return Arrays.asList(SearchDateType.values())
                     .stream()
                     .filter( searchDateType -> searchDateType.key.equalsIgnoreCase(key) )
                     .findFirst().orElse(null);
    }

}
