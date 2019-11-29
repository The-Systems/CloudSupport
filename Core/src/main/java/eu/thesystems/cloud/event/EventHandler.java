package eu.thesystems.cloud.event;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {
    /**
     * the priority of this EventHandler (lowest is called first)
     *
     * @return the priority
     */
    byte priority() default EventPriority.NORMAL;
}
