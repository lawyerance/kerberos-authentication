package pers.lyks.kerberos.autoconfigure.rest;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pers.lyks.kerberos.autoconfigure.KerberosProperties;

/**
 * @author lawyerance
 * @version 1.0 2019-11-22
 */
@ConfigurationProperties(prefix = "spring.elasticsearch.rest")
public class KerberosRestClientProperties implements InitializingBean {
    @Autowired
    private RestClientProperties restClientProperties;

    private final KerberosProperties kerberos = new KerberosProperties("RestClient");

    @Override
    public void afterPropertiesSet() throws Exception {
        kerberos.resetUsername(restClientProperties.getUsername());
        kerberos.resetPassword(restClientProperties.getPassword());
    }

    public KerberosProperties getKerberos() {
        return kerberos;
    }

}
