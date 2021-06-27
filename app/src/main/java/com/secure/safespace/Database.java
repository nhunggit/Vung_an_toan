package com.secure.safespace;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.secure.util.Path;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class Database extends SQLiteOpenHelper {
    public static final String DATABASE_NAME="secure_database.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME="TABLE_SECURE";
    public static final String COLUMN_PATH_ENCRYPT="PATH_ENCRYPT";
    public static final String COLUMN_PATH_DECRYPT="PATH_DECRYPT";
    public static final String COLUMN_DECRYPT="DECRYPT";
    public static final String COLUMN_ID= "COLUMN_ID";
    private static final String SQL_CREATE_TABLE_QUERY="CREATE TABLE " + TABLE_NAME + " ( " + COLUMN_PATH_ENCRYPT + " VARCHAR (255) NOT NULL, " + COLUMN_PATH_DECRYPT  + " VARCHAR (255) NOT NULL, " + COLUMN_DECRYPT + " VARCHAR (255) NOT NULL )";
    //+ COLUMN_ID + " INT AUTO INCREMENT NOT NULL, "
    private static Database instance;
    private String PASS_DB= "nhung123";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_CREATE_TABLE_QUERY);
        onCreate(db);
    }
    static public synchronized Database getInstance(Context context){
        if (instance==null){
            instance= new Database(context);
        }
        return instance;

    }

    public void insertPath(String pathEncrypt, String pathDecrypt, String decrypt){
        SQLiteDatabase sqLiteDatabase= instance.getWritableDatabase(PASS_DB);

        ContentValues contentValues= new ContentValues();

        contentValues.put(COLUMN_PATH_ENCRYPT,pathEncrypt);
        contentValues.put(COLUMN_PATH_DECRYPT,pathDecrypt);
        contentValues.put(COLUMN_DECRYPT, decrypt);
        sqLiteDatabase.insert(TABLE_NAME,null,contentValues);
        sqLiteDatabase.close();
    }

    public void deletePath(String pathEncrypt, String pathDecrypt){
        Log.d("nhungltk", "deletePath: ");
        SQLiteDatabase sqLiteDatabase= instance.getWritableDatabase(PASS_DB);
        ContentValues contentValues= new ContentValues();
        contentValues.put(COLUMN_PATH_DECRYPT, pathDecrypt);
        contentValues.put(COLUMN_PATH_ENCRYPT, pathEncrypt);
        sqLiteDatabase.delete(TABLE_NAME, COLUMN_PATH_ENCRYPT + "='" + pathEncrypt + "'", null);
        sqLiteDatabase.close();
    }

    public List<Path> getList(){
        List<Path> list= new ArrayList();
        SQLiteDatabase sqLiteDatabase= instance.getWritableDatabase(PASS_DB);
        int count=0;
        Cursor cursor= sqLiteDatabase.rawQuery(String.format("SELECT * FROM '%s';",TABLE_NAME), null);
        Log.d("nhungltk", "getList: "+cursor.getCount());
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            String pathEncrypt= cursor.getString(cursor.getColumnIndex(COLUMN_PATH_ENCRYPT));
            String pathDecrypt= cursor.getString(cursor.getColumnIndex(COLUMN_PATH_DECRYPT));
            String decrypt= cursor.getString(cursor.getColumnIndex(COLUMN_DECRYPT));
            //int id= cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            Path path= new Path(pathEncrypt, pathDecrypt, decrypt);
            list.add(path);
            cursor.moveToNext();
        }
        cursor.close();
        sqLiteDatabase.close();

        return list;
    }

    public void delete(Context context){
        context.deleteDatabase(DATABASE_NAME);
    }
}
