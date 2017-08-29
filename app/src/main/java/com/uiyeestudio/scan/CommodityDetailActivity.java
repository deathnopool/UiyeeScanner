package com.uiyeestudio.scan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.uiyeestudio.scan.BuildConfig;
import com.uiyeestudio.scan.R;
import com.uiyeestudio.scan.store.CommodityField;
import com.squareup.picasso.Picasso;

public class CommodityDetailActivity extends AppCompatActivity {

    private static final String ITEM_URL = BuildConfig.TB_ITEM_URL;
    private static final String PIC_URL = BuildConfig.TB_ITEM_PIC_URL;

    private TextView titleView;
    private ImageView picView;
    private TextView priceView;
    private TextView barcodeView;
    private TextView shopNameView;
    private TextView tbUrlView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodity_detail);
        init();
    }

    private void init() {

        Intent intent = getIntent();

        String title = intent.getStringExtra(CommodityField.TITLE.getField());
        double price = intent.getDoubleExtra(CommodityField.PRICE.getField(), 0);
        String barcode = intent.getStringExtra(CommodityField.BARCODE.getField());
        String shopName = intent.getStringExtra(CommodityField.SHOP_NAME.getField());
        String pic = intent.getStringExtra(CommodityField.PIC.getField());
        String itemId = intent.getStringExtra(CommodityField.ITEM_ID.getField());

        titleView = (TextView) findViewById(R.id.text_detail_title);
        priceView = (TextView) findViewById(R.id.text_detail_price);
        barcodeView = (TextView) findViewById(R.id.text_detail_barcode);
        shopNameView = (TextView) findViewById(R.id.text_detail_shopName);
        picView = (ImageView) findViewById(R.id.img_detail_pic);
        tbUrlView = (TextView) findViewById(R.id.text_detail_url);

        titleView.setText(title);
        Picasso.with(this).load(PIC_URL + pic).into(picView);
        priceView.setText("" + price);
        barcodeView.setText(barcode);
        shopNameView.setText(shopName);
        tbUrlView.setText(ITEM_URL + "?id=" + itemId);

        initActionBar();
    }

    private void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
