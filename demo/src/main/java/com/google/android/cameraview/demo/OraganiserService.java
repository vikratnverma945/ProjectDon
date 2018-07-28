package com.google.android.cameraview.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.tzutalin.dlib.Constants;
import com.tzutalin.dlib.FaceRec;
import com.tzutalin.dlib.VisionDetRet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OraganiserService extends Service {


    Database db;
   static  long load_time;
    static FaceRec faceRec;
    public static int total_images=0;
    public static List<String> name_path_list;
    public static int tempindex=0;
   public  static int maximgtoprocess=25;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("my","I bind was called");
        return null;
    }

    @Override
    public void onCreate() {
        name_path_list= new ArrayList();
        faceRec= new FaceRec(Constants.getDLibDirectoryPath());
        db=new Database(this);
        Log.i("my"," On create  was called");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        searchfornewfaces();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i("my","destroy is called ");
        super.onDestroy();
    }

    public  static long estimatedtime(File dir){

        String extention = ".jpg";
        File[] listFile = dir.listFiles();
        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    if(listFile[i].getName().equals(".thumbnails")||listFile[i].getName().equals("Android")){
                        continue;
                    }else{
                        estimatedtime(listFile[i]);}
                } else {
                    if (listFile[i].getName().endsWith(extention)) {
                        if(i>maximgtoprocess){
                            break;
                        }
                        load_time+=2;
                    }
                }
            }
        }

        return load_time/60;
    }



    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    public List<VisionDetRet> findfaces(Bitmap img){

        return faceRec.detect(img);
    }
    public  void organiseimages(Database db){

        AsyncTask task= new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Intent broadcastintent2=new Intent();
                broadcastintent2.setAction("UPDATE_BAR_2");

                Database db=(Database)objects[0];
                int j=0;
                List<String> image_path = load_image_files(Environment.getExternalStorageDirectory());
                if(trainfaces()){
                    int i = 0;
                    for (String path : image_path) {
                        i++;
                        if (i > maximgtoprocess) {
                            break;
                        }
                        Bitmap img = BitmapFactory.decodeFile(path);
                        List<VisionDetRet> foundfaces = faceRec.recognize(img);
                        Log.i("accuracy", "Checking image for faces " + path);
                        for (VisionDetRet faces : foundfaces) {
                            Log.i("accuracy", "Image" + path + "matched to the face" + faces.getLabel());
                            Log.i("accuracy","Confidence is"+faces.getConfidence());
                            PersonRecord personRecord= new PersonRecord(faces.getLabel(),path,"true");
                            personRecord.savetodb(db);
                        }
                        j+=5;
                        broadcastintent2.putExtra("value",j);
                        sendBroadcast(broadcastintent2);

                        Log.i("organise","Chekign is in trained"+i+"images");
                    }
                    broadcastintent2.putExtra("completed",true);
                    sendBroadcast(broadcastintent2);
                }
           //     Toast.makeText(OraganiserService.this,"Sll Complete",Toast.LENGTH_LONG).show();
                return null;
            }
        };
        task.execute(db);

    }
    public void distroy(){
        faceRec.release();
    }
    public static boolean trainfaces(){

        Log.i("organise","Trainig the faces");
        faceRec.train();
        Log.i("organise","Faces Trained");
        return true;

    }

    public  void searchfornewfaces(){
        @SuppressLint("StaticFieldLeak") AsyncTask task= new AsyncTask() {


            @Override
            protected Object doInBackground(Object[] objects) {
                Intent broadcastintent=new Intent();
                broadcastintent.setAction("UPDATE_BAR");
                List image_path=load_image_files(Environment.getExternalStorageDirectory());
                Log.i("my","List size=-"+image_path.size());
                int j=0;
                for(int i=0;i<image_path.size();i++){
                    j+=2;
                    if(i>maximgtoprocess){
                        break;
                    }
                    String orignal_path=(String)image_path.get(i);
                    Bitmap image= BitmapFactory.decodeFile(orignal_path);
                    organiseBitmap(image,orignal_path);
                    broadcastintent.putExtra("value",j);
                    broadcastintent.putExtra("completed",false);
                    sendBroadcast(broadcastintent);
                    Log.i("temp1","Progress"+j);
                }
                broadcastintent.putExtra("completed",true);
                sendBroadcast(broadcastintent);
                organiseimages(db);
                Log.i("my","Bitmap created task finised");

                return null;
            }
        };
        task.execute();
    }
    public static List<String> load_image_files(File dir) {


        String extention = ".jpg";
        File[] listFile = dir.listFiles();
        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    if(listFile[i].getName().equals(".thumbnails")||listFile[i].getName().equals("Android")){
                        continue;
                    }else{
                        load_image_files(listFile[i]);}
                } else {
                    if (listFile[i].getName().endsWith(extention)) {
                        //name_list.add(listFile[i].getName());
                        name_path_list.add(listFile[i].getAbsolutePath());
                        //  Log.i("my","image pth="+listFile[i].getAbsolutePath());
                        total_images++;
                    }
                }
            }
        }
        Log.i("my","Toal iteration for searching images = "+total_images);
        return name_path_list;
    }

    /**
     *Take the image and find different faces and crop the face and copy it to dlind directory
     * @param img
     */
    public static void organiseBitmap(Bitmap img,String imgpath){
       img=scaleDown(img,500,true);
        List<VisionDetRet> faces=faceRec.detect(img);
        for(VisionDetRet face:faces){

            int width=face.getRight()-face.getLeft();
            int height=face.getBottom()-face.getTop();
            Bitmap faceimg=null;

            try {
                faceimg= Bitmap.createBitmap(img, face.getLeft(), face.getTop(), width, height);
                tempindex++;
            }catch (Exception e){
                Log.i("my","Exception in processing image ");
                continue;
            }
            String path= Constants.getDLibImageDirectoryPath()+File.separator+"person"+tempindex+".jpg";
            savebitmaptopath(new File(path),faceimg);
        }
    }

    /**
     * Save the Given Bitmap to a the path as a file
     * @param file
     * @param img
     */
    public static void savebitmaptopath(File file,Bitmap img){
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            img.compress(Bitmap.CompressFormat.JPEG,100,out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                Log.i("my","Io errror in saving File");
                e.printStackTrace();
            }
        }
    }



}
