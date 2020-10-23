package net.crisps.cloud.framework.web.config;

import lombok.extern.slf4j.Slf4j;
import net.crisps.cloud.framework.web.util.BeanRegistrationUtil;
import net.crisps.cloud.framework.web.util.DozerUtil;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Administrator
 */
@Slf4j
public class UtilRegistry implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, DozerUtil.class.getName(), DozerUtil.class);
    }
}
