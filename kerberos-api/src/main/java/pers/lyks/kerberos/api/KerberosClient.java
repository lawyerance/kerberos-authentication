package pers.lyks.kerberos.api;

/**
 * @author lawyerance
 * @version 1.0 2019-12-01
 */
public interface KerberosClient {
    JaasSubjectContainer login(String username, String password);
}
