package io.alcatraz.f12;

import android.app.Application;
import android.content.Context;

import java.util.concurrent.atomic.AtomicReference;

import io.alcatraz.f12.socat.ForwardingManager;

public class F12Application extends Application {
    private Context mContext;
    private AtomicReference<ForwardingManager> forwardingManager;

    //TODO : Check string.xml/Setup versionCode/build.gradle when release update
    //TODO : Set Empty View for all adapter views
    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        forwardingManager = new AtomicReference<>(new ForwardingManager(mContext));
        super.onCreate();
    }

    public AtomicReference<ForwardingManager> getForwardingManager(){
        return forwardingManager;
    }

    public Context getOverallContext() {
        return mContext;
    }

}
