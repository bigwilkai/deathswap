package com.wilkei.deathswap.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Contains extra information about an integer in the Config file, specifically a minimum and maximum value.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigInt {

    /**
     * The lowest value that this variable can have.
     */
    int min() default 10;

    /**
     * The highest value that this variable can have.
     */
    int max() default 100;
}
