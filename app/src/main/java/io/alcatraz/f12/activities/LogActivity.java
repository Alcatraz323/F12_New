package io.alcatraz.f12.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import io.alcatraz.f12.LogBuff;
import io.alcatraz.f12.R;
import io.alcatraz.f12.extended.CompatWithPipeActivity;

public class LogActivity extends CompatWithPipeActivity {
    Toolbar toolbar;
    TextView console_log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        initViews();
        initData();
    }

    private void findViews() {
        toolbar = findViewById(R.id.log_toolbar);
        console_log = findViewById(R.id.log_console_box);
    }

    private void initViews(){
        findViews();
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void initData(){
        console_log.setText(LogBuff.getFinalLog());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = new MenuInflater(this);
        mi.inflate(R.menu.activity_log_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_log_refresh:
                initData();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
