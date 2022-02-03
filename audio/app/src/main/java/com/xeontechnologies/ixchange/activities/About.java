package com.zitech.audio.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zitech.audio.R;
import com.zitech.audio.application.audioApplication;
import com.zitech.audio.utils.Consts;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class About extends AppCompatActivity {

    @BindView(R.id.Toolbar_Top)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.btn_contact_us)
    Button btnContactUs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbarTitle.setText(audioApplication.sharedPref.readValue(Consts.CURRENT_DEVICE_NAME_KEY, "audio"));
    }

    @OnClick(R.id.btn_contact_us)void setBtnContactUs(){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.ce.com.hk/contact.html"));
        startActivity(browserIntent);
    }
}
