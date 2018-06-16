package com.giphytest.component;

import android.app.Application;
import android.content.Context;

import com.giphytest.App;
import com.giphytest.module.AppModule;
import com.giphytest.ui.activity.MainActivity;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(final App app);

    void inject(final MainActivity mainActivity);

    Application application();

    Context context();
}
