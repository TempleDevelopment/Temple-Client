package team.temple.enterprise.handler;

import team.temple.enterprise.filter.EventFilter;

public interface EventHandler extends Comparable<EventHandler> {
    <E> void handle(final E event);
    Object getListener();
    ListenerPriority getPriority();
    Iterable<EventFilter> getFilters();
}