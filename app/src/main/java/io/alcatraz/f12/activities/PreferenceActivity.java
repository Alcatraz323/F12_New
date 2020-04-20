package io.alcatraz.f12.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import io.alcatraz.f12.Constants;
import io.alcatraz.f12.R;
import io.alcatraz.f12.adapters.PreferenceListAdapter;
import io.alcatraz.f12.beans.PreferenceHeader;
import io.alcatraz.f12.extended.CompatWithPipeActivity;

public class PreferenceActivity extends CompatWithPipeActivity {
    Toolbar toolbar;
    ListView listView;

    List<PreferenceHeader> headers;
    PreferenceListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        prepareHeader();
        initViews();
    }

    public void initViews() {
        toolbar = findViewById(R.id.preference_act_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = findViewById(R.id.preference_act_recycler);
        adapter = new PreferenceListAdapter(this, headers);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(PreferenceActivity.this, PreferenceInnerActivity.class);
                intent.putExtra(PreferenceInnerActivity.PREFERENCE_TRANSFER_HEADER, (PreferenceHeader) adapterView.getItemAtPosition(i));
                PreferenceActivity.this.startActivity(intent);
            }
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sendBroadcast(new Intent().setAction(Constants.BROADCAST_ACTION_UPDATE_PREFERENCES));
    }

    private void prepareHeader() {
        headers = new ArrayList<>();

    }
}
