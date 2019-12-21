package pers.lyks.kerberos.autoconfigure;

/**
 * Kerberos authenticate base information class.
 *
 * @author lawyerance
 * @version 1.0 2019-11-22
 */
public class KerberosProperties {

    private String username;
    private String password;
    private String loginModule;

    public KerberosProperties(String loginModule) {
        this.loginModule = loginModule;
    }

    public void resetUsername(String newValue) {
        if (null == this.username) {
            this.username = newValue;
        }
    }

    public void resetPassword(String newValue) {
        if (null == this.password) {
            this.password = newValue;
        }
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
