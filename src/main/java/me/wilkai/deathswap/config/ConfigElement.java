package me.wilkai.deathswap.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Contains extra information about an integer in the Config file, specifically a minimum and maximum value.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigElement {

    /**
     * The actual Name of this Element.
     */
    String name();

    /**
     * A description of what this Config Element is.
     */
    String summary();

    /**
     * The lowest value that this variable can have.
     * <b>Only works on Integers!</b>
     */
    int min() default -1;

    /**
     * The highest value that this variable can have.
     * <b>Only works on Integers!</b>
     */
    int max() default -1;
}
