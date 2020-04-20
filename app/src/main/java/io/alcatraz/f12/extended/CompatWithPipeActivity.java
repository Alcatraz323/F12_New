package io.alcatraz.f12.extended;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.alcatraz.f12.Constants;
import io.alcatraz.f12.F12Application;
import io.alcatraz.f12.socat.ForwardingManager;
import io.alcatraz.f12.utils.PermissionInterface;
import io.alcatraz.f12.utils.SharedPreferenceUtil;

@SuppressLint("Registered")
public class CompatWithPipeActivity extends AppCompatActivity {
    PermissionInterface pi;
    UpdatePreferenceReceiver updatePreferenceReceiver;

    int requestQueue = 0;

    public boolean doneFirstInitialize = false;

    //=========PREFERENCES==============

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (pi != null && requestCode == requestQueue - 1) {
            pi.onResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPrefernce();
        registReceivers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        doneFirstInitialize = true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissionWithCallback(PermissionInterface pi, String[] permissions, int requestCode) {
        this.pi = pi;
        requestPermissions(permissions, requestCode);
    }

    public void requestPermissionWithCallback(PermissionInterface pi, String[] permissions) {
        requestPermissionWithCallback(pi, permissions, requestQueue);
        requestQueue++;
    }

    public void onReloadPreferenceDone(){}

    public void loadPrefernce() {
        SharedPreferenceUtil spf = SharedPreferenceUtil.getInstance();
    }

    public void registReceivers() {
        IntentFilter ifil = new IntentFilter();
        ifil.addAction(Constants.BROADCAST_ACTION_UPDATE_PREFERENCES);
        updatePreferenceReceiver = new UpdatePreferenceReceiver();
        registerReceiver(updatePreferenceReceiver, ifil);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(updatePreferenceReceiver);
        super.onDestroy();
    }

    public void threadSleep(){
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void toast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    public void toast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    public ForwardingManager getForwardingManager(){
        F12Application application = (F12Application) getApplication();
        return application.getForwardingManager().get();
    }

    class UpdatePreferenceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            loadPrefernce();
            onReloadPreferenceDone();
        }
    }
}
