package pers.lyks.kerberos.autoconfigure.elasticsearch;

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
    @Autowired
    private RestClientProperties restClientProperties;
    private boolean compatible;
    private final KerberosProperties kerberos = new KerberosProperties("RestClient");

    @Override
    public void afterPropertiesSet() throws Exception {
        kerberos.resetUsername(restClientProperties.getUsername());
        kerberos.resetPassword(restClientProperties.getPassword());
    }


    public static class KerberosProperties {
        private boolean enabled = true;
        private String username;
        private String password;
        private String loginModule;

        public KerberosProperties(String loginModule) {
            this.loginModule = loginModule;
        }

        void resetUsername(String newValue) {
            if (null == this.username) {
                this.username = newValue;
            }
        }

        void resetPassword(String newValue) {
            if (null == this.password) {
                this.password = newValue;
            }
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getLoginModule() {
            return loginModule;
        }

        public void setLoginModule(String loginModule) {
            this.loginModule = loginModule;
        }
    }

    public KerberosProperties getKerberos() {
        return kerberos;
    }

    @Deprecated
    public boolean isCompatible() {
        return compatible;
    }

    @Deprecated
    public void setCompatible(boolean compatible) {
        this.compatible = compatible;
    }
}
