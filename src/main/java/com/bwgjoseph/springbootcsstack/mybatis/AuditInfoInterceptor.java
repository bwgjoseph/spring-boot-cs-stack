package com.bwgjoseph.springbootcsstack.mybatis;

import java.lang.reflect.Field;

import com.bwgjoseph.springbootcsstack.context.AuthenticatedPrincipalContext;
import com.bwgjoseph.springbootcsstack.services.post.CreatedBy;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;


// See https://stackoverflow.com/questions/7162528/how-automatically-update-fields-like-created-modified-date-when-using-mybatis
// See https://mybatis.org/mybatis-3/configuration.html#plugins
@Intercepts(
    // @Signature(
    //     type = Executor.class,
    //     method = "query",
    //     args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
    // )
    @Signature(
        type = Executor.class,
        method = "update",
        args = {MappedStatement.class, Object.class}
    )
)
@Component
@Slf4j
public class AuditInfoInterceptor implements Interceptor {
    private final AuthenticatedPrincipalContext authenticatedPrincipalContext;

    public AuditInfoInterceptor(AuthenticatedPrincipalContext authenticatedPrincipalContext) {
        this.authenticatedPrincipalContext = authenticatedPrincipalContext;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        // getArgs()[?] is based on the args defined in `@Signature`
        Object thisObject = invocation.getArgs()[1];

        /**
         * There are a couple of ways that we can get the field, and set the field value manually
         *
         * 1. via getClass().getDeclaredField()
         * 2. via getClass().getSuperclass().getDeclaredField()
         * 3. via isAnnotationPresent
         * 4. via SystemMetaObject
         */

        /**
         * via getClass().getDeclaredField() allows to get the field declared in the class
         */
        Field fieldBody = thisObject.getClass().getDeclaredField("body");
        fieldBody.setAccessible(true);
        log.info("Value of current body is: {}", fieldBody.get(thisObject));
        fieldBody.set(thisObject, "overwritten-body");

        /**
         * via getClass().getSuperclass().getDeclaredField() allows to get the field declared in the parent class
         */
        Field fieldUpdatedBy = thisObject.getClass().getSuperclass().getDeclaredField("updatedBy");
        fieldUpdatedBy.setAccessible(true);
        log.info("Value of current updatedBy is: {}", fieldUpdatedBy.get(thisObject));
        fieldUpdatedBy.set(thisObject, this.authenticatedPrincipalContext.getCurrentUser().getUsername());

        /**
         * via isAnnotationPresent allows to check if the field was annotated with a specific annotation
         * Benefit of this is that it doesn't rely on actual field name
         */
        Field[] allFields = thisObject.getClass().getSuperclass().getDeclaredFields();

        for (Field f : allFields) {
            f.setAccessible(true);
            if (f.isAnnotationPresent(CreatedBy.class)) {
                log.info("@CreatedBy annotation is present: {}", f.isAnnotationPresent(CreatedBy.class));
                log.info("Value of current createdBy is: {}", f.get(thisObject));
                f.set(thisObject, this.authenticatedPrincipalContext.getCurrentUser().getUsername());
            }
        }

        /**
         * via SystemMetaObject that provides easier way to set the field which is provided by myBatis
         * It is not required to know before hand whether the field comes from the class or superclass
         */
        MetaObject metaObject = SystemMetaObject.forObject(thisObject);
        metaObject.setValue("title", "overwritten-title");

        return invocation.proceed();
    }

}
