package com.example.demo.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Method;
import java.util.Arrays;


@Aspect
@Configuration
public class LoggingAspect {

    private Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * Point cut for createUser method in UserController
     */
    @Pointcut("execution(* com.example.demo.controllers.UserController.createUser(..))")    // match any return type in the createUser method with any argument
    private void forCreateUserMethod(){}

    /**
     * Point cut for submit method in the OrderController
     */
    @Pointcut("execution(* com.example.demo.controllers.OrderController.submit(..))")
    private void forCreateOrderMethod(){}

    /**
     * Point cut for all methods in controllers.
     */
    @Pointcut("execution(* com.example.demo.controllers.*.*(..))")
    private void forAllControllerMethods(){}

    /**
     * Point cut for all methods except the UserController:createUser and OrderController:submit
     */
    @Pointcut("forAllControllerMethods() && !(forCreateOrderMethod() || forCreateUserMethod())")
    private void forOtherControllerMethods(){}

    @Around("forCreateUserMethod() || forCreateOrderMethod()")
    public Object loggingCreationResultAndError(ProceedingJoinPoint joinPoint) throws Throwable {

        /**
         * Retrieve class name, method name and 1st argument
         */
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = signature.getDeclaringTypeName();
        String methodName = method.getName();
        String argument = joinPoint.getArgs()[0].toString();

        /**
         * Let's execute the method
         */
        ResponseEntity result = null;
        try {
            result = (ResponseEntity) joinPoint.proceed();
            // Logging when success execute method
            log.info("Info: Execution of {}.{} with argument ({}) and result {}", className, methodName, argument, result.getBody());
        }
        /**
         * When exception happens
         */
        catch (Exception ex) {
            // Logging when exception happens
            log.error("Exception: Execution of {}.{} with argument ({}) and exception {}", className, methodName, argument, ex.getMessage());
        }
        return result;
    }

    @AfterThrowing(
            pointcut = "forOtherControllerMethods()",
            throwing = "theExec"
    )
    public void loggingException(JoinPoint joinPoint, Throwable theExec) {
        /**
         * Retrieve class name, method name and arguments
         */
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = signature.getDeclaringTypeName();
        String methodName = method.getName();
        String argument = Arrays.stream(joinPoint.getArgs()).reduce(null, (partialResult, arg) -> partialResult == null ? arg.toString() : partialResult + ", " + arg, String::concat);

        log.error("Exception: Execution of {}.{} with argument ({}) and exception {}", className, methodName, argument, theExec.getMessage());
    }
}
