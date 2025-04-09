package org.example.proxies;


import org.example.interfaces.CheckedFunction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalTime;
import java.util.function.Supplier;

public class CircuitBreakerProxy implements InvocationHandler {
    private Object obj;
    private CircuitBreaker circuitBreaker = new CircuitBreaker();
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
        Object toReturn =  circuitBreaker.invokeFunction(() -> method.invoke(obj, args));
        circuitBreaker.nextState();
        return toReturn;
    }

}

