package com.pa3424.app.stabs1.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by ali on 10/14/20.
 */

public class GoogleFitUtils {

    public static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 920;


    public static void insertWaterIncome(float valLitres, Context ctx) {

        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_HYDRATION, FitnessOptions.ACCESS_WRITE)
                .build();
//
        if (GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(ctx), fitnessOptions)) {

            DataSource hydrationSource = new DataSource.Builder()
                    .setAppPackageName(ctx)
                    .setDataType(DataType.TYPE_HYDRATION)
                    .setType(DataSource.TYPE_RAW)
                    .build();

            Calendar cal = Calendar.getInstance();

            DataPoint hydration = DataPoint.create(hydrationSource);
            hydration.setTimestamp(cal.getTimeInMillis(), TimeUnit.MILLISECONDS);
            hydration.getValue(Field.FIELD_VOLUME).setFloat(valLitres);

            DataSet dataSet = DataSet.create(hydrationSource);
            dataSet.add(hydration);

            //

            Fitness.getHistoryClient(ctx, GoogleSignIn.getLastSignedInAccount(ctx))
                    .insertData(dataSet)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.e("reali", "google fit insert data onComplete");
                        }
                    });

        }

    }

    public static void accessGoogleFit(Activity activity) {

        Fitness.getHistoryClient(activity, GoogleSignIn.getLastSignedInAccount(activity))
                .readDailyTotal(DataType.TYPE_HYDRATION)
//                .readData(readRequest)
                .addOnSuccessListener(new OnSuccessListener<DataSet>() {
                    @Override
                    public void onSuccess(DataSet dataSet) {
                        Log.d("reali", "onSuccess() ");

                        List<DataPoint> points = dataSet.getDataPoints();

                        Log.e("reali", "points " + points.size());

                        if (points.size() > 0) {
                            Log.e("reaali", "point " + points.get(0).getValue(Field.FIELD_VOLUME).asFloat());
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("reali", "onFailure()", e);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DataSet>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSet> task) {
                        Log.d("reali", "onComplete()");
                    }
                });

    }

    public static void logIn(Activity activity) {

        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_HYDRATION, FitnessOptions.ACCESS_WRITE)
//                        .addDataType(DataType.AGGREGATE_HYDRATION, FitnessOptions.ACCESS_WRITE)
                .build();

        GoogleSignIn.requestPermissions(
                activity, // your activity
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                GoogleSignIn.getLastSignedInAccount(activity),
                fitnessOptions);
    }



    public static boolean isLogged(Activity activity) {

        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_HYDRATION, FitnessOptions.ACCESS_WRITE)
//                        .addDataType(DataType.AGGREGATE_HYDRATION, FitnessOptions.ACCESS_WRITE)
                .build();
//
        if (GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(activity), fitnessOptions)) {
            return true;
        } else {
            return false;
        }

    }
}
