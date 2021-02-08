package io.basquiat.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Common Utils
 * created by basquiat
 */
public class CommonUtils {

    /**
     * Object convert to json String
     *
     * @param object
     * @return String
     * @throws JsonProcessingException
     */
    public static String convertJsonStringFromObject(Object object) {
        String result = "";
        try {
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

}
