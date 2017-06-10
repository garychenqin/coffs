package cn.edu.bjut.coffs.utils;

import cn.edu.bjut.coffs.exception.AbstractException;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created by chenshouqin on 2016-07-07 20:37.
 */
public class StatusUtil {

    private static JsonMapper jsonMapper = JsonMapper.nonDefaultMapper();
    private static final int SUCCESS = 10000;

    public static String format(int status, String desc, Object data) {
        Map<String, Object> result = Maps.newLinkedHashMap();
        result.put("status", status);
        result.put("desc", Strings.nullToEmpty(desc));
        result.put("data", Optional.fromNullable(data).or(new Object()));
        return jsonMapper.toJson(result);
    }

    public static String format(Object data) {
        Map<String, Object> result = Maps.newLinkedHashMap();
        result.put("code", SUCCESS);
        result.put("status", "success");
        result.put("desc", "");
        result.put("data", Optional.fromNullable(data).or(new Object()));
        return jsonMapper.toJson(result);
    }

    public static String format(AbstractException exception, Object data) {
        Map<String, Object> result = Maps.newLinkedHashMap();
        result.put("status", exception.getCode());
        result.put("desc", exception.getDesc());
        result.put("data", Optional.fromNullable(data).or(new Object()));
        return jsonMapper.toJson(result);
    }

}
