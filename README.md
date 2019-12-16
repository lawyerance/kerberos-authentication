# Kerber-Authentication

基于JAAS进行kerberos认证

## 功能说明

主要对kerberos认证进行实现，由于是基于spring或springboot的集成方式实现

### Elasticsearch Rest client

- 无侵入式实现kerberos认证
- elasticsearch6客户端连接elasticsearch7服务端的兼容实现

### Elasticsearch Jest client

- 无侵入式实现kerberos认证

## 项目发布仓库

当前项目发布在本地仓库和${buildDir}下，需要发布至远程仓库的，可修改build.gradle配置

```bash
## 发布 RELEASE版本
./gradlew clean publish -Prelease

## 发布SNAPSHOT版本
./gradlew clean publish
```

## Rest-Client

使用方式可以参考[rest-client-sample](rest-client-sample)

下面对配置进行说明：
```properties
# 是否开启kerberos认证，缺省为true，当为false仅开启兼容模式（具体是否采用示客户端和服务端版本而定）
spring.elasticsearch.rest.kerberos.enabled=true
# Kerberos验证的用户名，未配置时取${spring.elasticsearch.rest.username}
spring.elasticsearch.rest.kerberos.username=username
# Kerberos验证的用户密码，未配置时取${spring.elasticsearch.rest.password}
spring.elasticsearch.rest.kerberos.password=password
# Kerberos验证使用的login-module名称，默认值'RestClient'
spring.elasticsearch.rest.kerberos.login-module=RestClient
```

## Jest-Client

使用方式可以参考[jest-client-sample](jest-client-sample)

下面对配置进行说明：
```properties
# 是否开启kerberos认证，缺省为true
spring.elasticsearch.jest.kerberos.enabled=true
# Kerberos验证的用户名，未配置时取${spring.elasticsearch.jest.username}
spring.elasticsearch.jest.kerberos.username=username
# Kerberos验证的用户密码，未配置时取${spring.elasticsearch.jest.password}
spring.elasticsearch.jest.kerberos.password=password
# Kerberos验证使用的login-module名称，默认值'JestClient'
spring.elasticsearch.jest.kerberos.login-module=JestClient
```
