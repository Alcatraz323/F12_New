package io.alcatraz.f12.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.alcatraz.f12.Constants;
import io.alcatraz.f12.Easter;
import io.alcatraz.f12.R;
import io.alcatraz.f12.adapters.AuthorAdapter;
import io.alcatraz.f12.adapters.QueryElementAdapter;
import io.alcatraz.f12.beans.QueryElement;
import io.alcatraz.f12.extended.CompatWithPipeActivity;
import io.alcatraz.f12.extended.DividerItemDecoration;
import io.alcatraz.f12.utils.PackageCtlUtils;
import io.alcatraz.f12.utils.Utils;

public class AboutActivity extends CompatWithPipeActivity {
    List<Integer> imgs = new ArrayList<Integer>();
    Map<Integer, List<String>> data = new HashMap<>();
    ListView lv;
    Toolbar tb;
    Easter easter;
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initData();
        initViews();
        easter = new Easter(this);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    public void showDetailDev() {
        AlertDialog g = new AlertDialog.Builder(this)
                .setTitle(R.string.au_l_2)
                .setMessage("Code:Alcatraz(GooglePlay)\n" +
                        "Main tester:Mr_Dennis")
                .setPositiveButton(R.string.ad_pb, null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initViews() {
        tb = findViewById(R.id.about_toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lv = findViewById(R.id.authorcontentListView1);
        AuthorAdapter aa = new AuthorAdapter(this, data, imgs);
        lv.setAdapter(aa);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i).toString().equals(getString(R.string.au_l_3))) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Alcatraz323/f12_full")));
                } else if (adapterView.getItemAtPosition(i).toString().equals(getString(R.string.au_l_4))) {
                    showOSPDialog();
                } else if (adapterView.getItemAtPosition(i).toString().equals(getString(R.string.au_l_2))) {
                    showDetailDev();
                } else {
                    if (vibrator.hasVibrator()) {
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                vibrator.cancel();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                                } else {
                                    vibrator.vibrate(100);
                                }
                            }
                        });

                    }
                    easter.shortClick();
                }
            }
        });


        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            vibrator.cancel();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                            } else {
                                vibrator.vibrate(200);
                            }
                        }
                    });
                }

                return true;
            }
        });
    }

    public void showOSPDialog() {
        @SuppressLint("InflateParams") View v = getLayoutInflater().inflate(R.layout.dialog_ops, null);
        new AlertDialog.Builder(this)
                .setTitle(R.string.au_osp)
                .setView(v)
                .setNegativeButton(R.string.ad_nb3, null).show();
        RecyclerView rv = v.findViewById(R.id.opRc1);
        List<QueryElement> dat = Constants.getOpenSourceProjects();
        QueryElementAdapter mra = new QueryElementAdapter(this, dat);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(mra);
        rv.addItemDecoration(new DividerItemDecoration(this, LinearLayout.HORIZONTAL,
                Utils.Dp2Px(this, 8), Color.parseColor("#eeeeee")));
    }


    public void initData() {
        imgs.add(R.drawable.ic_info_outline_black_24dp);
        imgs.add(R.drawable.ic_account_circle_black_24dp);
        imgs.add(R.drawable.ic_code_black_24dp);
        imgs.add(R.drawable.ic_open_in_new_black_24dp);
        List<String> l1 = new ArrayList<>();
        l1.add(getString(R.string.au_l_1));
        l1.add(PackageCtlUtils.getVersionName(this));
        List<String> l2 = new ArrayList<>();
        l2.add(getString(R.string.au_l_2));
        l2.add(getString(R.string.au_l_2_1));
        List<String> l3 = new ArrayList<>();
        l3.add(getString(R.string.au_l_3));
        l3.add("");
        List<String> l4 = new ArrayList<>();
        l4.add(getString(R.string.au_l_4));
        l4.add(getString(R.string.au_l_4_1));
        data.put(0, l1);
        data.put(1, l2);
        data.put(2, l3);
        data.put(3, l4);

    }
}