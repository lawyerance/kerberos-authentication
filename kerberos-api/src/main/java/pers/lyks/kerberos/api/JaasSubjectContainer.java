package pers.lyks.kerberos.api;

import javax.security.auth.Subject;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lawyerance
 * @version 1.0 2019-12-01
 */
public class JaasSubjectContainer {

    private final Map<String, byte[]> tokenCaches = new HashMap<>();

    private String username;
    private Subject subject;

    public JaasSubjectContainer(Subject subject) {
        this.subject = subject;
    }

    public JaasSubjectContainer(String username, Subject subject) {
        this.username = username;
        this.subject = subject;
    }

    public void addToken(String targetService, byte[] token) {
        this.tokenCaches.put(targetService, token);
    }

    public byte[] getToken(String targetService) {
        return this.tokenCaches.get(targetService);
    }

    public String getUsername() {
        return username;
    }

    public Subject getSubject() {
        return subject;
    }
}
