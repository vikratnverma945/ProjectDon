package com.google.android.cameraview.demo;

import android.content.Context;
import android.util.Log;

public class PersonRecord {
    String name;
    String path;
    String status;
    PersonRecord(String n,String p, String s){
        name=n;
        path=p;
        status=s;

    }
    public void savetodb(Database db){
         if(db.insertperson(name,path,status)){
            Log.i("organise","image added to Database Sucess");
        }else{
            Log.i("organise","Error  added to Database Sucess");
        }

    }

}
