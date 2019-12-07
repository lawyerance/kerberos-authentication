//
// Built on Thu Dec 05 13:51:06 CET 2019 by logback-translator
// For more information on configuration files in Groovy
// please see http://logback.qos.ch/manual/groovy.html

// For assistance related to this tool or configuration files
// in general, please contact the logback user mailing list at
//    http://qos.ch/mailman/listinfo/logback-user

// For professional support please see
//   http://www.qos.ch/shop/products/professionalSupport

import ch.qos.logback.classic.encoder.PatternLayoutEncoder

import static ch.qos.logback.classic.Level.INFO

def LOG_DIR = Optional.ofNullable(System.getProperty("app.logs.dir")).orElse(System.getProperty("user.dir") + "/logs")
def STDOUT_PATTERN = "%yellow(%date{yyyy-MM-dd HH:mm:ss}) %highlight(%-5level) %magenta([%thread]) %green(%logger{50})\$%blue(%file:%line) - %cyan(%msg%n)";


scan("30 seconds")
appender("STDOUT", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = STDOUT_PATTERN
    }
}
appender("FILE", FileAppender) {
    file = "${LOG_DIR}/app.log"
    encoder(PatternLayoutEncoder) {
        pattern = "%msg%n"
    }
}

root(DEBUG, ["STDOUT"])
root(INFO, ["STDOUT", "FILE"])