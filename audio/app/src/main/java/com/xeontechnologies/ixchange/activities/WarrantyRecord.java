package com.zitech.audio.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import com.zitech.audio.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WarrantyRecord extends AppCompatActivity {

    @BindView(R.id.listview_warranty_record_devices)
    ListView listView;

    @BindView(R.id.btn_add_model)
    Button btnAddModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warranty_record);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_add_model)void setBtnAddModel(){

    }
}
