package com.secretpackage.inspector;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.secretpackage.inspector.fragment.ProperiesFragment;
import com.secretpackage.inspector.model.PhotoFile;
import com.secretpackage.inspector.model.PropChanges;
import com.secretpackage.inspector.model.Property;
import com.secretpackage.inspector.model.Property2;
import com.secretpackage.inspector.util.Constants;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyCountCallback;
import com.kinvey.android.callback.KinveyReadCallback;
import com.kinvey.android.store.DataStore;
import com.kinvey.android.store.FileStore;
import com.kinvey.android.sync.KinveyPushResponse;
import com.kinvey.android.sync.KinveySyncCallback;
import com.kinvey.java.Query;
import com.kinvey.java.core.KinveyClientCallback;
import com.kinvey.java.model.FileMetaData;
import com.kinvey.java.model.KinveyPullResponse;
import com.kinvey.java.model.KinveyReadResponse;
import com.kinvey.java.query.AbstractQuery;
import com.kinvey.java.store.StoreType;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ProperiesFragment.OnListFragmentInteractionListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    TabLayout tabLayout;

    private Client myKinveyClient;

    ProperiesFragment inspect, review, upload;

    public static List<Property> mList = new ArrayList<>();
    public static List<PropChanges> mListChanges = new ArrayList<>();
    public static List<PhotoFile> photoList = new ArrayList<>();

    public static List<Property2> mList2 = new ArrayList<>();

    private ProgressDialog progress;
    private Button bt_reviewed;
    private SearchView searchView;

    private int dbIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        dbIndex = intent.getIntExtra("dbIndex", -1);

        if (dbIndex == -1) {
            finish();
        }

        mViewPager = (ViewPager) findViewById(R.id.container);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        inspect = ProperiesFragment.newInstance(0, dbIndex);
        review = ProperiesFragment.newInstance(1, dbIndex);
        upload = ProperiesFragment.newInstance(2, dbIndex);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        myKinveyClient = ((MyApplication)getApplication()).getKinveyClient();

        final TextView inspector_name = (TextView) findViewById(R.id.tv_inspector_name);
        inspector_name.setText(myKinveyClient.getActiveUser().getUsername());

        TextView tv_city = (TextView) findViewById(R.id.tv_city);
        ImageView iv_logo = (ImageView) findViewById(R.id.iv_logo);

        if (dbIndex == 0) {
            tv_city.setText("City of\nQuincy");
            Picasso.with(MainActivity.this).load(R.drawable.quincy_logo).fit().into(iv_logo);
        } else if (dbIndex == 1) {
            tv_city.setText("City and County\nof Broomfield");
            Picasso.with(MainActivity.this).load(R.drawable.broomfield_logo).fit().into(iv_logo);
        }

        bt_reviewed = (Button) findViewById(R.id.bt_reviewed);
        bt_reviewed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                multiReview();
            }
        });

        searchView = (SearchView) findViewById(R.id.searchView);

        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(Color.WHITE);
        searchAutoComplete.setBackgroundResource(R.drawable.divider);
        searchAutoComplete.setHighlightColor(Color.WHITE);
        searchAutoComplete.setFadingEdgeLength(10);
        searchAutoComplete.setHint("Property ID, street");
        searchAutoComplete.setHintTextColor(Color.parseColor("#AAFFFFFF"));
        searchAutoComplete.setTextSize(14);

//        searchView.setIconifiedByDefault(false);

        if (dbIndex == 0) {

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
//                Log.d(TAG, "######## " + query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    List<Property> searchResults = new ArrayList<Property>();

                    if (newText.length() > 0) {
                        Log.d("reali", "@@@@@@@@ " + newText);

                        for (Property p: mList) {
                            if (p.getStreet_name().toLowerCase().contains(newText.toLowerCase())) {
                                searchResults.add(p);
                            }
                        }

                        for (Property p: mList) {
                            if ((p.getId()+"").contains(newText)) {
                                searchResults.add(p);
                            }
                        }

                        if (inspect != null && inspect.adapter != null) {

                            inspect.showSearchResults(searchResults);
                        }

                    } else {
                        if (inspect != null && inspect.adapter != null) {

                            inspect.recalc();
                        }
                    }

                    return false;
                }
            });

        } else if (dbIndex == 1) {

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
//                Log.d(TAG, "######## " + query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    List<Property2> searchResults = new ArrayList<Property2>();

                    if (newText.length() > 0) {
                        Log.d("reali", "@@@@@@@@ " + newText);

                        for (Property2 p: mList2) {
                            if (p.getSitus_Address().toLowerCase().contains(newText.toLowerCase())) {
                                searchResults.add(p);
                            }
                        }

                        for (Property2 p: mList2) {
                            if (p.getAccount_Number().toLowerCase().contains(newText.toLowerCase())) {
                                searchResults.add(p);
                            }
                        }

                        if (inspect != null && inspect.adapter != null) {

                            inspect.showSearchResults(true, searchResults);
                        }

                    } else {
                        if (inspect != null && inspect.adapter != null) {

                            inspect.recalc();
                        }
                    }

                    return false;
                }
            });

        }

        String resFolderName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/inspecto/";
        File mDirectory = new File(resFolderName);
        mDirectory.mkdirs();

//        Query query = myKinveyClient.query().equals("", "").setSkip(0).setLimit(10);

        progress = ProgressDialog.show(this, "Information window",
                "Fetching newest data", true);

        if (dbIndex == 0) {
            findQuincy();
        } else if (dbIndex == 1) {
            findQuincyMini();
//            findBroomfield();
        }

        fileStoreFind();

//        dataStore.pull(query, new KinveyPullCallback<Property>() {
//            @Override
//            public void onSuccess(KinveyPullResponse<Property> kinveyPullResponse) {
//                Log.e("reali", "pull Property onSuccess");
//                progress.dismiss();
//
//                for (Property p : kinveyPullResponse.getResult()) {
//                    Log.e("reali", p.getId() + " _ " + p.getStreet_num() + " _ " + p.getMap_id() + " _ " + p.getProperty_type());
//                }
//
//                find();
//
//            }
//
//            @Override
//            public void onFailure(Throwable throwable) {
//                Log.e("reali", "pull Property onFailure");
//                progress.dismiss();
//            }
//        });

    }

    public void showReviewedBtn(boolean show) {
        if (show) {
            bt_reviewed.setVisibility(View.VISIBLE);
        } else {
            bt_reviewed.setVisibility(View.GONE);
        }
    }

    private void multiReview() {

        for (Property p : review.getSelectedList()) {

            for (Property pr : mList) {

                if (pr.getId() == p.getId()) {

                    pr.setStatus(2);
                    break;
                }
            }

        }

        review.recalc();
        upload.recalc();
        mViewPager.setCurrentItem(2, true);

        showReviewedBtn(false);

    }

    private void findQuincy() {

        Log.e("reali", "find Quincy");

        final Query query = myKinveyClient.query().addSort("id", AbstractQuery.SortOrder.ASC); //in("id", new String[]{"3347", "3348", "3349", "3350"}).

//        Query query = myKinveyClient.query().addSort("id", AbstractQuery.SortOrder.ASC).setSkip(7).setLimit(4);

        DataStore<Property> dataStore = DataStore.collection("quincy", Property.class, StoreType.SYNC, myKinveyClient);

        dataStore.count(new KinveyCountCallback() {
            @Override
            public void onSuccess(Integer integer) {
                Log.e("reali", "find Property " + integer);

                if (integer > 0) {
                    getProps(query, StoreType.SYNC);
                } else {
                    getProps(query, StoreType.CACHE);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("reali", "find Quincy");
            }
        });
    }

    private void getProps(Query query, StoreType storeType) {

        DataStore<Property> dataStore = DataStore.collection("quincy", Property.class, storeType, myKinveyClient);

        long syncStoreCount = dataStore.syncCount();
        Log.e("reali", "syncStoreCount" + syncStoreCount);

        dataStore.find(query, new KinveyReadCallback<Property>() {
            @Override
            public void onSuccess(KinveyReadResponse<Property> kinveyReadResponse) {

                List<Property> list = kinveyReadResponse.getResult();

                Log.e("reali", "find Quincy " + list.size());
                progress.dismiss();

//                for (Property p : list) {
//                    Log.e("reali", p.getId() + " _ " + p.getOccupancy().getCode() + " _ " + p.getCard().getOccupancy_code());
//                }

//                for (Property p : list) {
//
//                    Log.e("reali", p.getId() + " _ " + p.getNotes());
//
//                    Log.e("reali", p.getId() + " _ " + p.getStreet_name() + " _ " + p.getStreet_num() + " _ " + p.getOccupancy().getCode() + " _ " + p.getLocation().getCode() + " _ " + p.getTopology().getCode());
//
//                    for (Owner o : p.getOwners()) {
//                        Log.e("reali", "----- " + o.getName() + " _ " + o.getAddress());
//                    }
//
//                    for (BuildingPermit bp : p.getB_permits()) {
//                        Log.e("reali", "***** " + bp.getPermit_type() + " _ " + bp.getAmount());
//                    }
//
//                    for (Feature f : p.getFeatures()) {
//                        Log.e("reali", "_____ " + f.getDownload_date() + " _ " + f.getFeature_old());
//                    }
//
//                    for (Occupancy o : p.getUtilities()) {
//                        Log.e("reali", "+++++ " + o.getDownload_date() + " _ " + o.getCode());
//                    }
//
//                    for (Visit v : p.getVisits()) {
//                        Log.e("reali", "%%%%% " + v.getInspector() + " _ " + v.getVisit_code());
//                    }
//
//                    Log.e("reali", "^^^^^ " + p.getCard().getBath_style() + " _ " + p.getCard().getOccupancy_code());
//                }

                mList.clear();
                mList.addAll(list);

                setPropertiesType();

                mViewPager.setAdapter(mSectionsPagerAdapter);
                tabLayout.setupWithViewPager(mViewPager);

                mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        if (position == 1 && !review.getSelectedList().isEmpty()) {
                            showReviewedBtn(true);
                        } else {
                            showReviewedBtn(false);
                        }

                        if (position == 0) {
                            searchView.setVisibility(View.VISIBLE);
                        } else {
                            searchView.setVisibility(View.GONE);
                        }

                        Log.e("reali", "onPageSelected " + position);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });

                //sync();

            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("reali", "find Quincy onFailure");
                progress.dismiss();
            }
        });



    }

    private void sync() {

        DataStore<Property> dataStore = DataStore.collection("quincy", Property.class, StoreType.SYNC, myKinveyClient);

        dataStore.sync(new KinveySyncCallback() {
            @Override
            public void onSuccess(KinveyPushResponse kinveyPushResponse, KinveyPullResponse kinveyPullResponse) {
                Log.e("reali", "sync onSuccess");
            }

            @Override
            public void onPullStarted() {

            }

            @Override
            public void onPushStarted() {

            }

            @Override
            public void onPullSuccess(KinveyPullResponse kinveyPullResponse) {
                Log.e("reali", "sync onPullSuccess");
            }

            @Override
            public void onPushSuccess(KinveyPushResponse kinveyPushResponse) {
                Log.e("reali", "sync onPushSuccess");
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("reali", "sync onFailure");
            }
        });

    }

    private void findQuincyMini() {

        Log.e("reali", "find Quincy");

        Query query = myKinveyClient.query().addSort("id", AbstractQuery.SortOrder.ASC); //in("id", new String[]{"3347", "3348", "3349", "3350"}).

//        Query query = myKinveyClient.query().addSort("id", AbstractQuery.SortOrder.ASC).setSkip(7).setLimit(4);

        DataStore<Property> dataStore = DataStore.collection("quincy", Property.class, StoreType.CACHE, myKinveyClient);

        dataStore.find(query, new KinveyReadCallback<Property>() {

            @Override
            public void onSuccess(KinveyReadResponse<Property> kinveyReadResponse) {
                List<Property> list = kinveyReadResponse.getResult();

                Log.e("reali", "find Quincy " + list.size());

                mList.clear();
                mList.addAll(list);

                findBroomfield();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("reali", "find Quincy onFailure");
                progress.dismiss();
            }
        });
    }

    private void findBroomfield() {

        Log.e("reali", "find Broomfield");

        Query query = myKinveyClient.query().addSort("Account_Number", AbstractQuery.SortOrder.ASC); //in("id", new String[]{"3347", "3348", "3349", "3350"}).

//        Query query = myKinveyClient.query().addSort("id", AbstractQuery.SortOrder.ASC).setSkip(7).setLimit(4);

        DataStore<Property2> dataStore = DataStore.collection("broomfield", Property2.class, StoreType.CACHE, myKinveyClient);

        dataStore.find(query, new KinveyReadCallback<Property2>() {
            @Override
            public void onSuccess(KinveyReadResponse<Property2> kinveyReadResponse) {

                List<Property2> list = kinveyReadResponse.getResult();

                Log.e("reali", "find Broomfield " + list.size());
                progress.dismiss();

                mList2.clear();

                for (int i=0; i<list.size(); i++) {

                    Property2 p = list.get(i);

                    int magicIndex = i;

                    if (i >= mList.size()) {
                        magicIndex = i - mList.size() + 1;
                    }

                    p.setB_permits(mList.get(magicIndex).getB_permits());
                    p.setVisits(mList.get(magicIndex).getVisits());
//                    p.setFeatures(mList.get(magicIndex).getFeatures());
                    p.setExt_wall(mList.get(magicIndex).getCard().getExt_wall());

//                    Log.e("reali", p.getAccount_Number() + " _ " + p.getAccount_Type());

                    mList2.add(list.get(i));

                }

//                mList2.addAll(list);

                setProperties2Type();

                mViewPager.setAdapter(mSectionsPagerAdapter);
                tabLayout.setupWithViewPager(mViewPager);

                mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        if (position == 1 && !review.getSelectedList().isEmpty()) {
                            showReviewedBtn(true);
                        } else {
                            showReviewedBtn(false);
                        }

                        if (position == 0) {
                            searchView.setVisibility(View.VISIBLE);
                        } else {
                            searchView.setVisibility(View.GONE);
                        }

                        Log.e("reali", "onPageSelected " + position);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });

            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("reali", "find Broomfield onFailure");
                progress.dismiss();
            }
        });
    }

    private void setPropertiesType() {
        for (Property p : mList) {

            int prop_type = calcPropType(p.getCard().getOccupancy_code());

            p.setProperty_type(Constants.prop_types[prop_type]);
            p.prop_type = prop_type;

//            Log.e("reali", p.getId() + " _ " + p.getCard().getOccupancy_code() + " _ " + Constants.prop_types[prop_type]);
        }
    }

    private void setProperties2Type() {
        for (Property2 p : mList2) {

            int prop_type = calcProp2Type(p.getAccount_Type());

            p.setProperty_type(Constants.prop_types[prop_type]);
            p.prop_type = prop_type;

//            Log.e("reali", p.getId() + " _ " + p.getCard().getOccupancy_code() + " _ " + Constants.prop_types[prop_type]);
        }
    }

    public static int calcPropType(String oc_code) {

        int prop_type = -1;

        if (oc_code == null || oc_code.isEmpty() || oc_code.equals("null")) {
            oc_code = "1020";
        }

        switch (oc_code) {
            case "1010":
            case "1012":
            case "1015":
            case "1016":
            case "1040":
            case "1045":
            case "1050":
            case "1060":
            case "1090":
            case "1110":
            case "130":
            case "111C":
            case "013C":
                prop_type = Constants.PROP_RESIDENTIAL;
                break;
            case "1320":
            case "1310":
            case "1300":
                prop_type = Constants.PROP_VACANT;
                break;
            case "1020":
                prop_type = Constants.PROP_CONDO;
                break;
            default:
                prop_type = Constants.PROP_COMMERCIAL;
                break;
        }

        return prop_type;

    }

    public static int calcProp2Type(String type) {

        int prop_type = -1;

        switch (type) {
            case "SFR":
                prop_type = Constants.PROP_SFR;
                break;
            case "AGRIC":
                prop_type = Constants.PROP_AGRIC;
                break;
            case "COMM":
                prop_type = Constants.PROP_COMM;
                break;
            case "MULTRES":
                prop_type = Constants.PROP_MULTRES;
                break;
            default:
                prop_type = Constants.PROP_SFR;
                break;
        }

        return prop_type;

    }

    private void fileStoreFind() {

        Query q = myKinveyClient.query();
//        q.startsWith("_filename", "house").setLimit(10);

        FileStore fs = myKinveyClient.getFileStore(StoreType.CACHE);
        fs.find(q, new KinveyClientCallback<FileMetaData[]>() {
            @Override
            public void onSuccess(FileMetaData[] fileMetaDatas) {

                photoList.clear();

                Log.e("reali", "FileStore find onSuccess");

                for (FileMetaData fmd : fileMetaDatas) {
                    photoList.add(new PhotoFile(fmd.getId(), fmd.getFileName(), fmd.getDownloadURL()));
//                    Log.e("reali", fmd.getId() + " " + fmd.getFileName() + " " + fmd.getDownloadURL());
                }

//                getPhotos();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("reali", "FileStore find onFailure");
            }
        });

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onListFragmentInteraction(Property item) {
        openProperty(item.getId());
    }

    @Override
    public void onListFragmentInteraction(Property2 item) {
        openProperty(item.getAccount_Number());
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return inspect;
                case 1:
                    return review;
                case 2:
                    return upload;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "INSPECT";
                case 1:
                    return "REVIEW";
                case 2:
                    return "UPLOAD";
            }
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1314) {
            if (resultCode == PropertyDetailsActivity.INSPECT) {

                if (inspect != null && inspect.adapter != null) {
                    inspect.recalc();
                }

                if (review != null && review.adapter != null) {
                    review.recalc();

                    review.save();
                }

                mViewPager.setCurrentItem(1, true);
            } else if (resultCode == PropertyDetailsActivity.REVIEW) {

                if (inspect != null && inspect.adapter != null) {
                    inspect.recalc();
                }

                if (review != null && review.adapter != null) {
                    review.recalc();
                }

                if (upload != null && upload.adapter != null) {
                    upload.recalc();

                    upload.upload();
                }

                mViewPager.setCurrentItem(2, true);
            }  else if (resultCode == PropertyDetailsActivity.CHANGED_PROP_TYPE) {

                if (inspect != null && inspect.adapter != null) {
                    inspect.recalc();
                }

                if (review != null && review.adapter != null) {
                    review.recalc();
                }

                if (upload != null && upload.adapter != null) {
                    upload.recalc();
                }

                openProperty(data.getIntExtra("id", 0));

            }
        }
    }

    private void openProperty(int id) {
        Intent intent = new Intent(MainActivity.this, PropertyDetailsActivity.class);
        intent.putExtra("prop_id", id);
        intent.putExtra("dbIndex", dbIndex);

        startActivityForResult(intent, 1314);
    }

    private void openProperty(String acc_num) {
        Intent intent = new Intent(MainActivity.this, PropertyDetailsActivity.class);
        intent.putExtra("acc_num", acc_num);
        intent.putExtra("dbIndex", dbIndex);

        startActivityForResult(intent, 1314);
    }
}
