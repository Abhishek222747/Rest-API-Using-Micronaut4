```java
package com.abhishek.practical.logging;

import io.micronaut.aop.InterceptorBean;
import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Arrays;
import java.util.UUID;

@Singleton
@InterceptorBean(LogExecution.class)
public class LogExecutionInterceptor implements MethodInterceptor<Object,Object> {
    private static final Logger LOG= LoggerFactory.getLogger(LogExecutionInterceptor.class);
    private static final String REQUEST_ID = "requestId";

    @Override
    public Object intercept(MethodInvocationContext<Object, Object> context){
        String requestId = UUID.randomUUID().toString();
        MDC.put(REQUEST_ID, requestId); // Add request ID to MDC for contextual logging

        String methodName = context.getMethodName();
        Object[] arguments = context.getArguments();

        if (LOG.isInfoEnabled()) {
            LOG.info("Executing method: {} with arguments: {}", methodName, Arrays.toString(arguments));
        }

        try{
            Object result= context.proceed();
            if (LOG.isInfoEnabled()) {
                LOG.info("Method {} executed successfully, returned: {}", methodName, result);
            }
            return result;
        } catch(Exception e){
            LOG.error("Error in method: " + methodName, e);
            throw new LogExecutionException("Exception in method " + methodName, e); // Wrap exception
        } finally {
            MDC.remove(REQUEST_ID); // Clean up MDC
        }
    }

    // Custom exception to provide more context
    public static class LogExecutionException extends RuntimeException {
        public LogExecutionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
```