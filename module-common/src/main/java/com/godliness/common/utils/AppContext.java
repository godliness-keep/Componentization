package com.godliness.common.utils;

import android.app.Application;
import android.content.Context;

/**
 * Created by godliness on 2019-08-28.
 *
 * @author godliness
 */
public final class AppContext {

    static Application sContext;

    public static void register(Application context) {
        sContext = context;
    }

    public static Context get() {
        return sContext;
    }
}
