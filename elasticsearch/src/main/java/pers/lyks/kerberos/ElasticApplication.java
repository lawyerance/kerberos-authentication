package pers.lyks.kerberos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pers.lyks.kerberos.autoconfigure.elasticsearch.KerberosRestClientAutoConfiguration;

/**
 * @author lawyerance
 * @version 1.0 2019-11-22
 */
@SpringBootApplication(exclude = {KerberosRestClientAutoConfiguration.class})
public class ElasticApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticApplication.class, args);
    }
}
