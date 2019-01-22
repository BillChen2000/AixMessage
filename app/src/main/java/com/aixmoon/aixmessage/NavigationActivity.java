package com.aixmoon.aixmessage;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    int stopflag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action:)", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //Main
        final AlertDialog.Builder alert = new AlertDialog.Builder(NavigationActivity.this);
        alert.setTitle("Welcome");
        alert.setMessage("欢迎使用AixMessage，请在各个文本框内输入相应的内容，点击最后的按钮批量发送。一次执行的最大任务数为1000。\n\n在发送过程中，你可以随时选择取消当前任务。\n\n本程序需要短信权限运行，请在接下来的对话框中选择允许。");
        alert.setNegativeButton("OK", null);
        final Button btnSend = (Button) findViewById(R.id.buttonSend);
        final EditText txtPhone = (EditText) findViewById(R.id.textPhone);
        final EditText txtName = (EditText) findViewById(R.id.textName);
        final EditText txtMessage = (EditText) findViewById(R.id.textMessage);
        // Toast.makeText(NavigationActivity.this,"no"+Phones[2]+"here",Toast.LENGTH_SHORT).show();
        btnSend.setOnClickListener(new View.OnClickListener() {
            String[] Phones = new String[1000];
            String[] Names = new String[1000];
            String[] Messages = new String[1000];

            public void onClick(View view) {
                if (btnSend.getText().toString() == "中止") {
                    stopflag = 1;
                    btnSend.setClickable(false);
                    Toast.makeText(NavigationActivity.this, "完成当前短信任务后将中止", Toast.LENGTH_SHORT).show();
                }
                else{
                    int countPhone = 0, countName = 0;
                    countPhone = txtPhone.getLineCount();
                    countName = txtName.getLineCount();
                    if (countPhone != countName) {
                        alert.setTitle("Warning");
                        alert.setMessage("输入的电话号码数（" + countPhone + "个）和输入的名字数（" + countName + "个）不匹配，请重试。");
                        alert.show();
                    } else if (txtPhone.length() == 0 || txtName.length() == 0 || txtMessage.length() == 0) {
                        alert.setTitle("Warning");
                        alert.setMessage("请至少输入一组电话号码。");
                        alert.show();
                    } else if (countPhone >= 1000) {
                        alert.setMessage("任务数量（" + countPhone + "）过大，请重试。");
                    } else {
                        send(countPhone);
                    }
                }

                //       Toast.makeText(NavigationActivity.this,"匿名类测试！",Toast.LENGTH_SHORT).show();
            }

            public void send(int count) {
                int i;

                String OriMessages = new String();
                Phones = txtPhone.getText().toString().split("\n");
                Names = txtName.getText().toString().split("\n");
                OriMessages = txtMessage.getText().toString();
                for (i = 0; i < count; i++) {
                    Messages[i] = OriMessages.replace("@name", Names[i].toString());
                    Messages[i] = Messages[i].replace("＠name", Names[i].toString());
                    Messages[i] = Messages[i].replace("@phone", Phones[i].toString());
                    Messages[i] = Messages[i].replace("＠phone", Phones[i].toString());
                }
                AlertDialog.Builder alert2 = new AlertDialog.Builder(NavigationActivity.this);
                alert2.setTitle("提示");
                int perlen = Messages[0].length() / 69 + 1;
                alert2.setMessage("您即将发送" + count + "条短信，由于系统短信长度的限制，每条短信将被分为" +
                        perlen + "条发送。其中第一条完整短信的内容如下：\n\n" + Messages[0] + "\n\n确认发送吗？");
                alert2.setNegativeButton("取消", null);
                alert2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btnSend.setText("中止");
                        final int count = txtPhone.getLineCount();

                        final TextView textStatus = (TextView) findViewById(R.id.textStatus);
                        final ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar);
                        final ProgressBar progressCircle = (ProgressBar) findViewById(R.id.progessCircle);
                        progress.setVisibility(View.VISIBLE);
                        progressCircle.setVisibility(View.VISIBLE);
                        progress.setMax(count);
                        final SmsManager sm = SmsManager.getDefault();
                        textStatus.setText("");
                        new Thread(new Runnable() {

                            public void run() {
                                int i;
                                for (i = 0; i < count; i++) {
                                    if (stopflag == 1) break;
                                    SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date mydate = new Date(System.currentTimeMillis());
                                    progress.setProgress(i);
                                    int status = i + 1;
                                    textStatus.setText("正在发送第" + status + "条短信，共" + count + "条。\n\n正在进行：\n\n收件人：" +Names[i]+ "（"+Phones[i] + "）\n内容：\n" + Messages[i] + "\n\n时间：" + sd.format(mydate));

                                    ArrayList<String> sms = sm.divideMessage(Messages[i]);
                                   // for (String smslist : sms) {
                                  //      sm.sendTextMessage(Phones[i].toString(), null, Messages[i], null, null);
                                        sm.sendMultipartTextMessage(Phones[i].toString(),null, sms ,null,null);
                                        try {
                                            Thread.sleep(5000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }



                                 //   }

                                }
                                if (stopflag == 1) {
                                    progressCircle.setVisibility(View.INVISIBLE);
                                    //                        progress.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));

                                    textStatus.setText("已中止。在中止前已经完成了" + i + "项任务\n\n最后一条短信发送给了"+Names[i-1]+"（"+Phones[i-1]+"）");
                                    btnSend.setClickable(true);
                                    btnSend.setText("预览与发送");
                                    stopflag=0;
                                } else {
                                    progressCircle.setVisibility(View.INVISIBLE);
                                    progress.setProgress(count);

                                    textStatus.setText("已完成，共进行了" + i + "项任务");
                                    stopflag=0;
                                    btnSend.setClickable(true);
                                    btnSend.setText("预览与发送");
                                }
                            }
                        }).start();

                    }
                });
                alert2.show();


            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_status) {
            // Handle the camera action
        } else if (id == R.id.nav_tasks) {

        } else if (id == R.id.nav_tools) {


        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_about) {
            Toast.makeText(NavigationActivity.this, "Brought to you sincerely, \nBill Chen.", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
