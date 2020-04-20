package io.alcatraz.f12.activities;

import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.alcatraz.f12.R;
import io.alcatraz.f12.adapters.ChromeAdapter;
import io.alcatraz.f12.extended.CompatWithPipeActivity;
import io.alcatraz.f12.socat.Forward;

public class ChromeMgrActivity extends CompatWithPipeActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    List<Forward> data = new ArrayList<>();

    ChromeAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chrome_mgr);
        initViews();
        initData();
    }

    private void findViews() {
        toolbar = findViewById(R.id.chrome_manager_toolbar);
        recyclerView = findViewById(R.id.chrome_manager_recycler);
    }

    private void initViews() {
        findViews();
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new ChromeAdapter(this, data,false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fall_down);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.setAdapter(adapter);
    }

    public void initData() {
        data.clear();
        adapter.notifyDataSetChanged();
        data.addAll(getForwardingManager().getChromeList());
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
}
