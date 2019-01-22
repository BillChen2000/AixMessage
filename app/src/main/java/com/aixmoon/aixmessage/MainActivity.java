package com.aixmoon.aixmessage;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Welcome");
        alert.setMessage("欢迎使用AixMessage，请在各个文本框内输入相应的内容，点击最后的按钮批量发送。一次执行的最大任务数为1000。\n\n在发送过程中，你可以随时选择取消当前任务。\n\n本程序需要短信权限运行。");
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this    ,
                        Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);

                }
                else{
                    Toast.makeText(MainActivity.this, "短信权限已获取", Toast.LENGTH_SHORT).show();
                    //jump();
                }
            }
        });
        alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this    ,
                        Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);

                }
                else {
                    Toast.makeText(MainActivity.this, "短信权限已获取", Toast.LENGTH_SHORT).show();
                    jump();
                }
            }
        }

        );
        alert.show();

        // Example of a call to a native method
      //  TextView tv = (TextView) findViewById(R.id.sample_text);
        //tv.setT
        // ext(stringFromJNI());
    }

    public void requestPer() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);

        }
        else {
            Toast.makeText(MainActivity.this, "短信权限已获取", Toast.LENGTH_SHORT).show();
            jump();
        }
    }

    public void jump(){
        Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
        startActivity(intent);
        finish();
    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                Toast.makeText(MainActivity.this, "短信权限已获取", Toast.LENGTH_SHORT).show();
                jump();
            } else if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(MainActivity.this, "您必须授权短信发送权限才能运行程序", Toast.LENGTH_SHORT).show();
                requestPer();

            }else{
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }

    }



}
