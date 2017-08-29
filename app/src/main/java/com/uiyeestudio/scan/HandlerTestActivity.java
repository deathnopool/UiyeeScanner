package com.uiyeestudio.scan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.uiyeestudio.scan.R;

public class HandlerTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler_test);
    }

    private void doSomething() {
        Intent intent = new Intent(this, ListActivity.class);
    }
}
