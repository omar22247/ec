package E_commerce.com.SecureEcommerceApplication.config;

import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DataSourceProxyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSource) {
            return ProxyDataSourceBuilder
                    .create((DataSource) bean)
                    .name("DS-Proxy")
                    .countQuery()
                    .logQueryToSysOut()
                    .build();
        }
        return bean;
    }
}
