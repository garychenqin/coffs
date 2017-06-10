package cn.edu.bjut.coffs.processor;

import cn.edu.bjut.coffs.annotation.*;
import cn.edu.bjut.coffs.api.HttpConditionRequest;
import cn.edu.bjut.coffs.api.RequestValue;
import cn.edu.bjut.coffs.enums.RequestType;
import cn.edu.bjut.coffs.exception.InitControllerException;
import cn.edu.bjut.coffs.exception.InvokeException;
import cn.edu.bjut.coffs.exception.PathNotFoundException;
import cn.edu.bjut.coffs.filter.Filter;
import cn.edu.bjut.coffs.utils.*;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * Created by chenshouqin on 2016-07-07 16:48.
 */
public class RequestProcessor {

    private int port;
    private Set<String> basePackage;
    private Set<Filter> filters = Sets.newHashSet();
    private final Map<String, HttpConditionRequest> snsRequestMap = HashBiMap.create();
    private final JsonMapper jsonMapper = JsonMapper.nonDefaultMapper();


    /**
     * 初始化注解
     */
    public void init() {
        if(null == basePackage) {
            throw new InitControllerException("base package is null ! ");
        }
        Set<Class<?>> classes = ClassUtil.getClasses(basePackage);
        if (null != classes || !classes.isEmpty()) {
            for (Class<?> clazz : classes) {
                RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);

                if (!Optional.fromNullable(requestMapping).isPresent()) {
                    continue;
                }

                if (Strings.isNullOrEmpty(requestMapping.pathPrefix())) {
                    throw new InitControllerException("requestMapping url cannot empty!");
                }

                String pathPrefix = requestMapping.pathPrefix();
                Method[] methods = clazz.getMethods();

                for (Method method : methods) {
                    RequestParams requestParams = method.getAnnotation(RequestParams.class);
                    Json json = method.getAnnotation(Json.class);
                    if (null != requestParams) {
                        String path = requestParams.path();
                        path = pathPrefix.toLowerCase() + path.toLowerCase();
                        boolean isJsonFormat = null == json ? false : true;
                        HttpConditionRequest httpConditionRequest = new HttpConditionRequest(path, requestParams.methods(),
                                requestParams.required(), method, SpringContextUtil.getBeanByClass(clazz), isJsonFormat);
                        LOGGER.infoLog(RequestProcessor.class, "init", "init controller methods : " + httpConditionRequest.toString());
                        snsRequestMap.put(path, httpConditionRequest);
                    }
                }
            }
        }

        Map<String, Filter> filterMap = SpringContextUtil.getBeans(Filter.class);
        if (null != filterMap && !filterMap.isEmpty()) {
            Set<String> keys = filterMap.keySet();
            for (String key : keys) {
                filters.add(filterMap.get(key));
            }
        }
    }

    /**
     * 处理 http 请求
     *
     * @param path        请求路径
     * @param mapParams   参数列表
     * @param requestType 请求类型（暂支持GET， POST）
     * @return
     */
    public String processHttpRequest(String path, Map<String, RequestValue> mapParams, RequestType requestType) {
        long beginTime = System.currentTimeMillis();
        for (Filter filter : filters) {
            if (!filter.process(path, mapParams)) {
                return StatusUtil.format(filter.getCheckFailedCode(), filter.getCheckFailedDesc(), null);
            }
        }
        path = path.toLowerCase();
        HttpConditionRequest httpConditionRequest = snsRequestMap.get(path);

        if (null != httpConditionRequest) {
            Method method = httpConditionRequest.getMethod();
            Object apiController = httpConditionRequest.getApiInstance();
            Set<RequestType> methodSupportsSet = httpConditionRequest.getMethodSupports();
            Set<String> requireParams = httpConditionRequest.getRequireSet();

            try {
                // 检查请求方法是否支持
                if (!methodSupportsSet.contains(requestType)) {
                    return StatusUtil.format(20000, "failed", requestType +" not support");
                }

                // 检查参数是否缺失
                StringBuilder sb = new StringBuilder();
                for(String param : requireParams) {
                    if(!mapParams.containsKey(param)) {
                        if(0 != sb.length()) {
                            sb.append(",");
                        }
                        sb.append(param);
                    }
                }
                if(0 != sb.length()) {
                    sb.insert(0, "required param(s): ");
                    return StatusUtil.format(20000, "failed", sb.toString());
                }

                // 反射获取参数注解并赋值
                Class<?>[] clazzes = method.getParameterTypes();
                Annotation[][] annotations = method.getParameterAnnotations();
                Object[] params = new Object[clazzes.length];

                for(int i = 0; i < annotations.length; i++) {
                    Annotation[] paramAnnotations = annotations[i];
                    if(0 == paramAnnotations.length) {
                        params[i] = null;
                        continue;
                    }
                    Class clazz = clazzes[i];
                    for(int j = 0; j < paramAnnotations.length; j++) {

                        if(annotations[i][j] instanceof Param) {
                            Param p = (Param) annotations[i][j];
                            String value = p.value();
                            Object param = null;
                            try {
                                param = createParam(clazz, value, mapParams);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            params[i] = param;
                        }

                        if(annotations[i][j] instanceof Model) {
                            Object o = clazz.newInstance();
                            Field[] fields = o.getClass().getDeclaredFields();
                            for(Field field : fields) {
                                field.setAccessible(true);
                                String fieldName = field.getName();
                                Class<?> type = field.getType();
                                if(mapParams.containsKey(fieldName)) {
                                    field.set(o, createParam(type, fieldName, mapParams));
                                }
                            }
                            params[i] = o;
                        }
                    }
                }

                Object result = method.invoke(apiController, params);

                // 检查是否需要返回json串，如果需要返回则进行格式化，否则直接返回字符串
                if(httpConditionRequest.isJsonFormat()) {
                    return jsonMapper.toJson(result);
                } else {
                    return result.toString();
                }
            } catch (Exception e) {
                LOGGER.errorLog(RequestProcessor.class, "processHttpRequest", e);
                return StatusUtil.format(new InvokeException(), path);
            }
        }

        LOGGER.infoLog(RequestProcessor.class, "processHttpRequest", "execute time : " + (System.currentTimeMillis() - beginTime));

        return StatusUtil.format(new PathNotFoundException());
    }

    /**
     * 根据实际的类型进行转换，暂时只支持基本数据类型
     * @param clazz
     * @param value
     * @param mapParams
     * @return
     */
    private Object createParam(Class<?> clazz, String value, Map<String, RequestValue> mapParams) {
        if(clazz == String.class) {
            return mapParams.get(value).toString();
        }
        if(clazz == int.class || clazz == Integer.class) {
            return Integer.parseInt(mapParams.get(value).toString());
        }
        if(clazz == boolean.class || clazz == Boolean.class) {
            return Boolean.valueOf(mapParams.get(value).toString());
        }
        if(clazz == long.class || clazz == Long.class) {
            return Long.parseLong(mapParams.get(value).toString());
        }
        if(clazz == double.class || clazz == Double.class) {
            return Double.parseDouble(mapParams.get(value).toString());
        }
        if(clazz == byte.class || clazz == Byte.class) {
            return Byte.parseByte(mapParams.get(value).toString());
        }
        if(clazz == short.class || clazz == Short.class) {
            return Short.parseShort(mapParams.get(value).toString());
        }
        return null;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Set<String> getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(Set<String> basePackage) {
        this.basePackage = basePackage;
    }
}
