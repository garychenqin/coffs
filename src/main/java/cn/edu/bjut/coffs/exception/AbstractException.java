package cn.edu.bjut.coffs.exception;

/**
 * Created by chenshouqin on 2016-07-07 21:17.
 */
public class AbstractException extends Exception {
    protected int code;
    protected String desc;

    public AbstractException(String desc) {
        super(desc);
        this.desc = desc;
    }

    public AbstractException(){}

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
