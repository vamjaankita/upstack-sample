package com.giphytest;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

import com.giphytest.bean.MyObjectBox;
import com.giphytest.component.AppComponent;
import com.giphytest.component.DaggerAppComponent;
import com.giphytest.module.AppModule;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import io.objectbox.BoxStore;


public class App extends Application {

    private AppComponent appComponent;
    public static RefWatcher refWatcher;
    private BoxStore boxStore;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        appComponent.inject(this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        refWatcher = LeakCanary.install(this);
        boxStore = MyObjectBox.builder().androidContext(App.this).build();
    }

    public static App get(final Context context) {
        return (App) context.getApplicationContext();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public BoxStore getBoxStore() {
        return boxStore;
    }
}
