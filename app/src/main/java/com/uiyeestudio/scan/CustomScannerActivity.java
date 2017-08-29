package com.uiyeestudio.scan;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.uiyeestudio.scan.BuildConfig;
import com.uiyeestudio.scan.R;
import com.uiyeestudio.scan.store.CommodityField;
import com.uiyeestudio.scan.store.HttpService;
import com.uiyeestudio.scan.store.entity.History;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.uiyeestudio.scan.store.entity.history.DaoSession;
import com.uiyeestudio.scan.store.entity.history.HistoryDao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Custom Scannner Activity extending from Activity to display a custom layout form scanner view.
 */
public class CustomScannerActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    private static final String TAG = "CustomScannerActivity";
    private static final String COMMODITY_URL = BuildConfig.COMMODITY_URL;
    private final static int GET_COMMODITY = 10086;
    private final static int RC_CAMERA = 10087;

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private BeepManager beepManager;
    private String lastResult = "";
    private Long lastResultTime = 0L;
    private String possibleResult;
    private InputMethodManager inputMethodManager;

    private EditText barcodeInput;
    // private TextView continueText;
    private TextView barcodeQuery;
    private Dialog loadingDialog;
    private ListView commodityList;
    private LinearLayout listHeader;
    private LinearLayout listFooter;
    private TextView headerCountText;
    private TextView footerText;

    private SimpleAdapter simpleAdapter;
    private List<Map<String, Object>> commodityData = new ArrayList<>();
    private DaoSession daoSession;

    private Long keyBackTime = 0L;
    private static final int keyBackDuration = 2000;

    private BarcodeCallback barcodeCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {

            final String resultText = result.getText();
            String resultType = result.getBarcodeFormat().name();
            lastResult = resultText;

            if (resultText.equals(lastResult) && 0!=lastResultTime && (new Date().getTime()-lastResultTime)<500) {
                lastResultTime = new Date().getTime();
                return;
            }

            beepManager.playBeepSound();
            barcodeScannerView.pause();

            if (resultType.equals(BarcodeFormat.QR_CODE.name())) {
                barcodeScannerView.pause();
                UiUtils.showDialog(CustomScannerActivity.this, "访问以下链接？", resultText, "确定", "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startDefaultBrowser(resultText);
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        barcodeScannerView.resume();
                        return;
                    }
                }, false);

            } else if (resultType.equals(BarcodeFormat.EAN_13.name())) {
                barcodeInput.setText(resultText);
                hideKeyboard();
                // continueText.setVisibility(View.VISIBLE);
                startLoading();

                getCommodityFromServer(resultText);
            } else if (resultType.equals(BarcodeFormat.CODE_39)) {
                return;
            }else {
                // startDefaultBrowser("https://www.baidu.com/s?ie=UTF-8&wd="+lastResult);
                UiUtils.showDialog(CustomScannerActivity.this,"", resultText, "确定", null, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // startDefaultBrowser(lastResult);
                        barcodeScannerView.resume();
                    }
                },null, false);
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_scanner);

        init();
       /* capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();*/

    }

    private void init() {
        barcodeScannerView = (DecoratedBarcodeView)findViewById(R.id.zxing_barcode_scanner);

        barcodeInput = (EditText) findViewById(R.id.barcode_edit_text);
        // continueText = (TextView) findViewById(R.id.barcode_text_continue);
        barcodeQuery = (TextView) findViewById(R.id.barcode_query);
        commodityList = (ListView) findViewById(R.id.list_commodity);
        simpleAdapter = new SimpleAdapter(this, commodityData, R.layout.layout_commodity_item,
                new String[]{
                        "title",
                        "price",
                        "barcode"
                },
                new int[]{
                        R.id.text_commodity_title,
                        R.id.text_commodity_price,
                        R.id.text_commodity_barcode
                });
        commodityList.setAdapter(simpleAdapter);
        commodityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    Map itemData = (Map) adapterView.getItemAtPosition(i);
                    startDetailActivity(itemData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        listHeader = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_commodity_header, null);
        listFooter = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_commodity_footer, null);
        headerCountText = listHeader.findViewById(R.id.text_commodity_header_count);
        footerText = listFooter.findViewById(R.id.text_commodity_footer);
        commodityList.addHeaderView(listHeader);
        commodityList.addFooterView(listFooter);
        commodityList.setEmptyView(findViewById(R.id.barcode_empty));

        inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        hideKeyboard();

        ButtonClickListener buttonClickListener = new ButtonClickListener();
        // continueText.setOnClickListener(buttonClickListener);
        barcodeQuery.setOnClickListener(buttonClickListener);

        Toolbar toolbar = (Toolbar) findViewById(R.id.barcode_toolbar);
        setSupportActionBar(toolbar);



        daoSession = ScanApplication.getInstance().getDaoSession();
        requestCameraPermission();

    }

    private void initScanner() {
        barcodeScannerView.decodeContinuous(barcodeCallback);
        beepManager = new BeepManager(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeScannerView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeScannerView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        barcodeScannerView.pause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Long nowTime = System.currentTimeMillis();
        if (keyCode == KeyEvent.KEYCODE_BACK && (nowTime-keyBackTime > keyBackDuration)) {
            keyBackTime = nowTime;
            Toast.makeText(this, "再次点击返回键退出", Toast.LENGTH_SHORT).show();
            return true;
        }
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    public void getCommodityFromServer(final String barcode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String commodityJson = HttpService.getInstance().post(COMMODITY_URL, "{\"barcode\": \"" + barcode + "\"}");
                    Message message = new Message();
                    message.what = GET_COMMODITY;
                    message.obj = commodityJson;
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(final Message msg) {
            if (msg.what == GET_COMMODITY) {
                buildCommodityData((String) msg.obj);
                simpleAdapter.notifyDataSetChanged();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cancelLoading();
                        barcodeScannerView.resume();
                    }
                }, 100);
            }
        }
    };



    private void hideKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(barcodeInput.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void startLoading() {
        if (null == loadingDialog) {
            loadingDialog = DialogThridUtil.showWaitDialog(CustomScannerActivity.this, "加载中...", false, false);
        } else {
            loadingDialog.show();
        }
    }

    private void cancelLoading() {
        if (null == loadingDialog)
            return;
        loadingDialog.cancel();
    }

    private void buildCommodityData(String jsonData) {

        JsonObject jsonEleResultObj = new JsonParser().parse(jsonData).getAsJsonObject();
        String state = jsonEleResultObj.get("state").getAsString();
        String result = jsonEleResultObj.get("data").getAsJsonObject().get("result").getAsString();
        JsonArray jsonArrData = jsonEleResultObj.get("data").getAsJsonObject().get("commodities").getAsJsonArray();
        int count = jsonArrData.size();

        commodityData.clear();
        for (JsonElement element: jsonArrData) {

            JsonObject dataObject = element.getAsJsonObject();
            String title = dataObject.get(CommodityField.TITLE.getField()).getAsString().trim();
            double price = Double.valueOf(dataObject.get(CommodityField.PRICE.getField()).getAsString().trim()) / 10000;
            String barcode = dataObject.get(CommodityField.BARCODE.getField()).getAsString().trim();
            String shopName = dataObject.get(CommodityField.SHOP_NAME.getField()).getAsString().trim();
            String pic = dataObject.get(CommodityField.PIC.getField()).getAsString().trim();
            long id = Long.valueOf(dataObject.get(CommodityField.ID.getField()).getAsString().trim());
            String itemId = dataObject.get(CommodityField.ITEM_ID.getField()).getAsString().trim();

            Map<String, Object> map = new HashMap<>();
            map.put(CommodityField.ID.getField(), id);
            map.put(CommodityField.TITLE.getField(), title);
            map.put(CommodityField.PRICE.getField(), price);
            map.put(CommodityField.BARCODE.getField(), barcode);
            map.put(CommodityField.SHOP_NAME.getField(), shopName);
            map.put(CommodityField.PIC.getField(), pic);
            map.put(CommodityField.ITEM_ID.getField(), itemId);

            commodityData.add(map);
        }

        saveHistoryAsync();

        headerCountText.setText("共找到 " + count + " 条");

        if (count>0) {
            footerText.setVisibility(View.VISIBLE);
        } else {
            footerText.setVisibility(View.GONE);
        }
    }

    private void startDetailActivity(Map<String, Object> itemData) {

        Intent intent = new Intent(this, CommodityDetailActivity.class);
        intent.putExtra(CommodityField.ID.getField(), (Long) itemData.get(CommodityField.ID.getField()));
        intent.putExtra(CommodityField.TITLE.getField(), (String) itemData.get(CommodityField.TITLE.getField()));
        intent.putExtra(CommodityField.PRICE.getField(), (Double) itemData.get(CommodityField.PRICE.getField()));
        intent.putExtra(CommodityField.BARCODE.getField(), (String) itemData.get(CommodityField.BARCODE.getField()));
        intent.putExtra(CommodityField.SHOP_NAME.getField(), (CharSequence) itemData.get(CommodityField.SHOP_NAME.getField()));
        intent.putExtra(CommodityField.PIC.getField(), (String) itemData.get(CommodityField.PIC.getField()));
        intent.putExtra(CommodityField.ITEM_ID.getField(), (String) itemData.get(CommodityField.ITEM_ID.getField()));

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.scan_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan_menu_history:
                startActivity(new Intent(this, HistoryActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        initScanner();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (perms.contains(Manifest.permission.CAMERA)) {
            new AppSettingsDialog.Builder(this)
                    .setTitle("权限请求")
                    .setRationale("应用需要摄像头权限才能正常运行，请在权限管理里面授予。")
                    .setPositiveButton("前往")
                    .setNegativeButton("退出")
                    .build()
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            if (!EasyPermissions.hasPermissions(this, new String[]{Manifest.permission.CAMERA})) {
                this.finish();
            }
        }
    }

    private void requestCameraPermission() {
        String[] permissions = new String[]{Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, permissions)) {
            initScanner();
        } else {
            EasyPermissions.requestPermissions(this, "需要摄像头权限哦", RC_CAMERA, permissions);
        }
    }

    private class ButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.barcode_text_continue:
                    continueScan();
                    break;
                case R.id.barcode_query:
                    getCommodity();
                    break;
                default:
                    break;
            }
        }

        private void continueScan() {
            barcodeScannerView.resume();
            // continueText.setVisibility(View.GONE);
        }

        private void getCommodity() {
            String code = barcodeInput.getText().toString().trim();
            if ("".equals(code) || 13!=code.length()) {
                Toast.makeText(CustomScannerActivity.this, "非法条形码", Toast.LENGTH_SHORT).show();
                return;
            }
            hideKeyboard();
            startLoading();
            getCommodityFromServer(code);
        }

    }

    private void saveHistoryAsync() {
        Thread saveThread = new Thread(new Runnable() {
            @Override
            public void run() {

                HistoryDao historyDao = daoSession.getHistoryDao();
                for (Iterator iterator = commodityData.iterator(); iterator.hasNext(); ) {
                    Map map = (Map) iterator.next();
                    History history = new History(
                            (Long) map.get(CommodityField.ID.getField()),
                            (String) map.get(CommodityField.BARCODE.getField()),
                            (String) map.get(CommodityField.TITLE.getField()),
                            (Double) map.get(CommodityField.PRICE.getField()),
                            (String) map.get(CommodityField.SHOP_NAME.getField()),
                            (String) map.get(CommodityField.PIC.getField()),
                            (String) map.get(CommodityField.ITEM_ID.getField())
                    );
                    historyDao.insertOrReplace(history);
                }
            }

        });
        try {
            saveThread.start();
            saveThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startDefaultBrowser(String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_uri = Uri.parse(url);
        intent.setData(content_uri);
        startActivity(intent);
    }

}
