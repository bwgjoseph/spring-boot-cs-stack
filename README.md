# Spring Boot CS Stack

This project is for self-learning on understanding various stacks, and making it all work together.

- [ ] [Gradle](https://gradle.org/)
- [ ] [Spring Boot](https://spring.io/projects/spring-boot)
- [ ] [MyBatis](https://mybatis.org/mybatis-3/)
- [ ] [Oracle](https://www.oracle.com/sg/database/)
- [ ] [TestContainers](https://www.testcontainers.org/)
- [ ] [Debezium](https://debezium.io/)
- [ ] [Solr](https://solr.apache.org/)
- [ ] [ArangoDB](https://www.arangodb.com/)
- [ ] [LiquidBase](https://www.liquibase.org/)
- [ ] [JaVers](https://javers.org/)
- [ ] [Hashicorp Vault](https://www.vaultproject.io/)

Aside from learning those stacks, side goals are:

- to apply best practices on each of the tech (if applicable)
- follow [package-by-feature](https://phauer.com/2020/package-by-feature/) structure approach

## Oracle

See `docker-compose/README.md` on how to build oracle 19.3 docker image

Declare a datasource to connects to `Oracle` via `MyBatis`

```java
// application.properties
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
spring.datasource.url=jdbc:oracle:thin:@localhost:1521/ORCLPDB1
spring.datasource.username=cs_dev
spring.datasource.password=password1

// build.gradle
// different version supports different JDK
// see https://www.oracle.com/database/technologies/maven-central-guide.html
implementation 'com.oracle.database.jdbc:ojdbc10:19.11.0.0'
```

## MyBatis with Spring Boot

It can be configured with either XML or Java Annotation. It is recommended that for complex use-case, XML should be better/easier to manage as compared to Java Annotation.

```java
// application.properties
// see http://mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/
mybatis.*

// build.gradle
implementation 'org.mybatis.spring.boot:mybatis-spring-boot-starter:2.2.0'
```

### Configuring with Java Annotation

- Declare a `@Mapper` interface with the methods and query

### Configuring with XML

- Create `mybatis-config.xml` in `resources` directory
- Add `mybatis.config-location=classpath:mybatis-config.xml` in `application.properties`
- Create `?Mapper.xml` that points to the `Mapper` interface which declares the methods
  - Declare all SQL query and mapping
- Note that it is not required to declared `@Mapper` in the interface class but if we don't, then need to declare a `@MapperScan(basePackages = "com.bwgjoseph.springbootcsstack")` at the main class
  - If we use `@Mapper`, it will be auto picked up by Spring

### Using Interceptor Plugin

See [AuditInfoInterceptor](/src/main/java/com/bwgjoseph/springbootcsstack/mybatis/AuditInfoInterceptor.java) on how various means such as using `field-based` and `annotation-based` reflection to set the field value

The concept is similar to how [spring-data-auditing](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#auditing) works

See [interceptor-docs](https://mybatis.org/mybatis-3/configuration.html#plugins)

## Spring Security

Adding `spring-boot-starter-security` to the classpath will automatically enables `spring-security` default configuration which by default will generate a default user named `user` and password generated and displayed on startup. For details, look at [spring-security-docs](https://docs.spring.io/spring-security/site/docs/current/reference/html5/#servlet-hello-auto-configuration)

It is also possible to setup a `default user, password and roles` from `application.properties`

```json
spring.security.user.name=admin
spring.security.user.password=password
spring.security.user.roles=manager
```

In test, there's the option to use `@WithAnonymousUser, @WithMockUser, @WithUserDetails` to inject the (mock) user so that we can write test with different user with different authorties. If that is not sufficient, we can also write custom annotation through the use of `@WithSecurityContext` to inject a custom user

- Creates `AuthenticatedPrincipalContext` as a facade to grab the `AuthenticatedPrincipal`
- Creates `@TestConfiguration UserConfig` as a way to easier inject mock user into the test via `@WithUserDetails`
- Creates custom `UserDetailsService` with in-memory users, and initialize the default user using `spring.security.user` configuration
  - Allows to dynamically create new user as well which greatly benefit during test because it is now possible to add any user with any roles during the test which makes it so much more flexible. See [PostControllerTest](src/test/java/com/bwgjoseph/springbootcsstack/PostControllerTest.java)
  - This replaces the previous implementation of using `@TestConfiguration UserConfig`

Important note:

- If you implemented a custom `User` which either `extends User` or `implements UserDetails` then you cannot use `@WithMockUser` because that, by default, only refers to the default `User` object
- In this case, the only sensible choice is to implement custom `UserDetailsService` which overrides `loadUserByUsername` to return the custom `User` object
- And that way, the only choice is to use either `@WithUserDetails` or `@WithSecurityContext`

See [spring-security-guide](https://www.marcobehler.com/guides/spring-security)

## Spring Boot Actuator

To work on `Audit Events`, we have to bring in `spring-boot-actuator` dependency first as it provides out of the box `auditevents` endpoint

Add the following to `build.gradle`

```groovy
implementation 'org.springframework.boot:spring-boot-starter-actuator'
```

Run the application after adding the dependency, navigate to http://localhost:8080/actuator/health and you should see `{"status":"UP"}` and by default, only `health` endpoint is exposed

If we wish to expose all endpoint, simply add the following to `application.properties`

```json
management.endpoints.web.exposure.include=*
```

Note that for `/auditevents`, we have to configure `InMemoryAuditEventRepository` in order for it to work

```java
@Configuration
public class ActuatorConfig {
    @Bean
    public InMemoryAuditEventRepository repository(){
        return new InMemoryAuditEventRepository();
    }
}
```

Thereafter, we can publish custom event like such

```java
this.applicationEventPublisher.publishEvent(new AuditApplicationEvent(
    new AuditEvent(user.getUsername(), "USER_REQUEST_AUDIT_EVENT")
));
```

And the logs will look like this

```json
{
  "events": [
    {
      "timestamp": "2021-10-17T06:35:30.752503600Z",
      "principal": "admin",
      "type": "USER_REQUEST_AUDIT_EVENT"
    },
    {
      "timestamp": "2021-10-17T06:35:51.209505800Z",
      "principal": "admin",
      "type": "USER_REQUEST_AUDIT_EVENT"
    }
  ]
}
```

It is also possible to filter the events like such

```bash
curl 'http://localhost:8080/actuator/auditevents?principal=alice&after=2021-09-23T07%3A15%3A31.562Z&type=logout' -i -X GET
```

## Spring AOP

Added a custom annotation `@LogExecutionTime` where when annotated on a method, will trigger an aspect (`LogExecutionTimeAspect`) to log out the execution time of the annotated method