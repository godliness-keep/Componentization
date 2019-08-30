package com.godliness.router.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by godliness on 2019-08-29.
 *
 * @author godliness
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.CLASS)
public @interface Extra {

    String name() default "";
}
