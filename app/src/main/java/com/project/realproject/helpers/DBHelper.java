package com.project.realproject.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.project.realproject.User;
import com.project.realproject.Vacation;

import static com.project.realproject.helpers.Formatter.*;

public class DBHelper extends SQLiteOpenHelper {

    private Context mContext;

    // 주의 DATABASE_NAME 바꾸면 db 달라져서 초기화.
    private static final String DATABASE_NAME = "FirstVacation.db";

    public static final String TABLE_VACATION = "FirstVacation";
    public static final String TABLE_USER = "User";

    static final int DATABASE_VERSION = 1;

    private static final String COLUMN_VACATION_NAME = "vacation";
    private static final String COLUMN_VACATION_START_DATE = "startDate";
    private static final String COLUMN_VACATION_TYPE = "type";
    private static final String COLUMN_VACATION_COUNT = "count";

    private static final String COLUMN_USER_NICKNAME = "nickName";
    private static final String COLUMN_USER_FIRST_DATE = "firstDate";
    private static final String COLUMN_USER_LAST_DATE = "lastDate";
    private static final String COLUMN_USER_MEAL_COST = "mealCost";
    private static final String COLUMN_USER_TRAFFIC_COST = "trafficCost";
    private static final String COLUMN_USER_TOTAL_FIRST_VAC = "totalFirstVac";
    private static final String COLUMN_USER_TOTAL_SECOND_VAC = "totalSecondVac";
    private static final String COLUMN_USER_TOTAL_SICK_VAC = "totalSickVac";
    private static final String COMMA_SEP = ", ";


    private static final String CREATE_TABLE_VACATION = "CREATE TABLE IF NOT EXISTS " + TABLE_VACATION + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
            COLUMN_VACATION_NAME + " TEXT" + COMMA_SEP +
            COLUMN_VACATION_START_DATE + " DATE" + COMMA_SEP +
            COLUMN_VACATION_TYPE + " TEXT" + COMMA_SEP +
            COLUMN_VACATION_COUNT + " DOUBLE" + " );";

    private static final String CREATE_TABLE_USER = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + "(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
            COLUMN_USER_NICKNAME + " TEXT" + COMMA_SEP +
            COLUMN_USER_FIRST_DATE + " DATE" + COMMA_SEP +
            COLUMN_USER_LAST_DATE + " DATE" + COMMA_SEP +
            COLUMN_USER_MEAL_COST + " INTEGER" + COMMA_SEP +
            COLUMN_USER_TRAFFIC_COST + " INTEGER" + COMMA_SEP +
            COLUMN_USER_TOTAL_FIRST_VAC + " INTEGER" + COMMA_SEP +
            COLUMN_USER_TOTAL_SECOND_VAC + " INTEGER" + COMMA_SEP +
            COLUMN_USER_TOTAL_SICK_VAC + " INTEGER" + " );";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_VACATION);
        db.execSQL(CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //data 추가
    public long insertVacation(Vacation vacation){
        SQLiteDatabase mDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_VACATION_NAME, vacation.getVacation());
        values.put(COLUMN_VACATION_START_DATE, formatter.format(vacation.getStartDate()));
        values.put(COLUMN_VACATION_TYPE, vacation.getType());
        values.put(COLUMN_VACATION_COUNT, vacation.getCount());
        return mDatabase.insert(TABLE_VACATION, null, values);
    }

    public long insertUser(User user){
        SQLiteDatabase mDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();
        String firstDate = formatter.format(user.getFirstDateTime());
        String lastDate = formatter.format(user.getLastDateTime());
        values.put(COLUMN_USER_NICKNAME, user.getNickName());
        values.put(COLUMN_USER_FIRST_DATE, firstDate);
        values.put(COLUMN_USER_LAST_DATE, lastDate);
        values.put(COLUMN_USER_MEAL_COST, user.getMealCost());
        values.put(COLUMN_USER_TRAFFIC_COST, user.getTrafficCost());
        values.put(COLUMN_USER_TOTAL_FIRST_VAC, user.getTotalFirstVac());
        values.put(COLUMN_USER_TOTAL_SECOND_VAC, user.getTotalSecondVac());
        values.put(COLUMN_USER_TOTAL_SICK_VAC, user.getTotalSickVac());
        return mDatabase.insert(TABLE_USER, null, values);
    }

    // data 삭제
    public void deleteVacation(Vacation vacation) {
        SQLiteDatabase mDatabase = getWritableDatabase();
        mDatabase.delete(TABLE_VACATION, "id =?",
                new String[] { String.valueOf(vacation.getId()) });
    }

    // data 수정
    public void updateVacation (int id, ContentValues addRowValue){
        SQLiteDatabase mDatabase = getWritableDatabase();
        mDatabase.update(TABLE_VACATION, addRowValue, "id =?",
                new String[] { String.valueOf(id) });
    }

    public void updateUser (int id, User user){
        SQLiteDatabase mDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NICKNAME, user.getNickName());
        values.put(COLUMN_USER_FIRST_DATE, formatter.format(user.getFirstDateTime()));
        values.put(COLUMN_USER_LAST_DATE, formatter.format(user.getLastDateTime()));
        values.put(COLUMN_USER_MEAL_COST, user.getMealCost());
        values.put(COLUMN_USER_TRAFFIC_COST, user.getTrafficCost());
        values.put(COLUMN_USER_TOTAL_FIRST_VAC, user.getTotalFirstVac());
        values.put(COLUMN_USER_TOTAL_SECOND_VAC, user.getTotalSecondVac());
        values.put(COLUMN_USER_TOTAL_SICK_VAC, user.getTotalSickVac());
        mDatabase.update(TABLE_USER, values, "id =?",
                new String[] { String.valueOf(id) });
    }

    public Cursor query (String[] columns, String tableName, String selection, String[] selectionArgs,
                         String groupBy, String having, String orderBy)
    {
        SQLiteDatabase mDatabase = getWritableDatabase();
        return mDatabase.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public int getDataCount(String tableName){
        SQLiteDatabase mDatabase = getWritableDatabase();
        String countQuery = "SELECT * FROM " + tableName;
        Cursor cursor = mDatabase.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public void deleteAll(){
        SQLiteDatabase mDatabase = getWritableDatabase();
        mDatabase.delete(TABLE_VACATION, null, null);
        mDatabase.delete(TABLE_USER, null, null);
    }


}
