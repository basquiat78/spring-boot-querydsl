package io.basquiat.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

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

    /**
     * 주어진 객체가 null이거나 값이 비어있는지 검사하고 boolean을 반환한다.
     * @param obj
     * @return boolean
     */
    public static boolean isEmpty(Object obj) {
        if(obj instanceof String) {
            return obj == null || "".equals(obj.toString().trim());
        } else if(obj instanceof List) {
            return obj == null || ((List<?>) obj).isEmpty();
        } else if(obj instanceof Map) {
            return obj == null || ((Map<?,?>) obj).isEmpty();
        } else if(obj instanceof Object[]) {
            return obj == null || Array.getLength(obj) == 0;
        } else {
            return obj == null;
        }
    }

}
