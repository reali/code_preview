package com.pa3424.app.stabs1.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.pa3424.app.stabs1.R;

public class ChooseNewActivity extends AppCompatActivity {

    public static final int GOTO_LOGIN = 1234;
    public static final int GOTO = 1111;
    public static final int LOGIN = 2222;

    private Button bt_goto_app, bt_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_new);

        bt_goto_app = (Button) findViewById(R.id.bt_goto_app);
        bt_login = (Button) findViewById(R.id.bt_login);

        bt_goto_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(GOTO);
                finish();
            }
        });

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(LOGIN);
                finish();
            }
        });
    }
}
