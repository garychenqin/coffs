package cn.edu.bjut.coffs.exception;

/**
 * Created by chenshouqin on 2016-07-07 21:26.
 */
public class PathNotFoundException extends AbstractException {

    public PathNotFoundException() {
        this.code = 20001;
        this.desc = "url path not found ! ";
    }
}
