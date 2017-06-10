package cn.edu.bjut.coffs.filter;

import cn.edu.bjut.coffs.api.RequestValue;

import java.util.Map;

/**
 * Created by chenshouqin on 2016-07-07 20:27.
 */
public interface Filter {
    boolean process(String path, Map<String, RequestValue> mapParams);
    String getCheckFailedDesc();
    int getCheckFailedCode();
}
