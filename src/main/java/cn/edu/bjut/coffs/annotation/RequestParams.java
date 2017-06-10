package cn.edu.bjut.coffs.annotation;

import cn.edu.bjut.coffs.enums.RequestType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by chenshouqin on 2016-07-07 19:53.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequestParams {
    String path();
    RequestType[] methods() default {RequestType.GET, RequestType.POST};
    String[] required() default {};
}
