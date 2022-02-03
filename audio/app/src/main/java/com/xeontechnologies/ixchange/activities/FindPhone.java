package com.zitech.audio.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.zitech.audio.R;
import com.zitech.audio.application.audioApplication;
import com.zitech.audio.utils.Consts;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

public class FindPhone extends AppCompatActivity {

    @BindView(R.id.Toolbar_Top)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.switch_alert_preference)
    SwitchCompat switch_alert_preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_phone);
        ButterKnife.bind(this);
        getExtras();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            }
        });
        toolbarTitle.setText(audioApplication.sharedPref.readValue(Consts.CURRENT_DEVICE_NAME_KEY, "audio"));

    }

    private void getExtras() {
        boolean ledState = audioApplication.sharedPref.readValue(Consts.FIND_PHONE_NAME_KEY, false);
        switch_alert_preference.setChecked(ledState);
    }

    @OnCheckedChanged(R.id.switch_alert_preference)void switchAlertPrefStateChange(boolean isChecked){
        audioApplication.sharedPref.writeValue(Consts.FIND_PHONE_NAME_KEY, isChecked);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}

