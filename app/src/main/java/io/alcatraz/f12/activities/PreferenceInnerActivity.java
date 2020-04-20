package io.alcatraz.f12.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import io.alcatraz.f12.R;
import io.alcatraz.f12.beans.PreferenceHeader;
import io.alcatraz.f12.extended.CompatWithPipeActivity;

public class PreferenceInnerActivity extends CompatWithPipeActivity {
    public static final String PREFERENCE_TRANSFER_HEADER = "PREFERENCE_HEADER";
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_inner);
        toolbar = findViewById(R.id.preference_act_toolbar);

        Intent intent = getIntent();
        PreferenceHeader header = intent.getParcelableExtra(PREFERENCE_TRANSFER_HEADER);
        toolbar.setTitle(header.getTitle());
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        switch (header.getIcon_res()){

        }
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
}
