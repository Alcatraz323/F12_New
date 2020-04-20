package io.alcatraz.f12.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import io.alcatraz.f12.R;
import io.alcatraz.f12.extended.CompatWithPipeActivity;
import io.alcatraz.f12.services.FloatWindowService;
import io.alcatraz.f12.socat.ForwardingManager;
import io.alcatraz.f12.utils.PackageCtlUtils;
import io.alcatraz.f12.utils.ShellUtils;
import io.alcatraz.f12.utils.Utils;

public class MainActivity extends CompatWithPipeActivity implements View.OnClickListener {
    Toolbar toolbar;
    CardView status;
    ImageView status_icon;
    TextView status_indicator;
    LinearLayout status_overlay;

    CardView chrome_mgr;
    TextView chrome_mgr_indicator;

    CardView service_control;
    TextView service_control_indicator;

    CardView settings;
    CardView help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        refresh();
    }

    private void findViews() {
        toolbar = findViewById(R.id.main_toolbar);
        status = findViewById(R.id.main_card_status);
        status_indicator = findViewById(R.id.main_card_status_indicator);
        status_icon = findViewById(R.id.main_card_status_indicator_image);
        status_overlay = findViewById(R.id.main_card_status_overlay);
        chrome_mgr = findViewById(R.id.main_card_chrome_mgr);
        chrome_mgr_indicator = findViewById(R.id.main_card_chrome_mgr_indicator);
        service_control = findViewById(R.id.main_card_service_control);
        service_control_indicator = findViewById(R.id.main_card_service_control_indicator);
        settings = findViewById(R.id.main_card_setting);
        help = findViewById(R.id.main_card_help);
    }

    private void initViews() {
        findViews();
        setSupportActionBar(toolbar);

        status.setOnClickListener(this);
        chrome_mgr.setOnClickListener(this);
        service_control.setOnClickListener(this);
        settings.setOnClickListener(this);
        help.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, R.string.toast_cant_overlay, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())));
            }
        }
        @SuppressLint("InflateParams") View tutorial = getLayoutInflater().inflate(R.layout.dialog_tutorial,null);
        new AlertDialog.Builder(this)
                .setTitle(R.string.tutorial_title)
                .setView(tutorial)
                .setNegativeButton(R.string.ad_pb,null).show();
    }

    private synchronized void refresh() {
        if (ShellUtils.hasRootPermission()) {
            status_indicator.setText(R.string.main_card_status_root_true);
            status_icon.setImageResource(R.drawable.ic_check_circle_black_24dp);
            status_overlay.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        } else {
            status_indicator.setText(R.string.main_card_status_root_false);
            status_icon.setImageResource(R.drawable.ic_close);
            status_overlay.setBackgroundColor(getResources().getColor(R.color.base_gray_tint));
        }
        service_control_indicator.setText(PackageCtlUtils.isServiceRunning(this, "io.alcatraz.f12.services.FloatWindowService") ?
                R.string.main_card_float_window_alive : R.string.main_card_float_window_dead);
        List<View> dialog_holder = new ArrayList<>();
        final AlertDialog processing_dialog = Utils.getProcessingDialog(this, dialog_holder, false, true);
        final TextView textView = (TextView) dialog_holder.get(0);
        final ProgressBar progressBar = (ProgressBar) dialog_holder.get(1);
        getForwardingManager().setActivity(this);
        processing_dialog.show();
        getForwardingManager().scanAbsBrowsers(new ForwardingManager.ScanInterface() {
            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void callProgress(final int current, final int total) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(String.format("Processing=(%d/%d)", current, total));
                        progressBar.setProgress(current / total * 100);
                    }
                });
            }

            @Override
            public void onDone() {
                processing_dialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chrome_mgr_indicator.setText(String.format(getString(R.string.main_card_chrome_manager_indicator),
                                getForwardingManager().getForwardingCount()));
                    }
                });
                if (PackageCtlUtils.isServiceRunning(MainActivity.this, "io.alcatraz.f12.services.FloatWindowService")) {
                    ComponentName componentName = new ComponentName(getPackageName(), "io.alcatraz.f12.services.FloatWindowService");
                    Intent intent = new Intent();
                    intent.setAction(FloatWindowService.ACTION_UPDATE);
                    intent.setComponent(componentName);
                    sendBroadcast(intent);
                }
            }
        }, true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (doneFirstInitialize) {
            refresh();
        }
    }

    @Override
    protected void onDestroy() {
        getForwardingManager().setActivity(null);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.activity_main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            case R.id.menu_main_log:
                startActivity(new Intent(MainActivity.this, LogActivity.class));
                break;
            case R.id.menu_main_refresh:
                refresh();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_card_status:

                break;
            case R.id.main_card_chrome_mgr:
                startActivity(new Intent(MainActivity.this, ChromeMgrActivity.class));
                break;
            case R.id.main_card_setting:
                startActivity(new Intent(MainActivity.this, PreferenceActivity.class));
                break;
            case R.id.main_card_help:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Alcatraz323/widgetassistant")));
                break;
            case R.id.main_card_service_control:
                stopService(new Intent(MainActivity.this, FloatWindowService.class));
                startService(new Intent(MainActivity.this, FloatWindowService.class));
                break;
        }
    }
}
