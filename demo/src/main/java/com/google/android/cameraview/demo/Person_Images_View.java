package com.google.android.cameraview.demo;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import com.tzutalin.dlib.Constants;

import java.io.File;
import java.util.ArrayList;

public class Person_Images_View extends AppCompatActivity {
    private GridView gridView;
    private GridViewAdpter2 gridAdapter;
    private String personname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        personname=getIntent().getStringExtra("personname");
        setContentView(R.layout.activity_person__images__view);
        gridView = (GridView) findViewById(R.id.gridView2);
        gridAdapter = new GridViewAdpter2(this, R.layout.gallery_person_image_layout, getData());
        gridView.setAdapter(gridAdapter);
    }

    // Prepare some dummy data for gridview
    private ArrayList<ImageItem2> getData() {
        final ArrayList<ImageItem2> imageItems = new ArrayList<>();
        Database db=new Database(this);
        Cursor res=db.getPersonImages(personname);
        while(res.moveToNext()){
            Bitmap img= BitmapFactory.decodeFile(res.getString(2));

            imageItems.add(new ImageItem2(img));
        }
        return imageItems;
    }
}
