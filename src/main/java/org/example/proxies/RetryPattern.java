package org.example.proxies;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class RetryPattern implements InvocationHandler {
    private Object obj;

    public static Object newInstance(Object obj) {
        return java.lang.reflect.Proxy.newProxyInstance(
                obj.getClass().getClassLoader(),
                obj.getClass().getInterfaces(),
                new RetryPattern(obj));
    }

    public RetryPattern(Object obj) {
        this.obj = obj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Simulate a circuit breaker pattern
        System.out.println("Invoking method: " + method.getName());
        try {
            return method.invoke(obj, args);
        }catch (InvocationTargetException e){
            // Simulate a retry breaker pattern
            System.out.println("retry activated: " + e.getCause().getClass());

        }
        return null;
    }
}
