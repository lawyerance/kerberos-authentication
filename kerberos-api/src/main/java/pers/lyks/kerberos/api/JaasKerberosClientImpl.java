package pers.lyks.kerberos.api;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.lyks.kerberos.util.JaasUtils;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link KerberosClient} which uses the SUN JAAS
 * login module, which is included in the SUN JRE, it will not work with an IBM JRE.
 * The whole configuration is done in this class, no additional JAAS configuration
 * is needed.
 *
 * @author lawyerance
 * @version 1.0 2019-12-01
 */
public class JaasKerberosClientImpl implements KerberosClient {
    private static final Logger logger = LoggerFactory.getLogger(JaasKerberosClientImpl.class);
    private boolean debug = false;
    private boolean multiTier = false;

    @Override
    public JaasSubjectContainer login(String username, String password) {
        logger.debug("Trying to authenticate " + username + " with Kerberos");
        JaasSubjectContainer result;

        try {
            LoginContext loginContext = new LoginContext("", null, new KerberosClientCallbackHandler(username, password), new LoginModuleConfig(this.debug));
            loginContext.login();
            Subject jaasSubject = loginContext.getSubject();
            if (logger.isDebugEnabled()) {
                logger.debug("Kerberos authenticated user: " + jaasSubject);
            }
            String validatedUsername = jaasSubject.getPrincipals().iterator().next().toString();
            Subject subjectCopy = JaasUtils.deepCopy(jaasSubject);
            result = new JaasSubjectContainer(validatedUsername, subjectCopy);
            if (!multiTier) {
                loginContext.logout();
            }
        } catch (LoginException e) {
            throw new RuntimeException("Kerberos authentication failed", e);
        }
        return result;
    }

    private static class LoginModuleConfig extends Configuration {
        private boolean debug;

        public LoginModuleConfig(boolean debug) {
            super();
            this.debug = debug;
        }

        @Override
        public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
            Map<String, String> options = new HashMap<>();
            options.put("storeKey", "true");
            if (debug) {
                options.put("debug", "true");
            }

            return new AppConfigurationEntry[]{new AppConfigurationEntry("com.sun.security.auth.module.Krb5LoginModule",
                AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options),};
        }

    }


    private static class KerberosClientCallbackHandler implements CallbackHandler {
        private String username;
        private String password;

        public KerberosClientCallbackHandler(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
            for (Callback callback : callbacks) {
                if (callback instanceof NameCallback) {
                    NameCallback ncb = (NameCallback) callback;
                    ncb.setName(username);
                } else if (callback instanceof PasswordCallback) {
                    PasswordCallback pwcb = (PasswordCallback) callback;
                    pwcb.setPassword(password.toCharArray());
                } else {
                    throw new UnsupportedCallbackException(callback, "We got a " + callback.getClass().getCanonicalName()
                        + ", but only NameCallback and PasswordCallback is supported");
                }
            }

        }

    }
}
