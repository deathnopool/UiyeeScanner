package com.uiyeestudio.scan;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.uiyeestudio.scan.store.entity.history.DaoMaster;
import com.uiyeestudio.scan.store.entity.history.DaoSession;

/**
 * Created by Michael on 2017/8/25.
 */

public class ScanApplication extends Application {

    private final static String TAG = "ScanApplication";
    private static ScanApplication instance;
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    @Override
    public void onCreate() {
        Log.i(TAG, "application onCreate...");
        super.onCreate();
        instance = this;
        setDatabase();
    }

    public static ScanApplication getInstance() {
        return instance;
    }

    private void setDatabase() {
        mHelper = new DaoMaster.DevOpenHelper(this, "histories-db", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }
    public SQLiteDatabase getDb() {
        return db;
    }

}
