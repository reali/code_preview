package com.pa3424.app.stabs1.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.pa3424.app.stabs1.R;
import com.pa3424.app.stabs1.fragments.SettingsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BottleSettingsActivity extends AppCompatActivity {

    @BindView(R.id.back) ImageView back;

    @BindView(R.id.change_bottle) View change_bottle;
    @BindView(R.id.fix_ble) View fix_ble;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottle_settings);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#036aab"));
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        change_bottle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChangeBottle();
            }
        });

        fix_ble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFixBle();
            }
        });
    }

    public void openChangeBottle() {

//        YesNoAlertActivity.showOkAlert(BaseNavigationListener.this, "Change water bottle is coming soon");

        Intent intent = new Intent(this, YesNoAlertActivity.class);
        intent.putExtra(YesNoAlertActivity.EXTRA_YES_NO_BTN, true);
        intent.putExtra(YesNoAlertActivity.EXTRA_TITLE, "Change water bottle");
        intent.putExtra(YesNoAlertActivity.EXTRA_QUESTION, "Do you want to unlink bottle and link other?");
        startActivityForResult(intent, YesNoAlertActivity.CHANGE_BOTTLE);

    }

    public void openFixBle() {

//        YesNoAlertActivity.showOkAlert(concreteActivity, "Fix Bluetooth connection is coming soon");

        Intent intent = new Intent(this, YesNoAlertActivity.class);
        intent.putExtra(YesNoAlertActivity.EXTRA_OK_BTN, true);
        intent.putExtra(YesNoAlertActivity.EXTRA_OK_BTN_TITLE, "Fix");
        intent.putExtra(YesNoAlertActivity.EXTRA_TITLE, "Fix Bluetooth connection");
        intent.putExtra(YesNoAlertActivity.EXTRA_QUESTION, "");
        startActivityForResult(intent, YesNoAlertActivity.FIX_BLUETOOTH);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == YesNoAlertActivity.CHANGE_BOTTLE) {

            if (resultCode == RESULT_OK) {
                setResult(SettingsFragment.BOTTLE_CHANGE);
                finish();
            }

        } else if (requestCode == YesNoAlertActivity.FIX_BLUETOOTH && resultCode == RESULT_OK) {
            setResult(SettingsFragment.BOTTLE_FIX_BLE);
            finish();
        }

    }
}
