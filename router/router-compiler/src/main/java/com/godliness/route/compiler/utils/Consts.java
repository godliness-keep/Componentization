package com.godliness.route.compiler.utils;


/**
 * Created by godliness on 2019-08-29.
 *
 * @author godliness
 */
public final class Consts {

    public static final String ARGUMENTS_NAME = "moduleName";
    public static final String ANN_TYPE_ROUTE = "com.godliness.router.annotation.Route";
    public static final String ANN_TYPE_EXTRA = "com.godliness.router.annotation.Extra";

    public static final String ACTIVITY = "android.app.Activity";
    public static final String ISERVICE = "com.dongnao.router.core.template.IService";

    public static final String IROUTE_GROUP = "com.godliness.router.core.template.IRouteGroup";
    public static final String IROUTE_ROOT = "com.godliness.router.core.template.IRouteRoot";
    public static final String IEXTRA = "com.godliness.router.core.template.IExtra";

    public static final String METHOD_LOAD_INTO = "loadInto";
    public static final String METHOD_LOAD_EXTRA = "loadExtra";

    public static final String SEPARATOR = "$$";
    public static final String PROJECT = "Router";
    public static final String NAME_OF_ROOT = PROJECT + SEPARATOR + "Root" + SEPARATOR;
    public static final String NAME_OF_GROUP = PROJECT + SEPARATOR + "Group" + SEPARATOR;

    public static final String PACKAGE_OF_GENERATE_FILE = "com.godliness.router.routes";

}
