package io.alcatraz.f12.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.alcatraz.f12.F12Application;
import io.alcatraz.f12.R;
import io.alcatraz.f12.adapters.ChromeAdapter;
import io.alcatraz.f12.socat.Forward;
import io.alcatraz.f12.socat.ForwardingManager;
import io.alcatraz.f12.utils.Utils;

public class FloatWindowService extends Service implements View.OnClickListener {
    public static final String ACTION_UPDATE = "alc_f12_chrome_update";

    //Controllers
    WindowManager windowManager;
    LayoutInflater layoutInflater;
    WindowManager.LayoutParams layoutParams;
    WindowManager.LayoutParams layoutParams_toggle;
    ChromeUpdateReceiver chromeUpdateReceiver;

    //Widgets
    View panel_root;
    View toggle_root;
    CardView window_toggle;
    LinearLayout window_main_panel;

    //Top bar
    ImageButton window_close;
    ImageButton window_reload;
    ImageButton window_picker;
    ImageView window_icon;
    TextView window_title;

    FrameLayout webview_container;
    WebView window_webview;
    FrameLayout window_picker_root;
    RecyclerView window_picker_recycler;

    LinearLayout err_layer;

    LinearLayout window_load_layer;
    ProgressBar window_load_progress;

    Animation slide_in_animation;
    Animation slide_out_animation;

    List<Forward> data = new ArrayList<>();
    ChromeAdapter adapter;

    @Override
    public void onCreate() {
        super.onCreate();
        initialize();
        initializeWindow();
        showToggle();
        initData();
        registerReceiver();
    }

    private void initialize() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private void initializeWindow() {
        initViews();
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.gravity = Gravity.END | Gravity.TOP;
        layoutParams.x = 0;
        layoutParams.y = 0;

        layoutParams_toggle = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams_toggle.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams_toggle.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        layoutParams_toggle.format = PixelFormat.RGBA_8888;
        layoutParams_toggle.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        layoutParams_toggle.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams_toggle.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams_toggle.gravity = Gravity.END | Gravity.TOP;
        layoutParams_toggle.x = 0;
        layoutParams_toggle.y = Utils.Dp2Px(this, 128);
    }

    @SuppressLint("InflateParams")
    private void createAndBindViews() {
        panel_root = layoutInflater.inflate(R.layout.float_window, null);
        toggle_root = layoutInflater.inflate(R.layout.float_toggle, null);
        window_toggle = toggle_root.findViewById(R.id.float_toggle);
        window_main_panel = panel_root.findViewById(R.id.float_main_panel);
        window_close = panel_root.findViewById(R.id.float_close);
        window_reload = panel_root.findViewById(R.id.float_reload);
        window_picker = panel_root.findViewById(R.id.float_picker);
        window_icon = panel_root.findViewById(R.id.float_icon);
        window_title = panel_root.findViewById(R.id.float_title);
        err_layer = panel_root.findViewById(R.id.in_debugger_err_layer);
        window_webview = new WebView(this);
        webview_container = panel_root.findViewById(R.id.float_webview_container);
        webview_container.addView(window_webview);
        window_picker_root = panel_root.findViewById(R.id.float_page_picker_root);
        window_picker_recycler = panel_root.findViewById(R.id.float_picker_recycler);
        window_load_layer = panel_root.findViewById(R.id.float_load_layer);
        window_load_progress = panel_root.findViewById(R.id.float_load_progress);
        slide_in_animation = AnimationUtils.loadAnimation(this, R.anim.slide_right);
        slide_out_animation = AnimationUtils.loadAnimation(this, R.anim.slide_left);
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fall_down);
        window_picker_recycler.setLayoutAnimation(controller);
        window_picker_recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChromeAdapter(this, data, true);
        adapter.setIcon(window_icon);
        adapter.setLoad_layer(window_load_layer);
        adapter.setPicker_root(window_picker_root);
        adapter.setWindow_title(window_title);
        adapter.setWebView(window_webview);
        window_picker_recycler.setAdapter(adapter);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initViews() {
        createAndBindViews();
        window_toggle.setOnClickListener(this);
        window_close.setOnClickListener(this);
        window_reload.setOnClickListener(this);
        window_picker.setOnClickListener(this);
        window_toggle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                stopSelf();
                return true;
            }
        });
        WebChromeClient wcc = new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                window_load_progress.setProgress(newProgress);
                if (newProgress == 100) {
                    window_load_layer.setVisibility(View.GONE);
                    window_picker_root.setVisibility(View.GONE);
                }
            }
        };

        WebViewClient wvc = new WebViewClient(){
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                view.loadUrl("about:blank");
                err_layer.setVisibility(View.VISIBLE);
            }
        };

        WebView.setWebContentsDebuggingEnabled(false);

        window_webview.getSettings().setJavaScriptEnabled(true);
        window_webview.getSettings().setSupportZoom(true);
        window_webview.getSettings().setDisplayZoomControls(false);
        window_webview.getSettings().setBuiltInZoomControls(true);
        window_webview.setWebChromeClient(wcc);
        window_webview.setWebViewClient(wvc);
        window_webview.getSettings().setUseWideViewPort(true);
        window_webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        window_webview.getSettings().setDomStorageEnabled(true);
        window_webview.getSettings().setDatabaseEnabled(true);
        window_webview.getSettings().setAppCacheEnabled(true);
    }

    private void registerReceiver(){
        chromeUpdateReceiver = new ChromeUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_UPDATE);
        registerReceiver(chromeUpdateReceiver,intentFilter);
    }

    private void showPicker() {
        window_load_layer.setVisibility(View.GONE);
        window_picker_root.setVisibility(View.VISIBLE);
    }

    private void showToggle() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                windowManager.addView(toggle_root, layoutParams_toggle);
                window_toggle.startAnimation(slide_in_animation);
            }
        } else {
            windowManager.addView(toggle_root, layoutParams_toggle);
            window_toggle.startAnimation(slide_in_animation);
        }
    }

    private void showFloatWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                windowManager.addView(panel_root, layoutParams);
                window_toggle.startAnimation(slide_in_animation);
            }
        } else {
            windowManager.addView(panel_root, layoutParams);
            window_toggle.startAnimation(slide_in_animation);
        }
    }

    public ForwardingManager getForwardingManager() {
        F12Application application = (F12Application) getApplication();
        return application.getForwardingManager().get();
    }

    private synchronized void initData() {
        updateRecycler();
    }

    private void updateRecycler() {
        data.clear();
        data.addAll(getForwardingManager().getChromeList());
        adapter.notifyDataSetChanged();
        window_picker_recycler.scheduleLayoutAnimation();
    }

    @Override
    public void onDestroy() {
        windowManager.removeView(toggle_root);
        window_webview.removeAllViews();
        window_webview.destroy();
        window_webview = null;
        adapter.setWebView(null);
        unregisterReceiver(chromeUpdateReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.float_toggle:
                showFloatWindow();
                windowManager.removeView(toggle_root);
                break;
            case R.id.float_close:
                windowManager.removeView(panel_root);
                showToggle();
                break;
            case R.id.float_reload:
                window_webview.reload();
                window_load_layer.setVisibility(View.VISIBLE);
                window_picker_root.setVisibility(View.VISIBLE);
                break;
            case R.id.float_picker:
                showPicker();
                break;
        }
    }

    class ChromeUpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            initData();
        }
    }
}
