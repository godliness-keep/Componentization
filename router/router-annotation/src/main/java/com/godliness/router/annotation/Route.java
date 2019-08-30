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
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Route {

    /**
     * 路由路径，标识一个路由节点
     */
    String path();

    /**
     * 将路由节点进行分组，可以实现按分组动态加载
     */
    String group() default "";
}
