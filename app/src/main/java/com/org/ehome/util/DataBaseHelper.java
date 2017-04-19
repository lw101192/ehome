package com.org.ehome.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by liuwei on 2016/8/3.
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
//    String sql = "drop table mymachine";

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DataBaseHelper(Context context, String name, int version) {
        this(context, name, null, version);
    }

    public DataBaseHelper(Context context, String name) {
        this(context, name, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            sqLiteDatabase.execSQL("create table  if not exists mymachine (id varchar primarykey,nickname varchar,createtime datetime,lastsynchronizetime datetime,unreadmessagenumber int,lastmessage varchar,online varchar)");
        } catch (Exception e) {

        }

        try {
            sqLiteDatabase.execSQL("create table  if not exists histroy (id varchar,nickname varchar,createtime datetime,content varchar,isread boolean,type varchar,show boolean)");
        } catch (Exception e) {
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
