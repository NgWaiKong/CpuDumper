package com.ngwaikong.cpudumper;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ngwaikong.cpudumper.sampler.DebuggerdCmd;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by ngwaikong on 2018/5/27.
 */

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                    BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File("/sdcard/cpu/" + System.currentTimeMillis() + "_file")));
                    CpuDumper cpuDumper = new CpuDumper(String.valueOf(android.os.Process.myPid()));
                    String ret = cpuDumper.dumpToString();
                    cpuDumper.dumpTop(5000, stream);
                    cpuDumper.dumpJavaStack();
                    cpuDumper.dumpNativeStack();
                    Log.i(TAG, ret);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        }.start();
    }

}
