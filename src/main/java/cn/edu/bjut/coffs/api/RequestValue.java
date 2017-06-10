package cn.edu.bjut.coffs.api;

/**
 * Created by chenshouqin on 2016-07-07 20:17.
 */
public class RequestValue {

    public enum RequestParamsType {
        STRING, STRING_ARRAY, BYTEARRAY, MAP
    }

    public RequestParamsType type;

    public Object value;

    public RequestValue(RequestParamsType type, Object value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
