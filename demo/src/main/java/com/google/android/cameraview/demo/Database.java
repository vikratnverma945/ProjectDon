package com.google.android.cameraview.demo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database extends SQLiteOpenHelper {
    public static final String DBNAME="dlibface.db";


    public Database(Context context) {
        super(context, DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    String sql="create table faces (id integer primary key autoincrement,pnmae TEXT,path TEXT,status TEXT)";
        String sql2="create table persons (id integer primary key autoincrement,pnmae TEXT,path TEXT,status TEXT)";
        db.execSQL(sql);
        db.execSQL(sql2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.i("my","Upgrade is called in databse no acton has been taken");
    }

    public boolean insertface(String name,String path,String status){

    SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put("pname",name);
        values.put("path",path);
        values.put("status",status);
        long res= db.insert("face",null,values);

        if(res==-1){
        Log.i("my","insert not success");
            return false;
        }else{
            Log.i("my","insert success");
            return true;
        }
    }
    public boolean insertperson(String name,String path,String status){

        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put("pnmae",name);
        values.put("path",path);
        values.put("status",status);
        long res= db.insert("persons",null,values);

        if(res==-1){
            Log.i("my","insert not success");
            return false;
        }else{
            Log.i("my","insert success");
            return true;
        }
    }
    public Cursor getDifferentPeople(){
        Cursor result;
        SQLiteDatabase db=this.getWritableDatabase();
        result=db.rawQuery("select distinct(pnmae) from persons",null);
        return result;
    }
    public Cursor getPersonImages(String personname){
        Cursor result;
        SQLiteDatabase db=this.getWritableDatabase();
        result=db.rawQuery("select * from persons where pnmae='"+personname+"'",null);
        return result;
    }

    public void delteallrecords() {
        SQLiteDatabase db= this.getWritableDatabase();
        db.execSQL("delete from persons ");
    }
}
