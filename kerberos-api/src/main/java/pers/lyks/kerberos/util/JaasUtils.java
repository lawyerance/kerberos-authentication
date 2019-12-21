package pers.lyks.kerberos.util;

import javax.security.auth.Subject;
import java.util.HashSet;

/**
 * @author lawyerance
 * @version 1.0 2019-12-01
 */
public final class JaasUtils {
    private JaasUtils() {

    }

    /**
     * Copy the principal and the credentials into a new Subject.
     *
     * @param subject
     * @return
     */
    public static Subject deepCopy(Subject subject) {
        return new Subject(false, new HashSet<>(subject.getPrincipals()), new HashSet<>(subject.getPublicCredentials()), new HashSet<>(subject.getPrivateCredentials()));
    }
}
