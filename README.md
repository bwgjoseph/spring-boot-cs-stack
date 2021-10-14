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

## Spring Security

Adding `spring-boot-starter-security` to the classpath will automatically enables `spring-security` default configuration which by default will generate a default user named `user` and password generated and displayed on startup. For details, look at [spring-security-docs](https://docs.spring.io/spring-security/site/docs/current/reference/html5/#servlet-hello-auto-configuration)

It is also possible to setup a `default user, password and roles` from `application.properties`

```json
spring.security.user.name=admin
spring.security.user.password=password
spring.security.user.roles=admin
```

In test, there's the option to use `@WithAnonymousUser, @WithMockUser, @WithUserDetails` to inject the (mock) user so that we can write test with different user with different authorties. If that is not sufficient, we can also write custom annotation through the use of `@WithSecurityContext` to inject a custom user

- Creates `AuthenticatedPrincipalContext` as a facade to grab the `AuthenticatedPrincipal`
- Creates `@TestConfiguration UserConfig` as a way to easier inject mock user into the test via `@WithUserDetails`

See [spring-security-guide](https://www.marcobehler.com/guides/spring-security)