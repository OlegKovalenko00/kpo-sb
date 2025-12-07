package com.hsebank.di;


import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;


public final class Container {
    private final Map<Class<?>, Object> singletons = new HashMap<>();
    private final Map<Class<?>, Class<?>> registrations = new HashMap<>();


    public <T> void registerSingleton(Class<T> service, T instance) {
        singletons.put(service, instance);
    }


    public <T> void register(Class<T> service, Class<? extends T> impl) {
        registrations.put(service, impl);
    }


    @SuppressWarnings("unchecked")
    public <T> T resolve(Class<T> service) {
        if (singletons.containsKey(service)) {
            return (T) singletons.get(service);
        }
        Class<?> impl = registrations.getOrDefault(service, service);
        try {
            Constructor<?>[] ctors = impl.getConstructors();
            if (ctors.length == 0) {
                T instance = (T) impl.getDeclaredConstructor().newInstance();
                registerSingleton(service, instance);
                return instance;
            }
            Constructor<?> ctor = ctors[0];
            Class<?>[] params = ctor.getParameterTypes();
            Object[] args = new Object[params.length];
            for (int i = 0; i < params.length; i++) {
                args[i] = resolve(params[i]);
            }
            T instance = (T) ctor.newInstance(args);
            registerSingleton(service, instance);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}