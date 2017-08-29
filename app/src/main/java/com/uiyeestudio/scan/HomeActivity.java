package com.uiyeestudio.scan;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.uiyeestudio.scan.R;

public class HomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.activity_home_linearLayout);
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.list_item, null);
        linearLayout.addView(view);

    }


    private void toActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_continuous:
                this.toActivity(ContinuousScanActivity.class);
                break;
            case R.id.menu_item_main:
                this.toActivity(MainScanActivity.class);
                break;
            case R.id.menu_item_list:
                this.toActivity(ListActivity.class);
                break;
            case R.id.menu_item_handlerTest:
                this.toActivity(HandlerTestActivity.class);
                break;
            case R.id.menu_item_custom:
                this.toActivity(CustomScannerActivity.class);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
