package cn.edu.bjut.coffs.exception;

/**
 * Created by chenshouqin on 2016-07-07 21:19.
 */
public class InvokeException extends AbstractException {

    public InvokeException(int code, String desc) {
        super(desc);
        this.code = code;
    }

    public InvokeException() {
        this.code = 20000;
        this.desc = "invoke method failed";
    }
}
