package team.stiff.pomelo.impl.annotated.handler;

import team.stiff.pomelo.filter.EventFilter;
import team.stiff.pomelo.handler.EventHandler;
import team.stiff.pomelo.handler.ListenerPriority;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.TempleClient;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * A basic implementation of the {@link EventHandler} used to
 * mark a method as an event listener via handle.
 *
 * @author Daniel
 * @since May 31, 2017
 */
public final class MethodEventHandler implements EventHandler {

    /**
     * The method object that has been marked as a listener.
     */
    private final MethodHandle methodHandle;

    /**
     * A filter predicate used to test the passed method against
     * all registered filters on the handler.
     */
    private final Set<EventFilter> eventFilters;

    /**
     * The annotation to the event listener.
     */
    private final Listener listenerAnnotation;

    public MethodEventHandler(final Object listenerParent, final Method method,
                              final Set<EventFilter> eventFilters) throws IllegalAccessException {
        this.methodHandle = TempleClient.LOOKUP.unreflect(method).bindTo(listenerParent).asType(MethodType.methodType(void.class, Object.class));
        this.eventFilters = eventFilters;
        this.listenerAnnotation = method.getAnnotation(Listener.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> void handle(final E event) {
        // iterate all event filters given to the handler
        for (final EventFilter filter : eventFilters)
            if (!filter.test(this, event))
                return;

        try {
            // invoke the listener with the current event
            methodHandle.invokeExact(event);
        } catch (final Throwable exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public Object getListener() {
        return methodHandle;
    }

    @Override
    public ListenerPriority getPriority() {
        return listenerAnnotation.priority();
    }

    @Override
    public Iterable<EventFilter> getFilters() {
        return eventFilters;
    }

    @Override
    public int compareTo(final EventHandler eventHandler) {
        return Integer.compare(eventHandler.getPriority().getPriorityLevel(),
                getPriority().getPriorityLevel());
    }
}
