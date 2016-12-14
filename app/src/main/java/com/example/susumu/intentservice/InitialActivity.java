package com.example.susumu.intentservice;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class InitialActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Activity","InitialActivity");
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://mznstamp.sist.ac.jp"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//Chromeを起動して、戻るボタンを押しても戻れないようにする。
        //Toast.makeText(this,"InitialActivity",Toast.LENGTH_SHORT).show();
        startActivity(intent);
        finish();//Chromeが起動したら、Activityを終了する。
    }
}
