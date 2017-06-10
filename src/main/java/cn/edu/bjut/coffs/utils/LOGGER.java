package cn.edu.bjut.coffs.utils;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import org.apache.log4j.Logger;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by chenshouqin on 2016-07-07 16:22.
 */
public class LOGGER {

    private static Joiner joiner = Joiner.on("|").skipNulls();
    private static Logger errorLogger = Logger.getLogger("ERROR");
    private static Logger infoLogger = Logger.getLogger("INFO");


    /**
     * 记录错误日志
     * @param
     * @param t
     */
    public static void errorLog(Class clazz, String methodName, Throwable t) {
        checkArgument(Optional.fromNullable(clazz).isPresent());
        checkArgument(Optional.fromNullable(t).isPresent());
        methodName = Optional.fromNullable(methodName).or("-");
        errorLogger.error(joiner.join(clazz.getName(), methodName), t);
    }

    /**
     * 记录 info 日志
     * @param clazz
     * @param methodName
     * @param msg
     */
    public static void infoLog(Class clazz, String methodName, String msg) {
        checkArgument(Optional.fromNullable(clazz).isPresent());
        methodName = Optional.fromNullable(methodName).or("-");
        msg = Optional.fromNullable(msg).or("-");
        infoLogger.info(joiner.join(clazz.getName(), methodName, msg));
    }

}
