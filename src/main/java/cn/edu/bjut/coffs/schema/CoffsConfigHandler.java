package cn.edu.bjut.coffs.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Created by chenshouqin on 2016-08-13 14:45.
 */
public class CoffsConfigHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("config", new ConfigBeanDefinitionParser());
    }
}
