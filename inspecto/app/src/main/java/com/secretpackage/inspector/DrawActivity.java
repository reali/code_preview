package com.secretpackage.inspector;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.secretpackage.inspector.view.LockableScrollView;
import com.agsw.FabricView.FabricView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class DrawActivity extends AppCompatActivity {

    FabricView fv;
    Bitmap bitmap;
    File file;
    Menu menu;
    LockableScrollView scroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        Intent intent = getIntent();
        file = (File)intent.getSerializableExtra("path");

        if (file == null || !file.exists()) {
            finish();
        }

        scroll = (LockableScrollView)findViewById(R.id.scrollView);
        scroll.setScrollingEnabled(false);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        if (bitmap == null) {

            finish();
        }

        fv = (FabricView) findViewById(R.id.faricView);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        if (displayMetrics.widthPixels > 1000) {
            fv.setLayoutParams(new LinearLayout.LayoutParams(1000, 1000));
        } else {
            fv.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, 1000));
        }

        fv.drawImage(0, 0, bitmap.getWidth(), bitmap.getHeight(), bitmap);

//        fv.drawText("test", 100, 100, new Paint());

    }

    private void clear() {
        fv.cleanPage();
        fv.drawImage(0, 0, bitmap.getWidth(), bitmap.getHeight(), bitmap);
    }

    private void save_and_exit() {
        Bitmap obtm = fv.getCanvasBitmap();

        int finWidth = 0;
        int finHeight = 0;

        Log.e("reali", "old " + bitmap.getWidth() + " " + bitmap.getHeight() + " new " + obtm.getWidth() + " " + obtm.getHeight());
        Log.e("reali", "max " + fv.maxTouchX + " " + fv.maxTouchY);

        if (bitmap.getHeight() <= obtm.getHeight()) {
            finHeight = bitmap.getHeight();
        } else {
            finHeight = obtm.getHeight();
        }

        finWidth = bitmap.getWidth();

        if (fv.maxTouchX > finWidth) {
            finWidth = (int)fv.maxTouchX;
        }

        if (fv.maxTouchY > finHeight) {
            finHeight = (int)fv.maxTouchY;
        }

        Log.e("reali", "res " + finWidth + " " + finHeight);

        Bitmap mBit = Bitmap.createBitmap(obtm, 0, 0, finWidth, finHeight);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            mBit.compress(Bitmap.CompressFormat.PNG, 0, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent();
//                intent.putExtra("res", fv.getCanvasBitmap());

        setResult(1212, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.draw_main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            save_and_exit();
            return true;
        } else if (id == R.id.action_clear) {
            clear();
            return true;
        } else if (id == R.id.action_undo) {
            fv.undo();
            return true;
        } else if (id == R.id.action_redo) {
            fv.redo();
            return true;
        } else if (id == R.id.action_paint) {
            menu.getItem(0).setIcon(R.drawable.paint_active);
            menu.getItem(1).setIcon(R.drawable.move);
            fv.setInteractionMode(FabricView.DRAW_MODE);
            scroll.setScrollingEnabled(false);
            return true;
        } else if (id == R.id.action_move) {
            menu.getItem(0).setIcon(R.drawable.paint);
            menu.getItem(1).setIcon(R.drawable.move_active);
            fv.setInteractionMode(FabricView.LOCKED_MODE);
            scroll.setScrollingEnabled(true);
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
