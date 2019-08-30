package com.godliness.route.compiler.utils;

import java.util.Collection;
import java.util.Map;

/**
 * Created by godliness on 2019-08-29.
 *
 * @author godliness
 */
public final class CheckEmpty {

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() <= 0;
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }
}
