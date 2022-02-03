package com.secretpackage.inspector;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.secretpackage.inspector.fragment.ExteriorFragment;
import com.secretpackage.inspector.fragment.InteriorFragment;
import com.secretpackage.inspector.fragment.SummaryFragment;
import com.secretpackage.inspector.model.BuildingPermit;
import com.secretpackage.inspector.model.Feature;
import com.secretpackage.inspector.model.NewValue;
import com.secretpackage.inspector.model.Occupancy;
import com.secretpackage.inspector.model.PermitChanges;
import com.secretpackage.inspector.model.PhotoFile;
import com.secretpackage.inspector.model.PropChanges;
import com.secretpackage.inspector.model.Property;
import com.secretpackage.inspector.model.Property2;
import com.secretpackage.inspector.model.Visit;
import com.secretpackage.inspector.util.Constants;
import com.secretpackage.inspector.util.DateUtils;
import com.kinvey.android.Client;
import com.kinvey.android.callback.AsyncDownloaderProgressListener;
import com.kinvey.android.callback.KinveyReadCallback;
import com.kinvey.android.store.DataStore;
import com.kinvey.android.store.FileStore;
import com.kinvey.java.KinveyException;
import com.kinvey.java.Query;
import com.kinvey.java.core.MediaHttpDownloader;
import com.kinvey.java.model.FileMetaData;
import com.kinvey.java.model.KinveyReadResponse;
import com.kinvey.java.store.StoreType;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PropertyDetailsActivity extends AppCompatActivity implements SummaryFragment.OnFragmentInteractionListener {

    public static final int INSPECT = 1411;
    public static final int REVIEW = 1412;
    public static final int CHANGED_PROP_TYPE = 1414;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Client myKinveyClient;

    private Property property1;
    private Property2 property2;

    public PropChanges propCh;

    private int prop_id;
    private String acc_num;
    private int dbIndex;

    public ViewPager photoPager;
    public ViewPager photoPager2;

    public int feature_type;
    Map<String, String> Feature_values;

    public int ext_wall_type;
    public Map<String, String> Ext_wall_values;

    public int style_type;
    public Map<String, String> Style_values;

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    private int REQUEST_CAMERA = 1;

    public View.OnClickListener heatListener;
    public View.OnClickListener fuelListener;
    public View.OnClickListener acListener;

    @BindView(R.id.btn_move_property) Button btn_move_property;
    @BindView(R.id.btn_move_property2) Button btn_move_property2;
    @BindView(R.id.tv_inspector_name) TextView inspector_name;
    @BindView(R.id.tv_property) TextView property_address;
    @BindView(R.id.tv_id) TextView property_id;
    @BindView(R.id.tv_owner) TextView owner_name;
    @BindView(R.id.tv_city) TextView tv_city;
    @BindView(R.id.iv_logo) ImageView iv_logo;
    @BindView(R.id.spinner_building) Spinner spinner_building;
    @BindView(R.id.spinner_section) Spinner spinner_section;

    String buildings[] = new String[]{"Building 1"};
    String sections[] = new String[]{"Section 1", "Section 2"};

    int index_cur_section_selected = 0;

    public File mySketchFile;
    public Bitmap sketchBitmap;
    File mDirectory;
    File mPhotoFile;
    Uri mUri;

    public static List<File> photoInfos;

    String photoHouseId = null, photoSketchId = null;

    public List<String> ext_spec, int_wall, utilitiesList, floors, ext_wall;

    private SummaryFragment summaryFragment;
    private InteriorFragment interiorFragment;
    private ExteriorFragment exteriorFragment;

    public String notes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_details);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        dbIndex = intent.getIntExtra("dbIndex", -1);
        prop_id = intent.getIntExtra("prop_id", -1);
        acc_num = intent.getStringExtra("acc_num");

        if (dbIndex == -1) {
            finish();
        }

        myKinveyClient = ((MyApplication)getApplication()).getKinveyClient();

//        ext_spec = new ArrayList<>();
//        ext_specNew = new ArrayList<>();

        if (dbIndex == 0) {

            if (prop_id != -1) {

                for (PropChanges pch : MainActivity.mListChanges) {
                    if (pch.prop_id == prop_id) {
                        propCh = pch;
                    }
                }

                if (propCh == null) {

                    Query query = myKinveyClient.query().equals("id", prop_id);

                    DataStore<PropChanges> dataCh = DataStore.collection("test", PropChanges.class, StoreType.SYNC, myKinveyClient);

                    try {
                        dataCh.find(query, new KinveyReadCallback<PropChanges>() {
                            @Override
                            public void onSuccess(KinveyReadResponse<PropChanges> kinveyReadResponse) {
                                if (kinveyReadResponse.getResult().size() > 0) {
                                    PropChanges pCh = kinveyReadResponse.getResult().get(0);

                                    Log.e("reali", "PropChanges " + pCh.prop_id);
                                    propCh = pCh;
                                    MainActivity.mListChanges.add(propCh);

                                    viewInit();
                                } else {
                                    propCh = new PropChanges(prop_id);
                                    MainActivity.mListChanges.add(propCh);

                                    viewInit();
                                }
                            }

                            @Override
                            public void onFailure(Throwable throwable) {
                                propCh = new PropChanges(prop_id);
                                MainActivity.mListChanges.add(propCh);

                                viewInit();
                            }
                        });
                    } catch (KinveyException ke){
                        // handle error
                        ke.printStackTrace();
                    }

                } else {
                    viewInit();
                }

            }

        } else if (dbIndex == 1) {
            viewInit();
        }

    }

    private void viewInit() {

        if (dbIndex == 0) {

            if (prop_id == -1) {
                finish();
            }

            for (Property p : MainActivity.mList) {
                if (p.getId() == prop_id) {
                    property1 = p;
                }
            }

            inspector_name.setText(myKinveyClient.getActiveUser().getUsername());
            property_address.setText(property1.getStreet_num() + " " + property1.getStreet_name() + ", "
                    + property1.getTown() + ", " + property1.getState());
            property_id.setText(property1.getId()+"");
            owner_name.setText(property1.getOwners().get(property1.getOwners().size()-1).getName());

            tv_city.setText("City of Quincy");
            Picasso.with(PropertyDetailsActivity.this).load(R.drawable.quincy_logo).fit().into(iv_logo);

            notes = property1.getNotes();

            String resFolderName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/inspecto/";
            mDirectory = new File(resFolderName);
            mDirectory.mkdirs();

            String photoHouse = property1.getId()+"_Photo.jpg";
            String photoSketch = property1.getId()+"_Sketch.jpg";

            for (PhotoFile pf : MainActivity.photoList) {
                if (pf.getName().startsWith(photoHouse)) {
                    photoHouseId = pf.getId();
//                Log.e("relai", pf.getId() + " photoHouse " + pf.getUrl());
                }
                if (pf.getName().startsWith(photoSketch)) {
                    photoSketchId = pf.getId();
//                Log.e("relai", pf.getId() + " photoSketch " + pf.getUrl());
                }
            }

            File myHouseFile = new File(mDirectory, photoHouse);
            mySketchFile = new File(mDirectory, photoSketch);

            photoInfos = new ArrayList<>();

            if (photoHouseId != null) {

                photoInfos.add(myHouseFile);
                getPhoto(myHouseFile, photoHouseId, false);

            }

            if (photoSketchId != null) {
                getPhoto(mySketchFile, photoSketchId, true);
            }

            if (property1.getStatus() == 0) {            //inspect
                btn_move_property.setText("Inspected");
                btn_move_property2.setText("Reviewed");
                btn_move_property2.setVisibility(View.VISIBLE);
            } else if (property1.getStatus() == 1) {     //review
                btn_move_property.setText("Reviewed");
                btn_move_property2.setVisibility(View.GONE);
            } else if (property1.getStatus() == 2) {     //upload
                btn_move_property.setVisibility(View.GONE);
                btn_move_property2.setVisibility(View.GONE);
            }

            btn_move_property.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (property1.getStatus() == 0) {            //inspect

                        if (checkAllParams1()) {

                            Intent intent = new Intent(PropertyDetailsActivity.this, Chooser.class);
                            intent.putExtra("type", Chooser.TYPE_VISIT);
                            intent.putExtra("title", "Please, choose visit type");
                            startActivityForResult(intent, 199);

                        } else {

                            AlertDialog.Builder alert = new AlertDialog.Builder(PropertyDetailsActivity.this);
//                        alert.setTitle("Alert");
                            alert.setMessage("You did not finished inspection. (All check marks should be checked)");
                            alert.setPositiveButton("Ok", null);
                            alert.show();

                        }

                    } else if (property1.getStatus() == 1) {     //review

                        String curVisitCode = property1.getVisits().get(property1.getVisits().size()-1).getVisit_code();

                        AlertDialog.Builder alert = new AlertDialog.Builder(PropertyDetailsActivity.this);
//                    alert.setTitle("By this action you are changing property type. All unsaved changes will be deleted!");
                        alert.setMessage("Is visit type correct? (" + Constants.Visit_values.get(curVisitCode) + ")");

                        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int id) {

                                setResult(REVIEW);
                                property1.setStatus(2);

                                for (int i=0; i<MainActivity.mList.size(); i++) {
                                    if (MainActivity.mList.get(i).getId() == prop_id) {
                                        MainActivity.mList.set(i, property1);
//                                    Log.e("reali", MainActivity.mList.get(i).getStatus() + " stts");
                                    }
                                }

                                finish();

                            }
                        });

                        alert.setNegativeButton("Change", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Intent intent = new Intent(PropertyDetailsActivity.this, Chooser.class);
                                intent.putExtra("type", Chooser.TYPE_VISIT);
                                intent.putExtra("title", "Please, choose visit type");
                                startActivityForResult(intent, 200);

                            }
                        });

                        alert.show();

                    }

                }
            });

            btn_move_property2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (property1.getStatus() == 0) {            //inspect

                        Intent intent = new Intent(PropertyDetailsActivity.this, Chooser.class);
                        intent.putExtra("type", Chooser.TYPE_VISIT);
                        intent.putExtra("title", "Please, choose visit type");
                        startActivityForResult(intent, 201);

                        return;

                    }
                }
            });

            ///////////////////////////////////////////////////////////

            if (property1.prop_type == Constants.PROP_RESIDENTIAL) {
                feature_type = Chooser.TYPE_FEATURES_RES;
                Feature_values = Constants.Feature_res_values;

                ext_wall_type = Chooser.TYPE_EXT_WALL_RES;
                Ext_wall_values = Constants.Ext_Wall_res_values;

                style_type = Chooser.TYPE_STYLE_RES;
                Style_values = Constants.Style_res_values;
            } else if (property1.prop_type == Constants.PROP_COMMERCIAL) {
                feature_type = Chooser.TYPE_FEATURES_COM;
                Feature_values = Constants.Feature_com_values;

                ext_wall_type = Chooser.TYPE_EXT_WALL_COM;
                Ext_wall_values = Constants.Ext_Wall_com_values;

                style_type = Chooser.TYPE_STYLE_COM;
                Style_values = Constants.Style_com_values;
            } else if (property1.prop_type == Constants.PROP_CONDO) {
                feature_type = Chooser.TYPE_FEATURES_COND;
                Feature_values = Constants.Feature_cond_values;

                ext_wall_type = Chooser.TYPE_EXT_WALL_RES;
                Ext_wall_values = Constants.Ext_Wall_res_values;

                style_type = Chooser.TYPE_STYLE_RES;
                Style_values = Constants.Style_res_values;
            } else {

                Feature_values = Constants.Feature_res_values;
                Ext_wall_values = Constants.Ext_Wall_res_values;
                Style_values = Constants.Style_res_values;

            }

            ///////////////////////////////////////////////////////////

            utilitiesList = new ArrayList<>();

            if (propCh.utilitiesListNew == null) {
                utilitiesList = new ArrayList<>();
                propCh.utilitiesListNew = new ArrayList<>();
                for (Occupancy oc : property1.getUtilities()) {
                    utilitiesList.add(oc.getCode() + " - " + Constants.Utility_values.get(oc.getCode()));
                    propCh.utilitiesListNew.add(new NewValue(oc.getCode(), Constants.Utility_values.get(oc.getCode()), false));
                }
            } else {

                for (int i = 0; i < propCh.utilitiesListNew.size(); i++) {
                    if (i < property1.getUtilities().size())  {
                        Occupancy oc = property1.getUtilities().get(i);
                        utilitiesList.add(oc.getCode() + " - " + Constants.Utility_values.get(oc.getCode()));
                    } else {
                        utilitiesList.add(propCh.utilitiesListNew.get(i).value + " - " + propCh.utilitiesListNew.get(i).showValue);
                    }
                }

            }

            int_wall = new ArrayList<>();

            if (propCh.int_wallNew == null) {
                propCh.int_wallNew = new ArrayList<>();
                for (String str : property1.getCard().getInt_wall()) {
                    int_wall.add(str + " - " + Constants.Int_Wall_values.get(str));
                    propCh.int_wallNew.add(new NewValue(str, Constants.Int_Wall_values.get(str), false));
                }
            } else {

                for (int i = 0; i < propCh.int_wallNew.size(); i++) {
                    if (i < property1.getCard().getInt_wall().size())  {
                        String str = property1.getCard().getInt_wall().get(i);
                        int_wall.add(str + " - " + Constants.Int_Wall_values.get(str));
                    } else {
                        int_wall.add(propCh.int_wallNew.get(i).value + " - " + propCh.int_wallNew.get(i).showValue);
                    }
                }

            }

            ext_spec = new ArrayList<>();

            if (propCh.ext_specNew == null) {
                propCh.ext_specNew = new ArrayList<>();
                for (String str : property1.getFeatures()) {
                    ext_spec.add(str + " - " + Feature_values.get(str));
                    propCh.ext_specNew.add(new NewValue(str, Feature_values.get(str), false));
                }
            } else {

                for (int i = 0; i < propCh.ext_specNew.size(); i++) {
                    if (i < property1.getFeatures().size())  {
                        String str = property1.getFeatures().get(i);
                        ext_spec.add(str + " - " + Feature_values.get(str));
                    } else {
                        ext_spec.add(propCh.ext_specNew.get(i).value + " - " + propCh.ext_specNew.get(i).showValue);
                    }
                }

            }

            floors = new ArrayList<>();

            if (propCh.floorsNew == null) {
                propCh.floorsNew = new ArrayList<>();
                for (String str : property1.getCard().getInt_floor()) {
                    floors.add(str + " - " + Constants.Floor_values.get(str));
                    propCh.floorsNew.add(new NewValue(str, Constants.Floor_values.get(str), false));
                }
            } else {

                for (int i = 0; i < propCh.floorsNew.size(); i++) {
                    if (i < property1.getCard().getInt_floor().size())  {
                        String str = property1.getCard().getInt_floor().get(i);
                        floors.add(str + " - " + Constants.Floor_values.get(str));
                    } else {
                        floors.add(propCh.floorsNew.get(i).value + " - " + propCh.floorsNew.get(i).showValue);
                    }
                }

            }

            ext_wall = new ArrayList<>();

            if (propCh.ext_wallNew == null) {
                propCh.ext_wallNew = new ArrayList<>();
                for (String str : property1.getCard().getExt_wall()) {
                    ext_wall.add(str + " - " + Ext_wall_values.get(str));
                    propCh.ext_wallNew.add(new NewValue(str, Ext_wall_values.get(str), false));
                }
            } else {

                for (int i = 0; i < propCh.ext_wallNew.size(); i++) {
                    if (i < property1.getCard().getExt_wall().size())  {
                        String str = property1.getCard().getExt_wall().get(i);
                        ext_wall.add(str + " - " + Ext_wall_values.get(str));
                    } else {
                        ext_wall.add(propCh.ext_wallNew.get(i).value + " - " + propCh.ext_wallNew.get(i).showValue);
                    }
                }

            }

            if (propCh.b_permits == null) {
                propCh.b_permits = new ArrayList<>();
                for (BuildingPermit bp : property1.getB_permits()) {
                    propCh.b_permits.add(new PermitChanges(bp));
                }
            }

            ///////////////////////////////////////////////////////////

            if (property1.prop_type == Constants.PROP_VACANT) {
                summaryFragment = SummaryFragment.newInstance(prop_id);
                interiorFragment = null;
                exteriorFragment = null;

                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), 1);
            } else {
                summaryFragment = SummaryFragment.newInstance(prop_id);
                interiorFragment = InteriorFragment.newInstance(prop_id);
                exteriorFragment = ExteriorFragment.newInstance(prop_id);

                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), 3);
            }

            ///////////////////////////////////////////////////////////

        } else if (dbIndex == 1) {

            if (acc_num == null || acc_num.isEmpty()) {
                finish();
            }

            for (Property2 p : MainActivity.mList2) {
                if (p.getAccount_Number().equals(acc_num)) {
                    property2 = p;
                }
            }

            if (propCh == null) {
                propCh = new PropChanges();
            }

            inspector_name.setText(myKinveyClient.getActiveUser().getUsername());

            if (property2.getSitus_Address() != null && !property2.getSitus_Address().isEmpty()) {
                property_address.setText(property2.getSitus_Address() + ", Broomfield, CO");
            } else {
                property_address.setText("-");
            }

            property_id.setText(property2.getAccount_Number());
            owner_name.setText(property2.getOwner_Name());

            tv_city.setText("City and County of Broomfield");
            Picasso.with(PropertyDetailsActivity.this).load(R.drawable.broomfield_logo).fit().into(iv_logo);

            notes = property2.getNotes();

            String resFolderName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/inspecto/";
            mDirectory = new File(resFolderName);
            mDirectory.mkdirs();

            String photoHouse = property2.getAccount_Number()+"_Photo.jpg";
            String photoSketch = property2.getAccount_Number()+"_Sketch.jpg";

            for (PhotoFile pf : MainActivity.photoList) {
                if (pf.getName().startsWith(photoHouse)) {
                    photoHouseId = pf.getId();
//                Log.e("relai", pf.getId() + " photoHouse " + pf.getUrl());
                }
                if (pf.getName().startsWith(photoSketch)) {
                    photoSketchId = pf.getId();
//                Log.e("relai", pf.getId() + " photoSketch " + pf.getUrl());
                }
            }

            File myHouseFile = new File(mDirectory, photoHouse);
            mySketchFile = new File(mDirectory, photoSketch);

            photoInfos = new ArrayList<>();

            if (photoHouseId != null) {

                photoInfos.add(myHouseFile);
                getPhoto(myHouseFile, photoHouseId, false);

            }

            if (photoSketchId != null) {
                getPhoto(mySketchFile, photoSketchId, true);
            }

            if (property2.getStatus() == 0) {            //inspect
                btn_move_property.setText("Inspected");
                btn_move_property2.setText("Reviewed");
                btn_move_property2.setVisibility(View.VISIBLE);
            } else if (property2.getStatus() == 1) {     //review
                btn_move_property.setText("Reviewed");
                btn_move_property2.setVisibility(View.GONE);
            } else if (property2.getStatus() == 2) {     //upload
                btn_move_property.setVisibility(View.GONE);
                btn_move_property2.setVisibility(View.GONE);
            }

            btn_move_property.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (property2.getStatus() == 0) {            //inspect

//                        if (checkAllParams()) {
//
//                            Intent intent = new Intent(PropertyDetailsActivity.this, Chooser.class);
//                            intent.putExtra("type", Chooser.TYPE_VISIT);
//                            intent.putExtra("title", "Please, choose visit type");
//                            startActivityForResult(intent, 199);
//
//                        } else {
//
//                            AlertDialog.Builder alert = new AlertDialog.Builder(PropertyDetailsActivity.this);
////                        alert.setTitle("Alert");
//                            alert.setMessage("You did not finished inspection. (All check marks should be checked)");
//                            alert.setPositiveButton("Ok", null);
//                            alert.show();
//
//                        }

                    } else if (property2.getStatus() == 1) {     //review

                        String curVisitCode = property2.getVisits().get(property2.getVisits().size()-1).getVisit_code();

                        AlertDialog.Builder alert = new AlertDialog.Builder(PropertyDetailsActivity.this);
//                    alert.setTitle("By this action you are changing property type. All unsaved changes will be deleted!");
                        alert.setMessage("Is visit type correct? (" + Constants.Visit_values.get(curVisitCode) + ")");

                        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int id) {

                                setResult(REVIEW);
                                property2.setStatus(2);

                                for (int i=0; i<MainActivity.mList2.size(); i++) {
                                    if (MainActivity.mList2.get(i).getAccount_Number() == acc_num) {
                                        MainActivity.mList2.set(i, property2);
//                                    Log.e("reali", MainActivity.mList.get(i).getStatus() + " stts");
                                    }
                                }

                                finish();

                            }
                        });

                        alert.setNegativeButton("Change", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Intent intent = new Intent(PropertyDetailsActivity.this, Chooser.class);
                                intent.putExtra("type", Chooser.TYPE_VISIT);
                                intent.putExtra("title", "Please, choose visit type");
                                startActivityForResult(intent, 200);

                            }
                        });

                        alert.show();

                    }

                }
            });

            btn_move_property2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (property2.getStatus() == 0) {            //inspect

                        Intent intent = new Intent(PropertyDetailsActivity.this, Chooser.class);
                        intent.putExtra("type", Chooser.TYPE_VISIT);
                        intent.putExtra("title", "Please, choose visit type");
                        startActivityForResult(intent, 201);

                        return;

                    }
                }
            });

            ///////////////////////////////////////////////////////////

            ext_wall_type = Chooser.TYPE_EXT_WALL_RES;
            Ext_wall_values = Constants.Ext_Wall_res_values;

            ext_wall = new ArrayList<>();
            propCh.ext_wallNew = new ArrayList<>();
            for (String str : property2.getExt_wall()) {
                ext_wall.add(str + " - " + Ext_wall_values.get(str));
                propCh.ext_wallNew.add(new NewValue(str, Ext_wall_values.get(str), false));
            }

            if (property2.prop_type == Constants.PROP_AGRIC) {

                feature_type = Chooser.TYPE_FEATURES_COM;
                Feature_values = Constants.Feature_com_values;

            } else if (property2.prop_type == Constants.PROP_COMM) {

                feature_type = Chooser.TYPE_FEATURES_COM;
                Feature_values = Constants.Feature_com_values;

            } else if (property2.prop_type == Constants.PROP_MULTRES) {

                feature_type = Chooser.TYPE_FEATURES_RES;
                Feature_values = Constants.Feature_res_values;

            } else if (property2.prop_type == Constants.PROP_SFR) {

                feature_type = Chooser.TYPE_FEATURES_RES;
                Feature_values = Constants.Feature_res_values;

            }

//            if (property2.prop_type == Constants.PROP_RESIDENTIAL) {
//                feature_type = Chooser.TYPE_FEATURES_RES;
//                Feature_values = Constants.Feature_res_values;
//
//                ext_wall_type = Chooser.TYPE_EXT_WALL_RES;
//                Ext_wall_values = Constants.Ext_Wall_res_values;
//
//                style_type = Chooser.TYPE_STYLE_RES;
//                Style_values = Constants.Style_res_values;
//            } else if (property2.prop_type == Constants.PROP_COMMERCIAL) {
//                feature_type = Chooser.TYPE_FEATURES_COM;
//                Feature_values = Constants.Feature_com_values;
//
//                ext_wall_type = Chooser.TYPE_EXT_WALL_COM;
//                Ext_wall_values = Constants.Ext_Wall_com_values;
//
//                style_type = Chooser.TYPE_STYLE_COM;
//                Style_values = Constants.Style_com_values;
//            } else if (property2.prop_type == Constants.PROP_CONDO) {
//                feature_type = Chooser.TYPE_FEATURES_COND;
//                Feature_values = Constants.Feature_cond_values;
//
//                ext_wall_type = Chooser.TYPE_EXT_WALL_RES;
//                Ext_wall_values = Constants.Ext_Wall_res_values;
//
//                style_type = Chooser.TYPE_STYLE_RES;
//                Style_values = Constants.Style_res_values;
//            } else {
//
//                Feature_values = Constants.Feature_res_values;
//                Ext_wall_values = Constants.Ext_Wall_res_values;
//                Style_values = Constants.Style_res_values;
//
//            }

            ///////////////////////////////////////////////////////////


            utilitiesList = new ArrayList<>();
            propCh.utilitiesListNew = new ArrayList<>();

            /////

            if (property2.prop_type == Constants.PROP_AGRIC) {
                summaryFragment = SummaryFragment.newInstance(true, acc_num);
                interiorFragment = null;
                exteriorFragment = null;

                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), 1);
            } else if (property2.prop_type == Constants.PROP_MULTRES) {
                summaryFragment = SummaryFragment.newInstance(true, acc_num);
                interiorFragment = null;
                exteriorFragment = ExteriorFragment.newInstance(true, acc_num);

                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), 2);
            } else {
                summaryFragment = SummaryFragment.newInstance(true, acc_num);
                interiorFragment = InteriorFragment.newInstance(true, acc_num);
                exteriorFragment = ExteriorFragment.newInstance(true, acc_num);

                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), 3);
            }

            ///////////////////////////////////////////////////////////

        }

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                Log.e("reali", position + " " + mViewPager.getCurrentItem());

                if (position == 0 && summaryFragment != null) {
                    summaryFragment.et_notes.setText(notes);
                } else if (position == 1 && interiorFragment != null) {
                    interiorFragment.et_notes.setText(notes);
                } else if (position == 2 && exteriorFragment != null) {
                    exteriorFragment.et_notes.setText(notes);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        heatListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (propCh.isHeatTypeChecked) {
                    interiorFragment.bt_change_heat_type.setText("Change");
                    exteriorFragment.bt_change_heat_type.setText("Change");
                    interiorFragment.iv_check_heat_type.setImageResource(R.drawable.check);
                    exteriorFragment.iv_check_heat_type.setImageResource(R.drawable.check);
                    propCh.isHeatTypeChecked = false;
                } else {
                    interiorFragment.bt_change_heat_type.setText(interiorFragment.tv_heat_type.getText());
                    exteriorFragment.bt_change_heat_type.setText(exteriorFragment.tv_heat_type.getText());
                    exteriorFragment.iv_check_heat_type.setImageResource(R.drawable.check_gr);
                    interiorFragment.iv_check_heat_type.setImageResource(R.drawable.check_gr);
                    propCh.isHeatTypeChecked = true;
                    propCh.heatVal = property1.getCard().getHeat_type();
                }
            }
        };

        fuelListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (propCh.isFuelChecked) {
                    interiorFragment.bt_change_heat_fuel.setText("Change");
                    exteriorFragment.bt_change_heat_fuel.setText("Change");
                    interiorFragment.iv_check_fuel.setImageResource(R.drawable.check);
                    exteriorFragment.iv_check_fuel.setImageResource(R.drawable.check);
                    propCh.isFuelChecked = false;
                } else {
                    interiorFragment.bt_change_heat_fuel.setText(interiorFragment.tv_heat_fuel.getText());
                    exteriorFragment.bt_change_heat_fuel.setText(exteriorFragment.tv_heat_fuel.getText());
                    interiorFragment.iv_check_fuel.setImageResource(R.drawable.check_gr);
                    exteriorFragment.iv_check_fuel.setImageResource(R.drawable.check_gr);
                    propCh.isFuelChecked = true;
                    propCh.fuelVal = property1.getCard().getHeat_fuel();
                }
            }
        };

        acListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (propCh.isAcChecked) {
                    interiorFragment.bt_change_ac_type.setText("Change");
                    exteriorFragment.bt_change_ac_type.setText("Change");
                    interiorFragment.iv_check_ac.setImageResource(R.drawable.check);
                    exteriorFragment.iv_check_ac.setImageResource(R.drawable.check);
                    propCh.isAcChecked = false;
                } else {
                    interiorFragment.bt_change_ac_type.setText(interiorFragment.tv_ac_type.getText());
                    exteriorFragment.bt_change_ac_type.setText(exteriorFragment.tv_ac_type.getText());
                    interiorFragment.iv_check_ac.setImageResource(R.drawable.check_gr);
                    exteriorFragment.iv_check_ac.setImageResource(R.drawable.check_gr);
                    propCh.isAcChecked = true;
                    propCh.acVal = property1.getCard().getAc_type();
                }
            }
        };

        ///

        ArrayAdapter adapterSections = new ArrayAdapter<String>(this, R.layout.spinner_item, sections);
        adapterSections.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_section.setAdapter(adapterSections);
        spinner_section.setSelection(0);

        ArrayAdapter adapterBuildings = new ArrayAdapter<String>(this, R.layout.spinner_item, buildings);
        adapterBuildings.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_building.setAdapter(adapterBuildings);
        spinner_building.setSelection(0);

//        spinner_section.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//
//                if (index_cur_section_selected != i) {
//
//                    Log.e("reali", "selected - " + i);
//
//                    if (prop_id == 3341) {
//
//                        interiorFragment = InteriorFragment.newInstance(prop_id+i);
//
//                        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), 3);
//                        mViewPager.setAdapter(mSectionsPagerAdapter);
//                        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//                            @Override
//                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//                            }
//
//                            @Override
//                            public void onPageSelected(int position) {
////                Log.e("reali", position + " " + mViewPager.getCurrentItem());
//
////                                if (position == 0 && summaryFragment != null) {
////                                    summaryFragment.et_notes.setText(notes);
////                                } else if (position == 1 && interiorFragment != null) {
////                                    interiorFragment.et_notes.setText(notes);
////                                } else if (position == 2 && exteriorFragment != null) {
////                                    exteriorFragment.et_notes.setText(notes);
////                                }
//                            }
//
//                            @Override
//                            public void onPageScrollStateChanged(int state) {
//
//                            }
//                        });
//
//                    }
//
//                    index_cur_section_selected = i;
//                }
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//            }
//        });

    }

    private boolean checkAllParams1() {

        if (property1.prop_type == Constants.PROP_RESIDENTIAL) {

            if (propCh.isOccuCodeChecked && propCh.isTopolChecked && propCh.isOccuChecked && propCh.isLocChecked) {

            } else
                return false;

            if (propCh.isKitchenChecked &&
                    propCh.isBathStyleChecked && propCh.isDeprGradeChecked && propCh.isBedroomChecked && propCh.isBathroomChecked &&
                    propCh.isHalfbathChecked && propCh.isOtherRoomChecked && propCh.isFixturesChecked) {

            } else
                return false;

            if (propCh.isHeatTypeChecked && propCh.isFuelChecked && propCh.isAcChecked && propCh.isStyleChecked &&
                    propCh.isGradeChecked && propCh.isStoriesChecked && propCh.isRoofCovTypeChecked && propCh.isRoofStrChecked) {

            } else
                return false;

            ////

            for (NewValue str : propCh.utilitiesListNew) {
                if (!str.selected)
                    return false;
            }

            for (NewValue str : propCh.int_wallNew) {
                if (!str.selected)
                    return false;
            }

            for (NewValue str : propCh.floorsNew) {
                if (!str.selected)
                    return false;
            }

            for (NewValue str : propCh.ext_wallNew) {
                if (!str.selected)
                    return false;
            }

            for (NewValue str : propCh.ext_specNew) {
                if (!str.selected)
                    return false;
            }

            return true;

        } else if (property1.prop_type == Constants.PROP_COMMERCIAL) {

            if (propCh.isOccuCodeChecked && propCh.isTopolChecked && propCh.isLocChecked) {

            } else
                return false;

            if (propCh.isHeatTypeChecked && propCh.isFuelChecked && propCh.isAcChecked &&
                    propCh.isCeilingTypeChecked && propCh.isDeprGradeChecked) {

            } else
                return false;

            if (propCh.isStyleChecked &&
                    propCh.isGradeChecked && propCh.isStoriesChecked && propCh.isRoofCovTypeChecked && propCh.isRoofStrChecked) {

            } else
                return false;

            ////

            for (NewValue str : propCh.utilitiesListNew) {
                if (!str.selected)
                    return false;
            }

            for (NewValue str : propCh.int_wallNew) {
                if (!str.selected)
                    return false;
            }

            for (NewValue str : propCh.floorsNew) {
                if (!str.selected)
                    return false;
            }

            for (NewValue str : propCh.ext_wallNew) {
                if (!str.selected)
                    return false;
            }

            for (NewValue str : propCh.ext_specNew) {
                if (!str.selected)
                    return false;
            }

            return true;

        } else if (property1.prop_type == Constants.PROP_CONDO) {

            if (propCh.isOccuCodeChecked && propCh.isTopolChecked && propCh.isOccuChecked && propCh.isLocChecked) {

            } else
                return false;

            if (propCh.isKitchenChecked && propCh.isBathStyleChecked && propCh.isDeprGradeChecked && propCh.isBedroomChecked &&
                    propCh.isBathroomChecked && propCh.isHalfbathChecked && propCh.isOtherRoomChecked && propCh.isFixturesChecked) {

            } else
                return false;

            if (propCh.isHeatTypeChecked && propCh.isFuelChecked && propCh.isAcChecked && propCh.isStyleChecked &&
                    propCh.isGradeChecked && propCh.isStoriesChecked && propCh.isRoofCovTypeChecked && propCh.isRoofStrChecked) {

            } else
                return false;

            ////

            for (NewValue str : propCh.utilitiesListNew) {
                if (!str.selected)
                    return false;
            }

            for (NewValue str : propCh.int_wallNew) {
                if (!str.selected)
                    return false;
            }

            for (NewValue str : propCh.floorsNew) {
                if (!str.selected)
                    return false;
            }

            for (NewValue str : propCh.ext_wallNew) {
                if (!str.selected)
                    return false;
            }

            for (NewValue str : propCh.ext_specNew) {
                if (!str.selected)
                    return false;
            }

            return true;

        } else if (property1.prop_type == Constants.PROP_VACANT) {

            if (propCh.isOccuCodeChecked && propCh.isTopolChecked && propCh.isLocChecked) {

            } else
                return false;

            for (NewValue str : propCh.utilitiesListNew) {
                if (!str.selected)
                    return false;
            }

            return true;

        }


        return false;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        int count;

        public SectionsPagerAdapter(FragmentManager fm, int count) {
            super(fm);
            this.count = count;
        }

        @Override
        public Fragment getItem(int position) {

            if (count == 3) {
                switch (position) {
                    case 0:
                        return summaryFragment;
                    case 1:
                        return interiorFragment;
                    case 2:
                        return exteriorFragment;
                }
            } else if (count == 2) {
                switch (position) {
                    case 0:
                        return summaryFragment;
                    case 1:
                        return exteriorFragment;
                }
            } else if (count == 1) {
                return summaryFragment;
            }

            return summaryFragment;

        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            if (count == 2) {

                switch (position) {
                    case 0:
                        return "SUMMARY";
                    case 1:
                        return "EXTERIOR";
                }


            } else {

                switch (position) {
                    case 0:
                        return "SUMMARY";
                    case 1:
                        return "INTERIOR";
                    case 2:
                        return "EXTERIOR";
                }

            }

            return null;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1210 && resultCode == 1212) {
//            Bitmap btmp = data.getParcelableExtra("res");
//            sketch.setImageBitmap(btmp);
//            File file = new File(mySketchFile.getAbsolutePath());
//            Picasso.with(getContext()).load(file).resize(0, 400).onlyScaleDown().into(sketch);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            sketchBitmap = BitmapFactory.decodeFile(mySketchFile.getAbsolutePath(), options);

            if (summaryFragment != null && summaryFragment.sketch != null) {
                summaryFragment.sketch.setImageBitmap(sketchBitmap);
            }

            if (interiorFragment != null && interiorFragment.sketch != null) {
                interiorFragment.sketch.setImageBitmap(sketchBitmap);
            }

            if (exteriorFragment != null && exteriorFragment.sketch != null) {
                exteriorFragment.sketch.setImageBitmap(sketchBitmap);
            }

        } else if (requestCode == 69 && resultCode == 1313) {
            int pos = data.getIntExtra("pos", 0);
            int subpos = data.getIntExtra("sub_pos", 0);
            String[] mKeys = Constants.Utility_values.keySet().toArray(new String[Constants.Utility_values.size()]);

            propCh.utilitiesListNew.get(subpos).value = mKeys[pos];
            propCh.utilitiesListNew.get(subpos).showValue = Constants.Utility_values.get(mKeys[pos]);
            propCh.utilitiesListNew.get(subpos).selected= true;
            summaryFragment.utilities_adapter.notifyDataSetChanged();
        } else if (requestCode == 76 && resultCode == 1313) {
            int pos = data.getIntExtra("pos", 0);
            String[] mKeys = Constants.Fuel_Type_values.keySet().toArray(new String[Constants.Fuel_Type_values.size()]);

            interiorFragment.bt_change_heat_fuel.setText(mKeys[pos] + " - " + Constants.Fuel_Type_values.get(mKeys[pos]));
            exteriorFragment.bt_change_heat_fuel.setText(mKeys[pos] + " - " + Constants.Fuel_Type_values.get(mKeys[pos]));

            interiorFragment.iv_check_fuel.setImageResource(R.drawable.check_gr);
            exteriorFragment.iv_check_fuel.setImageResource(R.drawable.check_gr);
            propCh.isFuelChecked = true;
            propCh.fuelVal = mKeys[pos];
        } else if (requestCode == 77 && resultCode == 1313) {
            int pos = data.getIntExtra("pos", 0);
            String[] mKeys = Constants.Heat_Type_values.keySet().toArray(new String[Constants.Heat_Type_values.size()]);

            interiorFragment.bt_change_heat_type.setText(mKeys[pos] + " - " + Constants.Heat_Type_values.get(mKeys[pos]));
            exteriorFragment.bt_change_heat_type.setText(mKeys[pos] + " - " + Constants.Heat_Type_values.get(mKeys[pos]));

            interiorFragment.iv_check_heat_type.setImageResource(R.drawable.check_gr);
            exteriorFragment.iv_check_heat_type.setImageResource(R.drawable.check_gr);
            propCh.isHeatTypeChecked = true;
            propCh.heatVal = mKeys[pos];
        } else if (requestCode == 78 && resultCode == 1313) {
            int pos = data.getIntExtra("pos", 0);
            String[] mKeys = Constants.AC_Type_values.keySet().toArray(new String[Constants.AC_Type_values.size()]);

            interiorFragment.bt_change_ac_type.setText(mKeys[pos] + " - " + Constants.AC_Type_values.get(mKeys[pos]));
            exteriorFragment.bt_change_ac_type.setText(mKeys[pos] + " - " + Constants.AC_Type_values.get(mKeys[pos]));

            interiorFragment.iv_check_ac.setImageResource(R.drawable.check_gr);
            exteriorFragment.iv_check_ac.setImageResource(R.drawable.check_gr);
            propCh.isAcChecked = true;
            propCh.acVal = mKeys[pos];
        } else if (requestCode == 81 && resultCode == 1313) {
            int pos = data.getIntExtra("pos", 0);
            int subpos = data.getIntExtra("sub_pos", 0);
            String[] mKeys = Feature_values.keySet().toArray(new String[Feature_values.size()]);

            propCh.ext_specNew.get(subpos).value = mKeys[pos];
            propCh.ext_specNew.get(subpos).showValue = Feature_values.get(mKeys[pos]);
            propCh.ext_specNew.get(subpos).selected = true;
            update_spec_features_adapters();
        } else if (requestCode == 82 && resultCode == 1313) {
            int pos = data.getIntExtra("pos", 0);
            int subpos = data.getIntExtra("sub_pos", 0);
            String[] mKeys = Ext_wall_values.keySet().toArray(new String[Ext_wall_values.size()]);

            propCh.ext_wallNew.get(subpos).value = mKeys[pos];
            propCh.ext_wallNew.get(subpos).showValue = Ext_wall_values.get(mKeys[pos]);
            propCh.ext_wallNew.get(subpos).selected = true;
            exteriorFragment.ext_wall_adapter.notifyDataSetChanged();
        } else if (requestCode == 79 && resultCode == 1313) {
            int pos = data.getIntExtra("pos", 0);
            String[] mKeys = Feature_values.keySet().toArray(new String[Feature_values.size()]);

            ext_spec.add(mKeys[pos] + " - " + Feature_values.get(mKeys[pos]));
            propCh.ext_specNew.add(new NewValue(mKeys[pos], Feature_values.get(mKeys[pos]), true));
            update_spec_features_adapters();

        } else if (requestCode == 79 && resultCode == 1314) {
            List<Integer> selectedList = data.getIntegerArrayListExtra("selected_list");
            String[] mKeys = Feature_values.keySet().toArray(new String[Feature_values.size()]);

            for (int i=0; i<selectedList.size(); i++) {
                if (selectedList.get(i) == 1) {
                    Log.e("reali", mKeys[i] + "");
                    ext_spec.add(mKeys[i] + " - " + Feature_values.get(mKeys[i]));
                    propCh.ext_specNew.add(new NewValue(mKeys[i], Feature_values.get(mKeys[i]), true));
                }
            }

            update_spec_features_adapters();

        } else if (requestCode == 94 && resultCode == 1313) {
            int pos = data.getIntExtra("pos", 0);
            int subpos = data.getIntExtra("sub_pos", 0);
            String[] mKeys = Constants.Int_Wall_values.keySet().toArray(new String[Constants.Int_Wall_values.size()]);

            propCh.int_wallNew.get(subpos).value = mKeys[pos];
            propCh.int_wallNew.get(subpos).showValue = Constants.Int_Wall_values.get(mKeys[pos]);
            propCh.int_wallNew.get(subpos).selected = true;
            interiorFragment.int_wall_adapter.notifyDataSetChanged();
        } else if (requestCode == 95 && resultCode == 1313) {
            int pos = data.getIntExtra("pos", 0);
            int subpos = data.getIntExtra("sub_pos", 0);
            String[] mKeys = Constants.Floor_values.keySet().toArray(new String[Constants.Floor_values.size()]);

            propCh.floorsNew.get(subpos).value = mKeys[pos];
            propCh.floorsNew.get(subpos).showValue = Constants.Floor_values.get(mKeys[pos]);
            propCh.floorsNew.get(subpos).selected = true;
            interiorFragment.floors_adapter.notifyDataSetChanged();
        } else if (requestCode == 156 && resultCode == 1313) {
            summaryFragment.b_permits_adapter.notifyDataSetChanged();
        } else if (requestCode == 199 && resultCode == 1313) {
            int pos = data.getIntExtra("pos", 0);
            String[] mKeys = Constants.Visit_values.keySet().toArray(new String[Constants.Visit_values.size()]);

            Visit v = new Visit();
            v.setVisit_date(DateUtils.fromDateToString(new Date()));
            v.setVisit_code(mKeys[pos]);
            v.setInspector(myKinveyClient.getActiveUser().getUsername());

            property1.getVisits().add(v);
            property1.setStatus(1);

            for (int i=0; i<MainActivity.mList.size(); i++) {
                if (MainActivity.mList.get(i).getId() == prop_id) {
                    MainActivity.mList.set(i, property1);
//                        Log.e("reali", MainActivity.mList.get(i).getStatus() + " stts");
                }
            }

            setResult(INSPECT);
            finish();
        } else if (requestCode == 200 && resultCode == 1313) {
            int pos = data.getIntExtra("pos", 0);
            String[] mKeys = Constants.Visit_values.keySet().toArray(new String[Constants.Visit_values.size()]);

            property1.getVisits().get(property1.getVisits().size()-1).setVisit_code(mKeys[pos]);
            property1.setStatus(2);

            for (int i=0; i<MainActivity.mList.size(); i++) {
                if (MainActivity.mList.get(i).getId() == prop_id) {
                    MainActivity.mList.set(i, property1);
//                        Log.e("reali", MainActivity.mList.get(i).getStatus() + " stts");
                }
            }

            setResult(REVIEW);
            finish();
        } else if (requestCode == 201 && resultCode == 1313) {
            int pos = data.getIntExtra("pos", 0);
            String[] mKeys = Constants.Visit_values.keySet().toArray(new String[Constants.Visit_values.size()]);

            Visit v = new Visit();
            v.setVisit_date(DateUtils.fromDateToString(new Date()));
            v.setVisit_code(mKeys[pos]);
            v.setInspector(myKinveyClient.getActiveUser().getUsername());

            property1.getVisits().add(v);
            property1.setStatus(2);

            for (int i=0; i<MainActivity.mList.size(); i++) {
                if (MainActivity.mList.get(i).getId() == prop_id) {
                    MainActivity.mList.set(i, property1);
//                        Log.e("reali", MainActivity.mList.get(i).getStatus() + " stts");
                }
            }

            setResult(REVIEW);
            finish();
        } else if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            onCaptureImageResult();
        }
    }

    public void checkCameraPermission() {

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(PropertyDetailsActivity.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(PropertyDetailsActivity.this,
                        new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);

//                if (ActivityCompat.shouldShowRequestPermissionRationale(PropertyDetailsActivity.this, Manifest.permission.CAMERA)) {
//                    //show info why we need it, the request
//                } else {
//                    //request
//                }

            } else {
                cameraIntent();
            }

        } else {
            cameraIntent();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {

                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        permissions[0].equals(Manifest.permission.CAMERA)) {

                    cameraIntent();

                }

                return;
            }

        }
    }

    private void cameraIntent() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, REQUEST_CAMERA);

        mPhotoFile = new File(mDirectory, System.currentTimeMillis() + ".jpg");
        mUri = Uri.fromFile(mPhotoFile);

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mUri);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUEST_CAMERA);

    }

    private void onCaptureImageResult() {

        photoInfos.add(mPhotoFile);

        if (summaryFragment != null) {

            if (summaryFragment.photoViewAdapter != null) {
                summaryFragment.photoViewAdapter.notifyDataSetChanged();
            }

            if (summaryFragment.photoPreviewAdapter != null) {
                summaryFragment.photoPreviewAdapter.notifyDataSetChanged();
            }

            if (photoPager != null) {
                photoPager.setCurrentItem(photoInfos.size()-1);
            }

        }

        if (exteriorFragment != null) {

            if (exteriorFragment != null && exteriorFragment.photoViewAdapter != null) {
                exteriorFragment.photoViewAdapter.notifyDataSetChanged();
            }

            if (exteriorFragment != null && exteriorFragment.photoPreviewAdapter != null) {
                exteriorFragment.photoPreviewAdapter.notifyDataSetChanged();
            }

            if (photoPager2 != null) {
                photoPager2.setCurrentItem(photoInfos.size()-1);
            }

        }

//        imgSignupUserProfile.setImageBitmap(thumbnail);

    }

    public void update_spec_features_adapters() {

        if (summaryFragment != null && summaryFragment.ext_spec_features_adapter != null) {
            summaryFragment.ext_spec_features_adapter.notifyDataSetChanged();
        }

        if (interiorFragment != null && interiorFragment.ext_spec_features_adapter != null) {
            interiorFragment.ext_spec_features_adapter.notifyDataSetChanged();
        }

        if (exteriorFragment != null && exteriorFragment.ext_spec_features_adapter != null) {
            exteriorFragment.ext_spec_features_adapter.notifyDataSetChanged();
        }

    }

    private void getPhoto(final File myFile, String fileId, final boolean isSketch) {

        Log.e("relai", " getPhoto " +myFile.getAbsolutePath());

        if (!myFile.exists()) {
            FileOutputStream fos= null;
            try {
                fos = new FileOutputStream(myFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            FileMetaData fileMetadata = new FileMetaData(fileId);

            FileStore fs = myKinveyClient.getFileStore(StoreType.CACHE);

            try {
                fs.download(fileMetadata, fos, new AsyncDownloaderProgressListener<FileMetaData>() {
                    @Override
                    public void onSuccess(FileMetaData fileMetaData) {
                        Log.i("reali", "File download succeeded. " + myFile.getAbsolutePath());

                        if (summaryFragment != null) {

                            if (isSketch) {
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                                sketchBitmap = BitmapFactory.decodeFile(mySketchFile.getAbsolutePath(), options);

                                summaryFragment.sketch.setImageBitmap(sketchBitmap);

                                if (summaryFragment.sketch != null) {
                                    summaryFragment.sketch.setImageBitmap(sketchBitmap);
                                }

                                if (interiorFragment != null && interiorFragment.sketch != null) {
                                    interiorFragment.sketch.setImageBitmap(sketchBitmap);
                                }

                                if (exteriorFragment != null && exteriorFragment.sketch != null) {
                                    exteriorFragment.sketch.setImageBitmap(sketchBitmap);
                                }

                            } else {
                                summaryFragment.photoViewAdapter.notifyDataSetChanged();
                                summaryFragment.photoPreviewAdapter.notifyDataSetChanged();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("reali", "File download failed " + throwable.getMessage());
                    }

                    @Override
                    public void progressChanged(MediaHttpDownloader mediaHttpDownloader) throws java.io.IOException {
                        Log.i("reali", "File download progressChanged. " + mediaHttpDownloader.getProgress() + " " + mediaHttpDownloader.getNumBytesDownloaded() + " " + mediaHttpDownloader.getChunkSize());
                    }

                    @Override
                    public void onCancelled() {
                        Log.i("reali", "File download onCancelled.");
                    }

                    @Override
                    public boolean isCancelled() {
                        return false;
                    }
                });
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }

    }

//    public void getPhoto(final File myFile, String fileId, final ImageView iv) {
//
//        if (myFile.exists()) {
//            Picasso.with(getContext()).load(myFile).resize(0, 400).onlyScaleDown().into(iv);
//        } else {
//            FileOutputStream fos= null;
//            try {
//                fos = new FileOutputStream(myFile);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//
//            FileMetaData fileMetadata = new FileMetaData(fileId);
//
//            FileStore fs = myKinveyClient.getFileStore(StoreType.CACHE);
//
//            try {
//                fs.download(fileMetadata, fos, new AsyncDownloaderProgressListener<FileMetaData>() {
//                    @Override
//                    public void onSuccess(FileMetaData fileMetaData) {
//                        Log.i("reali", "File download succeeded. " + fileMetaData.getId());
//
//                        Picasso.with(getContext()).load(myFile).resize(0, 400).onlyScaleDown().into(iv);
//                    }
//
//                    @Override
//                    public void onFailure(Throwable throwable) {
//                        Log.e("reali", "File download failed " + throwable.getMessage());
//                    }
//
//                    @Override
//                    public void progressChanged(MediaHttpDownloader mediaHttpDownloader) throws java.io.IOException {
//                        Log.i("reali", "File download progressChanged. " + mediaHttpDownloader.getProgress() + " " + mediaHttpDownloader.getNumBytesDownloaded() + " " + mediaHttpDownloader.getChunkSize());
//                    }
//
//                    @Override
//                    public void onCancelled() {
//                        Log.i("reali", "File download onCancelled.");
//                    }
//
//                    @Override
//                    public boolean isCancelled() {
//                        return false;
//                    }
//                });
//            } catch (java.io.IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
}
