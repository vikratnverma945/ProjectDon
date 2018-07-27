//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.tzutalin.dlib;

import android.graphics.Bitmap;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;
import java.util.Arrays;
import java.util.List;

public class sd {
    private static final String TAG = "dlib";
    private long mNativeFaceRecContext;
    private String dir_path = "";

    public sd(String sample_dir_path) {
        this.dir_path = sample_dir_path;
        this.jniInit(this.dir_path);
    }

    @Nullable
    @WorkerThread
    public void train() {
        this.jniTrain();
    }

    @Nullable
    @WorkerThread
    public List<VisionDetRet> recognize(@NonNull Bitmap bitmap) {
        VisionDetRet[] detRets = this.jniBitmapRec(bitmap);
        return Arrays.asList(detRets);
    }

    @Nullable
    @WorkerThread
    public List<VisionDetRet> detect(@NonNull Bitmap bitmap) {
        VisionDetRet[] detRets = this.jniBitmapDetect(bitmap);
        return Arrays.asList(detRets);
    }

    protected void finalize() throws Throwable {
        super.finalize();
        this.release();
    }

    public void release() {
        this.jniDeInit();
    }

    @Keep
    private static native void jniNativeClassInit();

    @Keep
    private synchronized native int jniInit(String var1);

    @Keep
    private synchronized native int jniDeInit();

    @Keep
    private synchronized native int jniTrain();

    @Keep
    private synchronized native VisionDetRet[] jniBitmapDetect(Bitmap var1);

    @Keep
    private synchronized native VisionDetRet[] jniBitmapRec(Bitmap var1);

    static {
        try {
            System.loadLibrary("android_dlib");
            jniNativeClassInit();
            Log.d("dlib", "jniNativeClassInit success");
        } catch (UnsatisfiedLinkError var1) {
            Log.e("dlib", "library not found");
        }

    }
}
