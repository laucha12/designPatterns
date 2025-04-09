package org.example.proxies;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CircuitBreakerProxy implements InvocationHandler {
    private Object obj;

    public static Object newInstance(Object obj) {
        return java.lang.reflect.Proxy.newProxyInstance(
                obj.getClass().getClassLoader(),
                obj.getClass().getInterfaces(),
                new CircuitBreakerProxy(obj));
    }

    public CircuitBreakerProxy(Object obj) {
        this.obj = obj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Simulate a circuit breaker pattern
        System.out.println("Invoking method: " + method.getName());
        try {
            return method.invoke(obj, args);
        }catch (InvocationTargetException e){
            // Simulate a circuit breaker pattern
            System.out.println("Circuit breaker activated: " + e.getCause().getClass());

        }
        return null;
    }
}
