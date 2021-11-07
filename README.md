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

## PoC on patch endpoint

To explore if it is possible to perform a patch with partial data, and update database using dynamic SQL query

TLDR; it's possible but need to test on more edge case. For example, default value set on the pojo may accidentally override the data unknowingly

### Possible solution

#### Using json-patch / json-merge-patch

See [using-http-patch-in-spring](https://cassiomolin.com/2019/06/10/using-http-patch-in-spring/) for detailed explanation

It seem a little more complicated than I need right, so while this seem to be a good way to tackle the problem, there is a slightly easier way

Note that this solution also requires to get the original data upfront before applying the patch

#### Using jackson + dynamic SQL (batis)

The idea is straight-forward, and easy to implement but have not tested for edge case, and more complicated use-case yet. There is no requirement to pull the data upfront before patch. Have yet to test with updating of different data-type column (string, int, date, etc)

> See [stackoverflow](https://stackoverflow.com/questions/69778451/mybatis-passing-in-the-datatype-on-dynamic-update-query) on issue with different datatype

- Create a `patch` endpoint
  - See `PostController.patchById`
- Converts the incoming request pojo into `Map<String, Object>` using `jackson objectMapper`
- Set `objectMapper` to ignore all `null` values
  - With that, this would not work if the value really needs to be set as `null`
- Use `mybatis dynamic SQL` to construct the `update` statement
  - See `PostUpdateMapper`
  - ~~Note that, this method does not seem to work. Not sure if it's batis bug. (see [mybatis-3#2369](https://github.com/mybatis/mybatis-3/issues/2369))~~
  - To use dynamic SQL with dynamic column (or table), the correct syntax to use is `${}` instead of `#{}`
  - The alternative working solution is to use `@UpdateProvider` that construct the query using [sql-builder](https://mybatis.org/mybatis-3/statement-builders.html)

#### Using MyBatis Dynamic SQL library

This solution rely on [mybatis-dynamic-sql](https://mybatis.org/mybatis-dynamic-sql/docs/introduction.html) library and it work very well even on the different datatype which is a plus. The only down side is to create some boilerplate code.

- Create a class that maps to the actual table with the datatype defined. See `PostDynamicSqlSupport`
- Create the neccessary methods in Mapper class
  - There are two way about this
    - Use `UpdateStatementProvider`
      - Construct `UpdateStatementProvider` query directly in the service class and call the `mapper.update` method (see `CommonUpdateMapper`)
    - Use `UpdateDSL`
      - Define `update` and `updateSelectiveColumns` method in Mapper class
      - Service class to call `update` method

Overall, it seem like the last solution is the most elegant and not having to worry about the data-type issue and is type-safe

### Running `MyBatis` with `batch` mode

When inserting or updating multiple records, it is not efficient to do so by creating a huge insert/update statement (see [batch-insert.update](#batch-insertupdate)). So to do that, one have to use the `sqlSession` with `BATCH` mode (see [mybatis-faq](https://github.com/mybatis/mybatis-3/wiki/FAQ#how-do-i-code-a-batch-insert))

- Create `MyBatisConfig` to initialize the default `SqlSession` and the batch `SqlSession`
- Inject the `batch SqlSession` to get the mapper class and trigger the method manually
  - It also also possible to assign the mapper class to use the `batch SqlSession` by default using `MapperScan`, see [here](https://github.com/jeffgbutler/mybatis-cockroach-demo/blob/e4255a659d/src/main/java/com/example/cockroachdemo/MyBatisConfiguration.java#L27-L28)
  ```java
  @MapperScan(basePackages = "com.example.cockroachdemo.batchmapper", annotationClass = Mapper.class,
            sqlSessionTemplateRef = "batchSqlSessionTemplate")
  ```
- Create `BatchResults` to store the final rows affected

Take note that when using `lombok` with `@AllArgsConstructor` and with `@Qualifier` annotation, `lombok` will ignore the `@Qualifier` annotation, hence, the injected `SqlSession` will be the `non-batch`. See [spring-mybatis-how-to-determine-if-using-batch-mode-correctly](https://stackoverflow.com/questions/69787861/spring-mybatis-how-to-determine-if-using-batch-mode-correctly)

So the solution to that is to add `lombok.config` to the root directory, and add `lombok.copyableAnnotations += org.springframework.beans.factory.annotation.Qualifier`

References:

- [mybatis-cockroach-demo](https://github.com/jeffgbutler/mybatis-cockroach-demo)
- [so-60993091](https://github.com/harawata/mybatis-issues/tree/master/so-60993091)

#### Batch Insert/Update

Should always use a single insert/update statement and loop it via `Java` and use `BATCH` executor. See [here](https://groups.google.com/g/mybatis-user/c/8QxLH7XuYlU/m/mLT5WT_TokoJ)

References:

- [mybatis-batch-insert-update-for-oracl](https://stackoverflow.com/questions/23486547/mybatis-batch-insert-update-for-oracle/29264696#29264696)
- [using-mybatis-3-4-6-for-oracle-batch-update-and-got-the-1-result](https://stackoverflow.com/questions/58909833/using-mybatis-3-4-6-for-oracle-batch-update-and-got-the-1-result/58914577#58914577)
- [spring-mybatis-how-to-determine-if-using-batch-mode-correctly](https://stackoverflow.com/questions/69787861/spring-mybatis-how-to-determine-if-using-batch-mode-correctly)
- [mybatis-batch-update-insert-delete](https://pretius.com/blog/mybatis-batch-update-insert-delete/)
- [java-persistence-frameworks-comparison](https://github.com/bwajtr/java-persistence-frameworks-comparison)

## Spring Test

### TestRestTemplate

Using `TestRestTemplate` to call `patchForObject` will encounter `I/O` exception as it does not support `patch` method by default. See this [stackoverflow-post](https://stackoverflow.com/questions/41557069/how-do-i-implement-a-patch-executed-via-resttemplate) explanation for further details

There two 2 ways to resolve this issue

Bring in the dependency `testImplementation 'org.apache.httpcomponents:httpclient:4.5.13'` first

1. Manually define `requestFactory`

During the test, either within the `BeforeAll, BeforeEach` or within each individual `test`, do

```java
this.testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
```

2. Initialize `RestTemplate` with the appropriate `requestFactory`

Override the `RestTemplate Bean` configuration

```java
@TestConfiguration
static class Config {
    @Bean
    public RestTemplate httpComponentsClientRestTemplate() {
        final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(3000);
        requestFactory.setReadTimeout(3000);
        return new RestTemplate(requestFactory);
    }
}
```

This way, there is no need to manually (and remember to) set anything, and is the better approach of the two