package cn.edu.bjut.coffs.schema;

import cn.edu.bjut.coffs.processor.RequestProcessor;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

import java.util.Set;

/**
 * Created by chenshouqin on 2016-08-13 14:36.
 */
public class ConfigBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        String portString = element.getAttribute("port");

        int port;
        try {
            port = Integer.parseInt(portString);
        } catch (NumberFormatException e) {
            throw new RuntimeException("port config is not a number");
        }

        String basePackageConfig = element.getAttribute("base-package");
        if(Strings.isNullOrEmpty(basePackageConfig)) {
            throw new RuntimeException("please set basePackage!");
        }
        String[] basePackageArray = basePackageConfig.split(",");
        if(null == basePackageArray || 0 == basePackageArray.length) {
            throw new RuntimeException("please set basePackage correctly!");
        }
        Set<String> basePackage = Sets.newHashSet(basePackageArray);

        builder.addPropertyValue("port", port);
        builder.addPropertyValue("basePackage", basePackage);
    }

    @Override
    protected Class<?> getBeanClass(Element element) {
        return RequestProcessor.class;
    }
}
