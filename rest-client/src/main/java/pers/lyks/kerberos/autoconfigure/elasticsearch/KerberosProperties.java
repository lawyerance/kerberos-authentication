package pers.lyks.kerberos.autoconfigure.elasticsearch;

/**
 * @author lawyerance
 * @version 1.0 2019-11-22
 */
public class KerberosProperties {
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
