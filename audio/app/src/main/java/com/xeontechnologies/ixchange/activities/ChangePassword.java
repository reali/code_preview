package com.zitech.audio.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zitech.audio.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePassword extends AppCompatActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @BindView(R.id.et_change_password_current)
    EditText etCurrentPassword;

    @BindView(R.id.et_change_password_new_password)
    EditText etNewPassword;

    @BindView(R.id.et_change_password_confirm_password)
    EditText etConfirmNewPassword;

    @BindView(R.id.btn_change_password_submit)
    Button btnSubmitChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_change_password_submit)void setSubmitButton(){

    }
}
