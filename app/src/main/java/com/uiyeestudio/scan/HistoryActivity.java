package com.uiyeestudio.scan;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.uiyeestudio.scan.R;
import com.uiyeestudio.scan.store.CommodityField;
import com.uiyeestudio.scan.store.entity.History;
import com.uiyeestudio.scan.store.entity.history.DaoSession;
import com.uiyeestudio.scan.store.entity.history.HistoryDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {

    private ListView commodityList;
    private TextView emptyView;

    private SimpleAdapter simpleAdapter;
    private List<Map<String, Object>> commodityData = new ArrayList<>();
    private DaoSession daoSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        init();
    }

    private void init() {
        initActionBar();

        commodityList = (ListView) findViewById(R.id.layout_history_list);
        simpleAdapter = new SimpleAdapter(this, commodityData, R.layout.layout_commodity_item,
                new String[]{"title", "price", "barcode"},
                new int[]{
                        R.id.text_commodity_title,
                        R.id.text_commodity_price,
                        R.id.text_commodity_barcode
                });
        commodityList.setAdapter(simpleAdapter);
        commodityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i<0) {
                    return;
                }
                // Toast.makeText(HistoryActivity.this, "hello", Toast.LENGTH_SHORT).show();
                startDetailActivity((Map<String, Object>) adapterView.getItemAtPosition(i));
            }
        });
        emptyView = (TextView) findViewById(R.id.layout_history_empty);
        commodityList.setEmptyView(emptyView);

        daoSession = ScanApplication.getInstance().getDaoSession();
        buildCommodityData();
    }

    private void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.layout_history_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void buildCommodityData() {

        HistoryDao historyDao = daoSession.getHistoryDao();
        List<History> historyList = historyDao.loadAll();
        for (History history : historyList) {
            Map<String, Object> map = new HashMap<>();
            map.put(CommodityField.ID.getField(), history.getId());
            map.put(CommodityField.TITLE.getField(), history.getTitle());
            map.put(CommodityField.PRICE.getField(), history.getPrice());
            map.put(CommodityField.BARCODE.getField(), history.getBarcode());
            map.put(CommodityField.SHOP_NAME.getField(), history.getShopName());
            map.put(CommodityField.PIC.getField(), history.getPic());
            map.put(CommodityField.ITEM_ID.getField(), history.getItemId());

            commodityData.add(map);
        }

        simpleAdapter.notifyDataSetChanged();
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
        getMenuInflater().inflate(R.menu.history_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.hitory_menu_clear:
                clearHistoryDialog();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearHistoryDialog() {
        UiUtils.showDialog(this, "提示", "确定清空？", "确定", "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                clearHistory();
            }
        }, null, true);
    }

    private void clearHistory() {
        commodityData.clear();
        simpleAdapter.notifyDataSetChanged();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HistoryDao historyDao = daoSession.getHistoryDao();
                historyDao.deleteAll();
            }
        }).start();
    }
}
