package com.google.android.cameraview.demo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tzutalin.dlib.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.google.android.cameraview.demo.OraganiserService.estimatedtime;

public class Welcome_Activity extends AppCompatActivity {
    String[] permissions = new String[]{

            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    SharedPreferences prefes;
    ProgressBar progressbar;
    BroadcastReceiver receiver;
    IntentFilter intentFilter;
    IntentFilter intentFilter2;
    BroadcastReceiver receiver2;
    private boolean is2regiter=false;
    private Intent sintent;
    private SharedPreferences sharedPreferences;


    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("openprefes", MODE_PRIVATE);
        boolean com = sharedPreferences.getBoolean("completed", false);
        if (com) {
            startActivity(new Intent(Welcome_Activity.this, MainActivity.class));
        } else {

            if (savedInstanceState != null) {
                super.onRestoreInstanceState(savedInstanceState);
            }
            setContentView(R.layout.activity_welcome);
        }

    }


    /**
     * Asking for permission
     * @param view
     */
    /**
     * Method es Excuted on Continue click
     * @param view
     */
    public void askforstorage(View view) {
       if(checkPermissions()){

           AlertDialog.Builder dialog=new AlertDialog.Builder(this);
           final EditText edt= new EditText(this);
           dialog.setView(edt);
           dialog.setTitle("Select The Number Of images to process or enter -1 to to search for all images");
           dialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   int value=Integer.parseInt(edt.getText().toString());
                   OraganiserService. maximgtoprocess=value;
                   extractfaces();
               }
           });

           setupdlibir();
           Database  db= new Database(this);
           db.delteallrecords();
           dialog.show();
        }else{
        //  Toast.makeText(this,"Sorry We cant Continue Without Storage Permisions",Toast.LENGTH_LONG).show();
        }
    }

    private void extractfaces() {
        intentFilter =  new IntentFilter();
        intentFilter.addAction("UPDATE_BAR");
        setContentView(R.layout.activity_faces__extraction);
        sintent= new Intent(this,OraganiserService.class);
        final TextView timeremshow=(TextView)findViewById(R.id.facetime);
        progressbar=(ProgressBar) findViewById(R.id.progressBar);
       final int estimate=(int) estimatedtime(Environment.getExternalStorageDirectory());
         progressbar.setMax(estimate*60);

        receiver= new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean completed=intent.getBooleanExtra("completed",false);
                if(completed==true){
                    organniseimages();
                    return;
                }
                int prog=intent.getIntExtra("value",0);
          progressbar.setProgress(prog);
               int minute= ((estimate*60)-prog)/60;
          timeremshow.setText("Time Remaining = "+minute+"Minutes");
            }
        };
        registerReceiver(receiver,intentFilter);
        startService(sintent);


    }

    private void organniseimages() {
        intentFilter2= new IntentFilter();
        intentFilter2.addAction("UPDATE_BAR_2");

    setContentView(R.layout.face_organiser);

        final TextView timeremshow=(TextView)findViewById(R.id.organisefacetime);
    final ProgressBar progressbar2=(ProgressBar)findViewById(R.id.organiserbar);
        final int estimate=(int) estimatedtime(Environment.getExternalStorageDirectory());

        progressbar2.setMax(estimate*60*5);
    receiver2= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            is2regiter=true;
            int prog=intent.getIntExtra("value",0);
            progressbar2.setProgress(prog);
            int minute= ((estimate*60*5)-prog)/60;
            timeremshow.setText("Time Remaining = "+minute+"Minutes");
            boolean completed=intent.getBooleanExtra("completed",false);
            if(completed){
                stopService(sintent);
                SharedPreferences.Editor editor= sharedPreferences.edit();
                editor.putBoolean("completed",true);
                editor.apply();
            //   Toast.makeText(Welcome_Activity.this,"All Completed Images has been organised",Toast.LENGTH_LONG).show();
                startActivity( new Intent(Welcome_Activity.this,MainActivity.class));
            }
          }
    };
if(!is2regiter){
    registerReceiver(receiver2, intentFilter2);
}
    }


    private boolean checkPermissions()   {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }


    /**
     * Copy dat files from raw folder and creating dlib directories
     */


    private void setupdlibir() {
        Log.i("dlibsetup","Creating Folder");
        File dir=new File(Constants.getDLibImageDirectoryPath());
        if(dir.exists()){
            return;
        }
        if(!dir.exists()){
            if(dir.mkdirs()){
                Log.i("dlibsetup","Folder Created Copying Files");
                copydatfiles(R.raw.dlib_face_recognition_resnet_model_v1,Constants.getFaceDescriptorModelPath());
                copydatfiles(R.raw.shape_predictor_5_face_landmarks,Constants.getFaceShapeModelPath());
            }else{
                Log.i("dlibsetup","Error ccreating data");
            }

        }else {
            Log.i("dlibsetup","Dlib setuped");
        }
    }








    public void copydatfiles(int id,String file){
        InputStream in = getResources().openRawResource(id);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[] buff = new byte[1024];
        int read = 0;

        try {
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}

