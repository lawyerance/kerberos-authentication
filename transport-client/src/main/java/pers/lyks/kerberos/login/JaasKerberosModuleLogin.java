package pers.lyks.kerberos.login;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.login.LoginContext;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.HashSet;

/**
 * @author lawyerance
 * @version 1.0 2019-11-25
 */
public class JaasKerberosModuleLogin {

    private final String userPrincipalName;
    private String password;
    private final String loginModule;

    public JaasKerberosModuleLogin(String userPrincipalName, String password, String loginModule) {
        this.userPrincipalName = userPrincipalName;
        this.password = password;
        this.loginModule = loginModule;
    }


    public JaasKerberosModuleLogin(String userPrincipalName, String loginModule) {
        this.userPrincipalName = userPrincipalName;
        this.loginModule = loginModule;
    }


    public LoginContext login() throws PrivilegedActionException {
        return AccessController.doPrivileged((PrivilegedExceptionAction<LoginContext>) () -> {
            final Subject subject = new Subject(false, Collections.singleton(new KerberosPrincipal(userPrincipalName)), new HashSet<>(), new HashSet<>());
            final LoginContext loginContext = new LoginContext(this.loginModule, subject, password != null ? new KebCallbackHandler(userPrincipalName, password) : null, null);
            loginContext.login();
            return loginContext;
        });
    }


    /**
     * JAAS call back handle to provide credentials
     */
    static class KebCallbackHandler implements CallbackHandler {
        private final String principal;
        private final String password;

        KebCallbackHandler(String principal, String password) {
            this.principal = principal;
            this.password = password;
        }

        @Override
        public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
            for (Callback callback : callbacks) {
                if (callback instanceof PasswordCallback) {
                    PasswordCallback pc = (PasswordCallback) callback;
                    if (pc.getPrompt().contains(principal)) {
                        pc.setPassword(password.toCharArray());
                        break;
                    }
                }
            }
        }
    }
}
