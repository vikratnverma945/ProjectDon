
package com.google.android.cameraview.demo;

import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.tzutalin.dlib.Constants;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {
    private GridView gridView;
    private GridViewAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_activity_main);

        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(this, R.layout.galler_grid_item_layout, getData());
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                //Create intent
                Intent intent = new Intent(MainActivity.this, Person_Images_View.class);
                intent.putExtra("personname", item.getTitle());
                intent.putExtra("image", item.getImage());

                //Start details activity
                startActivity(intent);
            }
        });
    }
        // Prepare some dummy data for gridview
        private ArrayList<ImageItem> getData () {
            final ArrayList<ImageItem> imageItems = new ArrayList<>();
            Database db = new Database(this);
            Cursor res = db.getDifferentPeople();
            while (res.moveToNext()) {
                Bitmap img = BitmapFactory.decodeFile(Constants.getDLibImageDirectoryPath() + File.separator + res.getString(0));
                String name = res.getString(0);
                imageItems.add(new ImageItem(img, name));
            }
            return imageItems;
        }


}
