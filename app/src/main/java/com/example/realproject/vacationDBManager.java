package com.example.realproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class vacationDBManager {
    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);

    private static final String DB_FIRST_VACATION = "FirstVacation.db";

    static final String TABLE_FIRST = "FirstVacation";
    static final String TABLE_USER = "User";

    private static final String CREATE_TABLE_FIRST_VACATION = "CREATE TABLE IF NOT EXISTS " + TABLE_FIRST + "(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "vacation TEXT, " + "startDate DATE, " +
            "type TEXT, " + "count DOUBLE );";
    private static final String CREATE_TABLE_USER = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + "(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "nickName TEXT, " + "firstDate DATE, " +
            "lastDate DATE, " + "mealCost INTEGER, " + "trafficCost INTEGER );";

    static final int FIRST_VERSION = 1;

    Context mContext = null;
    private static vacationDBManager mDBmanager = null;
    private SQLiteDatabase mDatabase;


    public static vacationDBManager getInstance(Context context){
            if(mDBmanager == null){
            mDBmanager = new vacationDBManager(context);
        }
        return mDBmanager;
    }

    private vacationDBManager(Context context){
        mContext = context;
        mDatabase = context.openOrCreateDatabase(DB_FIRST_VACATION, Context.MODE_PRIVATE, null);
        mDatabase.execSQL(CREATE_TABLE_FIRST_VACATION);
        mDatabase.execSQL(CREATE_TABLE_USER);
    }

    //data 추가
    public long insertFirstVacation(FirstVacation firstVacation){
        ContentValues values = new ContentValues();
        values.put("vacation", firstVacation.getVacation());
        values.put("startDate", formatter.format(firstVacation.getStartDate()));
        values.put("type", firstVacation.getType());
        values.put("count", firstVacation.getCount());
        return mDatabase.insert(TABLE_FIRST, null, values);
    }
    public long insertUser(User user){
        ContentValues values = new ContentValues();
        values.put("nickName", user.getNickName());
        values.put("firstDate", formatter.format(user.getFirstDate()));
        values.put("lastDate", formatter.format(user.getLastDate()));
        values.put("mealCost", user.getMealCost());
        values.put("trafficCost", user.getTrafficCost());
        return mDatabase.insert(TABLE_USER, null, values);
    }

    // data 삭제
    public int deleteFirstVacation(FirstVacation firstVacation) {
        int id = firstVacation.getId();
        String[] idArr = new String[] { String.valueOf(id) };
        return mDatabase.delete(TABLE_FIRST, "id =?", idArr);
    }
    public int deleteUser(User user) {
        int userId = user.getId();
        String[] idArr = new String[] { String.valueOf(userId) };
        return mDatabase.delete(TABLE_USER, "id =?", idArr);
    }

    // data 수정
    // firstVacation을 받아서 getId()하면 안되고 그냥 id를 직접 받으면 되는데 이유를 모름.
    public long updateFirstVacation (int id, ContentValues addRowValue){
        String[] idArr = new String[] { String.valueOf(id) };
        return mDatabase.update(TABLE_FIRST, addRowValue, "id =?", idArr);
    }
    public long updateUser (int id, ContentValues addRowValue){
        String[] idArr = new String[] { String.valueOf(id) };
        return mDatabase.update(TABLE_USER, addRowValue, "id =?", idArr);
    }

    //특정 table의 data 한줄 뽑아오기
    public Cursor query (String[] columns, String tableName, String selection, String[] selectionArgs,
                         String groupBy, String having, String orderBy)
    {
        return mDatabase.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public int getDataCount(String tableName){
        String countQuery = "SELECT  * FROM " + tableName;
        Cursor cursor = mDatabase.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        return count;
    }
}
