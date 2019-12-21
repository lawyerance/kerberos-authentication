package pers.lyks.kerberos;

import org.junit.Test;

import javax.security.auth.Subject;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lawyerance
 * @version 1.0 2019-12-07
 */
public class ConfigurationTest {

    @Test
    public void testConfig() throws Exception {
        Configuration configuration = new Configuration() {
            @Override
            public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
                Map<String, Object> options = new HashMap<>();
                options.put("debug", true);
                options.put("ticketCache", true);
                AppConfigurationEntry entry = new AppConfigurationEntry("com.sun.security.auth.module.Krb5LoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options);
                return new AppConfigurationEntry[]{entry};
            }
        };
        Subject subject = new Subject();
        LoginContext loginContext = new LoginContext("HttpClient", subject, null, configuration);
        loginContext.login();
    }
}
