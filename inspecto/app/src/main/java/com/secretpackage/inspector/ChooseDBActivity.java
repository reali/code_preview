package com.secretpackage.inspector;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ChooseDBActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_EXT_STORAGE = 1;
    private int dbIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_db);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        String[] arr = new String[]{"Quincy", "Broomfield"};
        String[] arr = new String[]{"Quincy"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arr);

        ListView list = (ListView) findViewById(R.id.listViewDB);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                dbIndex = i;
                checkWriteStoragePermission();
            }
        });

    }

    private void checkWriteStoragePermission() {

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(ChooseDBActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(ChooseDBActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_EXT_STORAGE);

            } else {
                openMainctivity();
            }

        } else {
            openMainctivity();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_EXT_STORAGE: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    openMainctivity();

                }

                return;
            }

        }
    }

    private void openMainctivity() {

        Intent intent = new Intent(ChooseDBActivity.this, MainActivity.class);
        intent.putExtra("dbIndex", dbIndex);
        startActivity(intent);
        finish();

    }

}
