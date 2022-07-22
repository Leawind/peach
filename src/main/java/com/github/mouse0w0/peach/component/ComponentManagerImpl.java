package com.github.mouse0w0.peach.component;

import com.github.mouse0w0.peach.component.store.ComponentStore;
import com.github.mouse0w0.peach.dispose.Disposable;
import com.github.mouse0w0.peach.dispose.Disposer;
import com.github.mouse0w0.peach.util.ArrayUtils;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class ComponentManagerImpl implements ComponentManager {
    private final ComponentManager parent;

    private final Map<Class<?>, Object> services = new HashMap<>();
    private final Map<Class<?>, Supplier<?>> serviceFactories = new HashMap<>();

    private ComponentStore componentStore;

    public ComponentManagerImpl(ComponentManager parent) {
        this.parent = parent;
    }

    protected ComponentStore getComponentStore() {
        if (componentStore == null) {
            componentStore = getService(ComponentStore.class);
        }
        return componentStore;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> classOfT, boolean createIfNeeded) {
        T service = (T) services.get(classOfT);
        if (createIfNeeded && service == null) {
            Supplier<?> serviceFactory = serviceFactories.get(classOfT);
            service = serviceFactory != null ? (T) serviceFactory.get() : null;
            registerService(classOfT, service);
        }
        return service;
    }

    protected void initServices(List<ServiceDescriptor> services) {
        for (ServiceDescriptor service : services) {
            registerService(service);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void registerService(ServiceDescriptor service) {
        Class<?> implementationClass;
        try {
            implementationClass = Class.forName(service.implementationName);
        } catch (ClassNotFoundException e) {
            throw new ServiceException("Not found service implementation class " + service.implementationName, e);
        }
        Class serviceClass;
        try {
            serviceClass = service.interfaceName != null ? Class.forName(service.interfaceName) : implementationClass;
        } catch (ClassNotFoundException e) {
            throw new ServiceException("Not found service interface class " + service.implementationName, e);
        }

        if (service.lazy) {
            registerServiceFactory(serviceClass, () -> newInstance(implementationClass));
        } else {
            registerService(serviceClass, newInstance(implementationClass));
        }
    }

    protected <T> void registerService(Class<T> classOfT, T service) {
        if (service instanceof PersistentStateComponent) {
            getComponentStore().loadComponent((PersistentStateComponent) service);
        }

        services.putIfAbsent(classOfT, service);
    }

    protected <T> void registerServiceFactory(Class<T> classOfT, Supplier<T> serviceFactory) {
        serviceFactories.putIfAbsent(classOfT, serviceFactory);
    }

    @SuppressWarnings("unchecked")
    private <T> T newInstance(Class<?> implementationClass) {
        try {
            Constructor<?>[] constructors = implementationClass.getConstructors();
            if (constructors.length != 1) {
                throw new ServiceException("Too many constructors of service");
            }
            Constructor<?> constructor = constructors[0];
            Object[] instances = ArrayUtils.map(constructor.getParameterTypes(), this::resolveInstance, Object[]::new);
            return (T) constructor.newInstance(instances);
        } catch (ReflectiveOperationException e) {
            throw new ServiceException("Failed to create the service instance of " + implementationClass.getName(), e);
        }
    }

    private Object resolveInstance(Class<?> type) {
        if (type.isAssignableFrom(getClass())) {
            return this;
        }

        Object service = getService(type);
        if (service != null) {
            return service;
        }

        if (parent != null) {
            return parent.getService(type);
        }

        return null;
    }

    protected void saveComponents() {
        ComponentStore componentStore = getComponentStore();
        for (Object value : services.values()) {
            if (value instanceof PersistentStateComponent) {
                componentStore.saveComponent((PersistentStateComponent) value);
            }
        }
    }

    protected void disposeComponents() {
        for (Object service : services.values()) {
            if (service instanceof Disposable) {
                Disposer.dispose((Disposable) service);
            }
        }
    }
}
