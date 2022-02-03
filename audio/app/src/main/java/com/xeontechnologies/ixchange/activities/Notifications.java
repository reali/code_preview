package com.zitech.audio.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.zitech.audio.R;
import com.zitech.audio.application.audioApplication;
import com.zitech.audio.utils.Consts;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class Notifications extends AppCompatActivity {

    @BindView(R.id.Toolbar_Top)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.switch_all_push_notifications)
    SwitchCompat switchAllPushNotifications;

    @BindView(R.id.checkbox_battery_notifications)
    CheckBox checkBoxBatteryNotifications;

    @BindView(R.id.checkbox_activity_promotion)
    CheckBox checkBoxActivityPromotions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        ButterKnife.bind(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbarTitle.setText(audioApplication.sharedPref.readValue(Consts.CURRENT_DEVICE_NAME_KEY, "audio"));
    }

}
