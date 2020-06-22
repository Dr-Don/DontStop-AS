package com.example.dontstop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.Contacts;

public class DBAdapter {
    private static final String DB_NAME = "player.db";
    private static final String DB_TABLE = "player";
    private static final int DB_VERSION = 1;

    public static final String KEY_NAME = "nametext";
    public static final String KEY_SCORE = "scoreinteger";

    private static final String DB_CREATE = "create table " + DB_TABLE + "(" +
            KEY_NAME+ " STRING primary key," + KEY_SCORE + " INTEGER);";

    private SQLiteDatabase db;
    private final Context context;
    private DBOpenHelper dbOpenHelper;

    public DBAdapter(Context context) {
        this.context = context;
    }

    private static class DBOpenHelper extends SQLiteOpenHelper{
        public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,int version){
            super(context,name,factory,version);
        }

        @Override
        public void onCreate(SQLiteDatabase _db){
            _db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion){
            _db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
            onCreate(_db);
        }
    }

    /**
     * 打开数据库
     * */
    public void open() throws SQLiteException{
        dbOpenHelper = new DBOpenHelper(context,DB_NAME,null,DB_VERSION);
        try{
            db = dbOpenHelper.getWritableDatabase();
        } catch (SQLiteException ex){
            db = dbOpenHelper.getReadableDatabase();
        }
    }

    /**
     * 关闭数据库
     * */
    public void close() {
        if (db != null) {
            db.close();
            db = null;
        }
    }

    /**
     * 插入数据
     * */
    public long insert(Player player){
        ContentValues newValues = new ContentValues();

        newValues.put(KEY_NAME, player.name);
        newValues.put(KEY_SCORE,player.score);

        return db.insert(DB_TABLE,null,newValues);
    }

    /**
     * 删除数据
     * */
    public long deleteAllData() {
        return db.delete(DB_TABLE, null, null);
    }

    public long deleteOneData(String name) {
        return db.delete(DB_TABLE,  KEY_NAME + "=" + name, null);
    }

    /**
     * 更新数据
     * */
    public long updateOneData(String name , Player player){
        ContentValues updateValues = new ContentValues();
        updateValues.put(KEY_SCORE, player.score);

        return db.update(DB_TABLE, updateValues,  KEY_NAME + "= '" + name + "'", null);
    }

    private Player[] ConvertToPlayer(Cursor cursor){
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || !cursor.moveToFirst()){
            return null;
        }

        Player[] players = new Player[resultCounts];
        for (int i = 0 ; i<resultCounts; i++){
            players[i] = new Player();
            players[i].name = cursor.getString(0);
            players[i].score= cursor.getInt(cursor.getColumnIndex(KEY_SCORE));
            cursor.moveToNext();
        }
        cursor.close();
        return players;
    }

    /**
     * 按名查询数据
     * */
    public Player getDate(String playerName){
        Cursor result = db.rawQuery("select * from " + DB_TABLE + " where " + KEY_SCORE +" = '" + playerName + "' ", null);
        Player player = new Player();
        if (result != null && result.moveToFirst() && result.getCount() > 0) {
            player.name = result.getString(result.getColumnIndex(KEY_NAME));
            player.score = result.getInt(result.getColumnIndex(KEY_SCORE));
        }
        result.close();
        return player;
    }

    /**
     * 查询全部数据
     * */
    public Player[] getAllData(){
        Cursor results = db.query(DB_TABLE, new String[] { KEY_NAME, KEY_SCORE}, null, null, null, null, null);
        return ConvertToPlayer(results);
    }
}
