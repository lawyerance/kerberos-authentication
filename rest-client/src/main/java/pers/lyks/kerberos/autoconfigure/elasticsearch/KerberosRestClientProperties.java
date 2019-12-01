package pers.lyks.kerberos.autoconfigure.elasticsearch;

import org.elasticsearch.Version;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lawyerance
 * @version 1.0 2019-11-22
 */
@ConfigurationProperties(prefix = "spring.elasticsearch.rest")
public class KerberosRestClientProperties implements InitializingBean {
    private static final byte ELASTIC_MAJOR_VERSION = 6;
    @Autowired
    private RestClientProperties restClientProperties;
    private boolean compatible;
    private final KerberosProperties kerberos = new KerberosProperties("RestClient");

    @Override
    public void afterPropertiesSet() throws Exception {
        kerberos.resetPassword(restClientProperties.getUsername());
        kerberos.resetPassword(restClientProperties.getPassword());
        compatible = (Version.CURRENT.major <= ELASTIC_MAJOR_VERSION);
    }

    public KerberosProperties getKerberos() {
        return kerberos;
    }

    public boolean isCompatible() {
        return compatible;
    }
}
