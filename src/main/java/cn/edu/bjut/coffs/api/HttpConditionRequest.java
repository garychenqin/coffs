package cn.edu.bjut.coffs.api;

import cn.edu.bjut.coffs.enums.RequestType;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by chenshouqin on 2016-07-07 19:59.
 */
public class HttpConditionRequest {

    private final String path;
    private final Set<RequestType> methodSupports;
    private final Set<String> requireSet;
    private final Method method;
    private final Object apiInstance;
    private final boolean isJsonFormat;

    public HttpConditionRequest(String path, RequestType[] methods, String[] reqiured,
                                Method method, Object object, boolean isJsonFormat) {
        this.path = path;
        this.method = method;
        this.apiInstance = object;
        this.isJsonFormat = isJsonFormat;

        methodSupports = ImmutableSet.copyOf(methods);
        requireSet = ImmutableSet.copyOf(reqiured);
    }

    public String getPath() {
        return path;
    }

    public Set<RequestType> getMethodSupports() {
        return methodSupports;
    }

    public Set<String> getRequireSet() {
        return requireSet;
    }

    public Method getMethod() {
        return method;
    }

    public Object getApiInstance() {
        return apiInstance;
    }

    public boolean isJsonFormat() {
        return isJsonFormat;
    }

    @Override
    public String toString() {
        return "HttpConditionRequest{" +
                "path='" + path + '\'' +
                ", methodSupports=" + methodSupports +
                ", requireSet=" + requireSet +
                ", method=" + method +
                ", apiInstance=" + apiInstance +
                '}';
    }
}
