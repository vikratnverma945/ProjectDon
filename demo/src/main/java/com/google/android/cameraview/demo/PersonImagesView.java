package com.google.android.cameraview.demo;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PersonImagesView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_images_view);
        String personname=getIntent().getStringExtra("personname");
        Log.i("final","recived name from intent"+personname);
        Database db= new Database(this);
        Cursor res=db.getPersonImages(personname);
        LinearLayout layout=(LinearLayout)findViewById(R.id.personimagescontainer);
      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 500);
        while (res.moveToNext()){
          String path=res.getString(2);

            ImageView imgview= new ImageView(this);

            Bitmap img= BitmapFactory.decodeFile(path);
            imgview.setImageBitmap(img);
            layout.addView(imgview,params);

        }

    }
}
