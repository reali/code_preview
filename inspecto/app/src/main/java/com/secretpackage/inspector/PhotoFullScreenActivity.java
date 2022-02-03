package com.secretpackage.inspector;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

public class PhotoFullScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_full_screen);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String photo = intent.getStringExtra("photo");
        File file = new File(photo);

        if (!file.exists()) {
            finish();
        }

        ImageView myImage = (ImageView) findViewById(R.id.imageView);
        Picasso.with(this)
                .load(file)
                .placeholder(R.mipmap.ic_launcher)
                .into(myImage);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
