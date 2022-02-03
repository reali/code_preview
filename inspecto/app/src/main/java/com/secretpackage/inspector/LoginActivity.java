package com.secretpackage.inspector;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.android.model.User;
import com.kinvey.android.store.UserStore;
import com.kinvey.java.core.KinveyClientCallback;


public class LoginActivity extends AppCompatActivity {

    private String TAG = "reali";
    private boolean mAuthTask = false;
    Client myKinveyClient;

    TextView username;
    Button logout, go_next;
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle(R.string.title_activity_login);

        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        username = (TextView) findViewById(R.id.tv_username);
        go_next = (Button) findViewById(R.id.btn_next);

        logout = (Button) findViewById(R.id.btn_logout);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mLoginFormView.setVisibility(View.GONE);

        myKinveyClient = ((MyApplication)getApplication()).getKinveyClient();

        logout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                UserStore.logout(myKinveyClient, new KinveyClientCallback<Void>() {

                    @Override
                    public void onSuccess(Void aVoid) {
                        logout.setVisibility(View.GONE);

                        username.setVisibility(View.GONE);
                        go_next.setVisibility(View.GONE);
                        mLoginFormView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e(TAG, "logout fail");
                    }
                });
            }
        });

        go_next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ChooseDBActivity.class));
                finish();
            }
        });

        showProgress(true);

        UserStore.retrieve(myKinveyClient, new KinveyUserCallback<User>() {
            @Override
            public void onFailure(Throwable e) {

                showProgress(false);

                mLoginFormView.setVisibility(View.VISIBLE);
                logout.setVisibility(View.GONE);
            }
            @Override
            public void onSuccess(User user) {

                myKinveyClient.setActiveUser(user);

                showProgress(false);

                mLoginFormView.setVisibility(View.GONE);
                logout.setVisibility(View.VISIBLE);

                username.setText(myKinveyClient.getActiveUser().getUsername());

                username.setVisibility(View.VISIBLE);
                go_next.setVisibility(View.VISIBLE);
            }
        });

    }

    private void attemptLogin() {
        if (mAuthTask) {
            return;
        }

        mEmailView.setError(null);
        mPasswordView.setError(null);

        final String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);

            mAuthTask = true;

            login(email, password);

        }
    }

    private void login(String email, String password) {

        try {

            UserStore.login(email, password, myKinveyClient, new KinveyUserCallback<User>() {
                @Override
                public void onFailure(Throwable t) {

                    mAuthTask = false;
                    showProgress(false);

                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                }

                @Override
                public void onSuccess(User user) {

                    myKinveyClient.setActiveUser(user);

                    mAuthTask = false;
                    showProgress(false);

                    mLoginFormView.setVisibility(View.GONE);
                    logout.setVisibility(View.VISIBLE);

                    username.setText(myKinveyClient.getActiveUser().getUsername());

                    username.setVisibility(View.VISIBLE);
                    go_next.setVisibility(View.VISIBLE);
                }

            });

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}

