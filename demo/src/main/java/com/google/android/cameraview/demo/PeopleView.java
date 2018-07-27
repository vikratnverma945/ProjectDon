package com.google.android.cameraview.demo;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzutalin.dlib.Constants;

import java.io.File;

/**
 * Display Images Of different People in a Grid
 */
public class PeopleView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_view);
        Database db= new Database(this);
        Cursor peopleresult=db.getDifferentPeople();
        while(peopleresult.moveToNext()){
            String name=peopleresult.getString(0);
            String path= Constants.getDLibImageDirectoryPath()+ File.separator+name;
            Log.i("people",path);
            LinearLayout layout=(LinearLayout)findViewById(R.id.peopleviewcontainer);
            ImageView imgview= new ImageView(this);
            imgview.setTag(name);
            TextView txt=new TextView(this);
            txt.setText(name);
            Bitmap img= BitmapFactory.decodeFile(path);
            imgview.setImageBitmap(img);
            imgview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView imgv=(ImageView)v;
                    String imgname=(String)imgv.getTag();
                    Intent intent= new Intent(PeopleView.this,PersonImagesView.class);
                    intent.putExtra("personname",imgname);
                    startActivity(intent);

                }
            });
            layout.addView(imgview);
            layout.addView(txt);
        }

    }
}
