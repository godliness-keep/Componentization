package com.godliness.common;

import com.godliness.common.utils.AppContext;

/**
 * Created by godliness on 2019-08-28.
 *
 * @author godliness
 */
public final class UrlConstant {

    /**
     * 根据打包类型自动配置release/debug
     */
    public static final String BASE_URL = AppContext.get().getString(R.string.projectUrl);
}
