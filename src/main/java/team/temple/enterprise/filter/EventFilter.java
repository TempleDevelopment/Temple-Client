package team.temple.enterprise.filter;

import team.temple.enterprise.handler.EventHandler;

public interface EventFilter<E> {

    boolean test(EventHandler eventHandler, E event);
}