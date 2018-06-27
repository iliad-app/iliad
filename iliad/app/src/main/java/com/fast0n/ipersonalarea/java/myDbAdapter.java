package com.fast0n.ipersonalarea.java;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class myDbAdapter {
    private final myDbHelper myhelper;

    public myDbAdapter(Context context) {
        myhelper = new myDbHelper(context);
    }

    public void insertData(String userID, String pass, String name, String phone) {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.userID, userID);
        contentValues.put(myDbHelper.MyPASSWORD, pass);
        contentValues.put(myDbHelper.MyName, name);
        contentValues.put(myDbHelper.MyPhone, phone);

        dbb.insert(myDbHelper.TABLE_NAME, null, contentValues);
    }

    public String getAllData() {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {myDbHelper.UID, myDbHelper.userID, myDbHelper.MyPASSWORD, myDbHelper.MyPhone};
        Cursor cursor = db.query(myDbHelper.TABLE_NAME, columns, null, null, null, null, null);
        StringBuilder buffer = new StringBuilder();
        while (cursor.moveToNext()) {
            String userID = cursor.getString(cursor.getColumnIndex(myDbHelper.userID));
            String password = cursor.getString(cursor.getColumnIndex(myDbHelper.MyPASSWORD));
            String phone = cursor.getString(cursor.getColumnIndex(myDbHelper.MyPhone));

            buffer.append(userID).append("&").append(password).append("&").append(phone).append(" \n");
        }
        cursor.close();
        return buffer.toString();
    }


    public String getUserID() {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {myDbHelper.UID, myDbHelper.userID, myDbHelper.MyPASSWORD, myDbHelper.MyName, myDbHelper.MyPhone};
        Cursor cursor = db.query(myDbHelper.TABLE_NAME, columns, null, null, null, null, null);
        StringBuilder buffer = new StringBuilder();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(myDbHelper.userID));
            buffer.append(name).append("\n");
        }
        cursor.close();
        return buffer.toString();
    }


    public String getPassword() {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {myDbHelper.UID, myDbHelper.userID, myDbHelper.MyPASSWORD, myDbHelper.MyName, myDbHelper.MyPhone};
        Cursor cursor = db.query(myDbHelper.TABLE_NAME, columns, null, null, null, null, null);
        StringBuilder buffer = new StringBuilder();
        while (cursor.moveToNext()) {
            String password = cursor.getString(cursor.getColumnIndex(myDbHelper.MyPASSWORD));
            buffer.append(password);
        }
        cursor.close();
        return buffer.toString();
    }


    public String getName() {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {myDbHelper.UID, myDbHelper.userID, myDbHelper.MyPASSWORD, myDbHelper.MyName, myDbHelper.MyPhone};
        Cursor cursor = db.query(myDbHelper.TABLE_NAME, columns, null, null, null, null, null);
        StringBuilder buffer = new StringBuilder();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(myDbHelper.MyName));
            buffer.append(name).append("\n");
        }
        cursor.close();
        return buffer.toString();
    }


    public String getPhoneNumber() {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {myDbHelper.UID, myDbHelper.userID, myDbHelper.MyPASSWORD, myDbHelper.MyName, myDbHelper.MyPhone};
        Cursor cursor = db.query(myDbHelper.TABLE_NAME, columns, null, null, null, null, null);
        StringBuilder buffer = new StringBuilder();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(myDbHelper.MyPhone));
            buffer.append(name).append("\n");
        }
        cursor.close();
        return buffer.toString();
    }

    public void delete(String uname) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] whereArgs = {uname};

        db.delete(myDbHelper.TABLE_NAME, myDbHelper.userID + " = ?", whereArgs);
    }

    public void updatePassword(String oldName, String newName) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.MyPASSWORD, newName);
        String[] whereArgs = {oldName};
        db.update(myDbHelper.TABLE_NAME, contentValues, myDbHelper.MyPASSWORD + " = ?", whereArgs);
    }

    static class myDbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "myDatabase";
        private static final String TABLE_NAME = "myTable";
        private static final int DATABASE_Version = 1;
        private static final String UID = "_id";
        private static final String userID = "userID";
        private static final String MyPASSWORD = "Password";
        private static final String MyName = "Name";
        private static final String MyPhone = "Phone";
        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + userID + " VARCHAR(255) ," + MyPASSWORD + " VARCHAR(225) ," + MyName + " VARCHAR(225) ," + MyPhone + " VARCHAR(225));";
        private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        private final Context context;

        myDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_Version);
            this.context = context;
        }

        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE);
            } catch (Exception ignored) {
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}