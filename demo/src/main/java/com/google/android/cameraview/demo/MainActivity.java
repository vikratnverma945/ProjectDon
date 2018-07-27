
package com.google.android.cameraview.demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.FaceDetector;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tzutalin.dlib.Constants;
import com.tzutalin.dlib.FaceRec;
import com.tzutalin.dlib.VisionDetRet;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                    Intent intent= new Intent(MainActivity.this,PersonImagesView.class);
                    intent.putExtra("personname",imgname);
                    startActivity(intent);

                }
            });
            layout.addView(imgview);
            layout.addView(txt);
        }
              }


}
