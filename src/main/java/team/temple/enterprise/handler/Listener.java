package team.temple.enterprise.handler;

import team.temple.enterprise.filter.EventFilter;
import team.temple.enterprise.handler.ListenerPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Listener {

    Class<? extends EventFilter>[] filters() default { };

    ListenerPriority priority() default ListenerPriority.NORMAL;
}