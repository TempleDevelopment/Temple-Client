package team.stiff.pomelo.impl.annotated.handler.scan;

import team.stiff.pomelo.filter.EventFilter;
import team.stiff.pomelo.filter.EventFilterScanner;
import team.stiff.pomelo.handler.EventHandler;
import team.stiff.pomelo.handler.scan.EventHandlerScanner;
import team.stiff.pomelo.impl.annotated.filter.MethodFilterScanner;
import team.stiff.pomelo.impl.annotated.handler.MethodEventHandler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

/**
 * An implementation of {@link EventHandlerScanner} that locates
 * all methods of a class that are event listeners.
 *
 * @author Daniel
 * @since May 31, 2017
 */
public final class MethodHandlerScanner implements EventHandlerScanner {
    private final AnnotatedListenerPredicate annotatedListenerPredicate =
            new AnnotatedListenerPredicate();
    private final EventFilterScanner<Method> filterScanner = new MethodFilterScanner();

    @Override
    public Map<Class<?>, Set<EventHandler>> locate(final Object listenerContainer) {
        final Map<Class<?>, Set<EventHandler>> eventHandlers = new HashMap<>();
        Stream.of(listenerContainer.getClass().getDeclaredMethods())
                .filter(annotatedListenerPredicate).forEach(method -> eventHandlers
                        .computeIfAbsent(method.getParameterTypes()[0], obj -> new TreeSet<>())
                        .add(create(listenerContainer, method, filterScanner.scan(method))));
        return eventHandlers;
    }

    private EventHandler create(Object owner, Method method, Set<EventFilter> filters) {
        try {
            return new MethodEventHandler(owner, method, filters);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not create event handlers", e);
        }
    }
}
