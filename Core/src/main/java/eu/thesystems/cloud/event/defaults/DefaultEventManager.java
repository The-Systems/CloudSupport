package eu.thesystems.cloud.event.defaults;
/*
 * Created by derrop on 25.10.2019
 */

import eu.thesystems.cloud.CloudSupport;
import eu.thesystems.cloud.event.Cancellable;
import eu.thesystems.cloud.event.CloudEvent;
import eu.thesystems.cloud.event.EventHandler;
import eu.thesystems.cloud.event.EventManager;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultEventManager implements EventManager {
    private Map<Class<? extends CloudEvent>, List<ListenerMethod>> eventMethods = new HashMap<>();

    @Override
    public EventManager registerListeners(Object... listeners) {
        for (Object listener : listeners) {
            this.register(listener);
        }
        return this;
    }

    @Override
    public EventManager registerListener(Object listener) {
        this.register(listener);
        return this;
    }

    private void register(Object listener) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventHandler.class)) {
                Parameter[] parameters = method.getParameters();
                if (parameters.length != 1) {
                    throw new IllegalArgumentException("length of parameters in an EventHandler must be exactly 1");
                }
                Parameter parameter = parameters[0];
                if (CloudEvent.class.isAssignableFrom(parameter.getType())) {
                    EventHandler handler = method.getAnnotation(EventHandler.class);
                    byte priority = handler.priority();

                    method.setAccessible(true);
                    Class<? extends CloudEvent> eventClass = (Class<? extends CloudEvent>) parameter.getType();
                    if (!this.eventMethods.containsKey(eventClass))
                        this.eventMethods.put(eventClass, new ArrayList<>());
                    this.eventMethods.get(eventClass).add(new ListenerMethod(listener, method, priority));
                    this.eventMethods.get(eventClass).sort(Comparator.comparingInt(ListenerMethod::getPriority));
                }
            }
        }
    }

    @Override
    public void unregisterAll() {
        this.eventMethods.clear();
    }

    public void unregister(Object listener) {
        for (Collection<ListenerMethod> value : this.eventMethods.values()) {
            Collection<ListenerMethod> remove = new ArrayList<>();
            for (ListenerMethod listenerMethod : value) {
                if (listenerMethod.getListener().equals(listener)) {
                    remove.add(listenerMethod);
                }
            }
            value.removeAll(remove);
        }
    }

    @Override
    public void unregisterAll(ClassLoader classLoader) {
        for (Map.Entry<Class<? extends CloudEvent>, List<ListenerMethod>> entry : new HashMap<>(this.eventMethods).entrySet()) {
            Collection<ListenerMethod> value = entry.getValue();
            value.stream().filter(listenerMethod -> listenerMethod.getListener().getClass().getClassLoader().equals(classLoader)).collect(Collectors.toList()).forEach(value::remove);
            if (value.isEmpty()) {
                this.eventMethods.remove(entry.getKey());
            }
        }
    }

    public <T extends CloudEvent> T callEvent(T event) {
        CloudSupport.getInstance().debug("Calling event " + event.getClass().getName());
        boolean debug = CloudSupport.getInstance().isDebugging();
        StringBuilder builder = new StringBuilder();
        this.fireEvent(event, builder, debug, event.getClass());

        Class<?> superClass = event.getClass();
        while ((superClass = superClass != null ? superClass.getSuperclass() : event.getClass().getSuperclass()) != null) {
            this.fireEvent(event, builder, debug, (Class<? extends CloudEvent>) superClass);
        }
        if (debug) {
            if (event instanceof Cancellable) {
                CloudSupport.getInstance().debug("Called event " + event.getClass().getName() + " (cancelled: " + ((Cancellable) event).isCancelled() + ") to listeners: " +
                        (builder.length() <= 2 ? "no listener" : builder.substring(0, builder.length() - 1)));
            } else {
                CloudSupport.getInstance().debug("Called event " + event.getClass().getName() + " to listeners: " +
                        (builder.length() <= 2 ? "no listener" : builder.substring(0, builder.length() - 1)));
            }
        }
        return event;
    }

    private void fireEvent(CloudEvent event, StringBuilder builder, boolean debug, Class<? extends CloudEvent> eventClass) {
        if (!this.eventMethods.containsKey(eventClass)) {
            return;
        }
        this.eventMethods.get(eventClass).forEach(listenerMethod -> {
            long start = System.currentTimeMillis();
            listenerMethod.invoke(event);
            if (debug) {
                builder.append(listenerMethod.name()).append(", ");
                if (System.currentTimeMillis() - start > 2000) {
                    CloudSupport.getInstance().debug("Event " + listenerMethod.name() + " took " + (System.currentTimeMillis() - start) + " ms to process!");
                }
            }
        });
    }
}
