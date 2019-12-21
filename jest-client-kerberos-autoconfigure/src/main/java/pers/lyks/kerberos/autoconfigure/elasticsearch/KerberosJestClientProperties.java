package pers.lyks.kerberos.autoconfigure.elasticsearch;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.elasticsearch.jest.JestProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lawyerance
 * @version 1.0 2019-11-22
 */
@ConfigurationProperties(prefix = "spring.elasticsearch.jest")
public class KerberosJestClientProperties implements InitializingBean {
    @Autowired
    private JestProperties jestProperties;

    private boolean compatible;
    private final KerberosProperties kerberos = new KerberosProperties("JestClient");

    @Override
    public void afterPropertiesSet() throws Exception {
        kerberos.resetUsername(jestProperties.getUsername());
        kerberos.resetPassword(jestProperties.getPassword());
    }

    public KerberosProperties getKerberos() {
        return kerberos;
    }

    public boolean isCompatible() {
        return compatible;
    }

    public void setCompatible(boolean compatible) {
        this.compatible = compatible;
    }
}
