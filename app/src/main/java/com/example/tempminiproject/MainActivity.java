package com.example.tempminiproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    Button button;
    ArrayAdapter arrayAdapter;
    ArrayList arrayList;
    EditText editText;
    ArrayList arrayList1;

    Button sendButton;
    TextView msgText;
    final int SEND_SMS_PERMISSION_REQUEST_CODE = 1;

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        button = findViewById(R.id.button);
        arrayList = new ArrayList<String>();
        arrayList1 = new ArrayList<String>();
        editText = findViewById(R.id.editText2);

        sendButton = findViewById(R.id.sendButton);
        msgText = findViewById(R.id.smsText);

        sharedPreferences = getApplicationContext().getSharedPreferences("com.example.tempaddelementlistview", Context.MODE_PRIVATE);

        final HashSet<String> set = (HashSet<String>) sharedPreferences.getStringSet("nums", null);
        if(set == null){
            arrayList.add("NO CONTACTS");
            Toast.makeText(this, "Please add some numbers.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Nope", Toast.LENGTH_SHORT).show();
            arrayList = new ArrayList(set);
        }

        //  Log.i("SET", set.toString());

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_selectable_list_item, arrayList);
        listView.setAdapter(arrayAdapter);

        if(arrayAdapter.getCount() == 0){
            arrayList.add("NO CONTACTS");
        }


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                arrayList.remove(position);
                HashSet<String> set = new HashSet<>(arrayList);
                sharedPreferences.edit().putStringSet("nums", set).apply();

                if(arrayList.isEmpty()){
                    arrayList.add("NO CONTACTS");
                }
                arrayAdapter.notifyDataSetChanged();

                return false;
            }
        });

        //SMS PART

        if(checkPermission(Manifest.permission.SEND_SMS)){
//            button.setEnabled(true);
            Toast.makeText(this, "Send SMS READY", Toast.LENGTH_SHORT).show();

        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
        }

    }

    public void addItems(View view){
        if(editText.getText().length() == 10) {
            // arrayList.add(editText.getText().toString());
            arrayList1.add(editText.getText().toString());

            if(arrayList1.size() > 0){
                if (arrayList.contains(editText.getText().toString())) {
                    Toast.makeText(this, "Same no. exists", Toast.LENGTH_SHORT).show();
                }else {
                    if(arrayList.contains("NO CONTACTS")){
                        arrayList.remove(0);
                    }
                    arrayList.add(editText.getText().toString());
                    editText.setText("");
                    Toast.makeText(this, "Number Added Successfully", Toast.LENGTH_SHORT).show();
                    HashSet<String> set = new HashSet<>(arrayList);
                    sharedPreferences.edit().putStringSet("nums", set).apply();
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        }else {
            Toast.makeText(this, "Number Length is Wrong", Toast.LENGTH_SHORT).show();
        }

    }


    //SMS PART

    public boolean checkPermission(String permission){

        int check = ContextCompat.checkSelfPermission(this,permission);
        return (check == PackageManager.PERMISSION_GRANTED);

    }

    public void onSend(View view){

        ArrayList<String> phoneNUmber = new ArrayList<String>();
        for(int i=0; i<arrayList.size(); i++) {
            phoneNUmber.add(listView.getItemAtPosition(i).toString());
        }
        String message = msgText.getText().toString();

        for(int j=0; j<arrayList.size(); j++) {

            if (phoneNUmber.get(0).equals("NO CONTACTS") || phoneNUmber.get(j) == null || phoneNUmber.get(j).length() == 0 || message == null || message.length() == 0) {

                Toast.makeText(this, "No numbers", Toast.LENGTH_SHORT).show();
                return;
            }

            if (checkPermission(Manifest.permission.SEND_SMS)) {

                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNUmber.get(j), null, message, null, null);
                Toast.makeText(this, "message sent", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permissio denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

