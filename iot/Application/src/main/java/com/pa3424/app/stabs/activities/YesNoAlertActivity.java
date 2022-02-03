package com.pa3424.app.stabs1.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pa3424.app.stabs1.R;

public class YesNoAlertActivity extends AppCompatActivity {

    public static final int SIGN_OUT = 1111;
    public static final int CHANGE_BOTTLE = 1212;
    public static final int FACTORY_RESET = 1313;
    public static final int FIX_BLUETOOTH = 1414;
    public static final int RECALIBRATE = 1515;

    public static final String EXTRA_YES_NO_BTN = "yes_no_btn";
    public static final String EXTRA_OK_BTN = "ok_btn";
    public static final String EXTRA_OK_BTN_TITLE = "title_ok_btn";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_QUESTION = "question";

    private String title, question, title_ok_btn;
    private boolean isYesNo;

    private TextView tv_title, tv_question;
    private Button bt_yes, bt_no, bt_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yes_no_alert);

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getBoolean(EXTRA_YES_NO_BTN, false)) {
                isYesNo = true;
            } else if (getIntent().getExtras().getBoolean(EXTRA_OK_BTN, false)) {
                isYesNo = false;
            }

            if (getIntent().getExtras().containsKey(EXTRA_TITLE)) {
                title = getIntent().getExtras().getString(EXTRA_TITLE, "");
            }

            if (getIntent().getExtras().containsKey(EXTRA_QUESTION)) {
                question = getIntent().getExtras().getString(EXTRA_QUESTION, "");
            }

            if (getIntent().getExtras().containsKey(EXTRA_OK_BTN_TITLE)) {
                title_ok_btn = getIntent().getExtras().getString(EXTRA_OK_BTN_TITLE, "");
            }
        }

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_question = (TextView) findViewById(R.id.tv_question);
        bt_yes = (Button) findViewById(R.id.button_yes);
        bt_no = (Button) findViewById(R.id.button_no);
        bt_ok = (Button) findViewById(R.id.button_ok);

        if (isYesNo) {
            bt_no.setVisibility(View.VISIBLE);
            bt_yes.setVisibility(View.VISIBLE);
            bt_ok.setVisibility(View.GONE);
        } else {
            bt_no.setVisibility(View.GONE);
            bt_yes.setVisibility(View.GONE);
            bt_ok.setVisibility(View.VISIBLE);
            bt_ok.setText(title_ok_btn);
        }

        tv_title.setText(title);
        tv_question.setText(question);

        if (question.isEmpty()) {
            tv_question.setVisibility(View.GONE);
        }

        bt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });

        bt_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    public static void showOkAlert(Activity ctx, String title) {
        Intent intent = new Intent(ctx, YesNoAlertActivity.class);
        intent.putExtra(YesNoAlertActivity.EXTRA_OK_BTN, true);
        intent.putExtra(YesNoAlertActivity.EXTRA_OK_BTN_TITLE, "Ok");
        intent.putExtra(YesNoAlertActivity.EXTRA_TITLE, title);
        intent.putExtra(YesNoAlertActivity.EXTRA_QUESTION, "");
        ctx.startActivity(intent);
    }
}
