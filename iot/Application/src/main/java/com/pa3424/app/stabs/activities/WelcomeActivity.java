package com.pa3424.app.stabs1.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import com.pa3424.app.stabs1.R;

/**
 * Activity which displays a registration screen to the user.
 */
public class WelcomeActivity extends Activity {

    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        progress = (ProgressBar) findViewById(R.id.progressBar2);
        progress.setVisibility(View.VISIBLE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                progress.setVisibility(View.GONE);
                startActivity(new Intent(WelcomeActivity.this, SetupActivity.class));
                finish();

            }
        }, 1500);

//        final Button loginButton = (Button) findViewById(R.id.login_button);
//        loginButton.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                // Starts an intent of the log in activity
//                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
//            }
//        });
//
//        // Sign up button click handler
//        Button signupButton = (Button) findViewById(R.id.signup_button);
//        signupButton.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                // Starts an intent for the sign up activity
//                startActivity(new Intent(WelcomeActivity.this, SignUpActivity.class));
//            }
//        });

        // Add code to print out the key hash
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.pa3424.app.stabs1",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
    }
}
