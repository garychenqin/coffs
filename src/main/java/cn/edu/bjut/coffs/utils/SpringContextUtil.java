package cn.edu.bjut.coffs.utils;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by chenshouqin on 2016-07-07 15:49.
 */

@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * 实现 applicationContextAware 接口的 context 注入函数，保存为静态变量
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        checkArgument(null != applicationContext);
        this.applicationContext = applicationContext;
    }

    /**
     * 获取 applicationContext
     * @return
     */
     public static ApplicationContext getApplicationContext() {
         checkApplicationContext();
         return applicationContext;
     }

    /**
     * 根据名称获取bean
     * @param name bean 名称
     * @param <T>
     * @return
     */
    public static <T> T getBeanByName(String name) {
        checkApplicationContext();
        checkArgument(!Strings.isNullOrEmpty(name), "bean name cannot empty ! ");
        return (T) applicationContext.getBean(name);
    }

    /**
     * 根据 class 获取 bean
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBeanByClass(Class<T> clazz) {
        checkApplicationContext();
        checkArgument(Optional.fromNullable(clazz).isPresent());
        return applicationContext.getBean(clazz);
    }

    /**
     * 根据 class 获取所有的 bean
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> Map<String, T> getBeans(Class<T> clazz) {
        checkApplicationContext();
        checkArgument(Optional.fromNullable(clazz).isPresent());
        return applicationContext.getBeansOfType(clazz);
    }

    /**
     * 检查applicationContext 是否为 null
     */
    private static void checkApplicationContext() {
        boolean isNotNull = Optional.fromNullable(applicationContext).isPresent();
        if(! isNotNull) {
            throw new IllegalStateException("applicationContext is null !");
        }
    }

}
