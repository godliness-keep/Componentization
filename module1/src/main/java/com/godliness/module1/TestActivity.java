package com.godliness.module1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by godliness on 2019-08-28.
 *
 * @author godliness
 */
public final class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!BuildConfig.isModule){
            // do something
        }
    }
}
