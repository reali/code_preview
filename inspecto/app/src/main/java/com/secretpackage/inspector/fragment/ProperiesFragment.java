package com.secretpackage.inspector.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.secretpackage.inspector.MainActivity;
import com.secretpackage.inspector.MyApplication;
import com.secretpackage.inspector.R;
import com.secretpackage.inspector.adapter.Properies2RVAdapter;
import com.secretpackage.inspector.adapter.ProperiesRVAdapter;
import com.secretpackage.inspector.model.NewValue;
import com.secretpackage.inspector.model.Occupancy;
import com.secretpackage.inspector.model.PropChanges;
import com.secretpackage.inspector.model.Property;
import com.secretpackage.inspector.model.Property2;
import com.secretpackage.inspector.util.Constants;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyDeleteCallback;
import com.kinvey.android.callback.KinveyReadCallback;
import com.kinvey.android.store.DataStore;
import com.kinvey.android.sync.KinveyPushCallback;
import com.kinvey.android.sync.KinveyPushResponse;
import com.kinvey.java.KinveyException;
import com.kinvey.java.Query;
import com.kinvey.java.core.KinveyClientCallback;
import com.kinvey.java.model.KinveyReadResponse;
import com.kinvey.java.query.AbstractQuery;
import com.kinvey.java.store.StoreType;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ProperiesFragment extends Fragment {

    private static final String ARG_TAB_INDEX = "tab-index";
    private static final String ARG_DB_INDEX = "db-index";

    private int tabIndex = 1;
    private int dbIndex = 0;

    private OnListFragmentInteractionListener mListener;

    private Client myKinveyClient;
    private DataStore<Property> dataStore;
    private DataStore<Property2> dataStore2;

//    private TextView tv_mapLot;
    private TextView tv_id;
    private TextView tv_address;
    private TextView tv_type;
//    private TextView tv_distance;
    private ImageView iv_id;
    private ImageView iv_addr;
    private ImageView iv_type;

    private boolean sort_id_asc = true;
//    private boolean sort_map_asc = false;
    private boolean sort_addr_asc = false;
    private boolean sort_type_asc = false;
//    private boolean sort_dist_asc = false;

    List<Property> mList = new ArrayList<>();
    List<Property2> mList2= new ArrayList<>();

    public RecyclerView.Adapter adapter;

    public ProperiesFragment() {
    }

    @SuppressWarnings("unused")
    public static ProperiesFragment newInstance(int columnCount, int dbIndex) {
        ProperiesFragment fragment = new ProperiesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TAB_INDEX, columnCount);
        args.putInt(ARG_DB_INDEX, dbIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            tabIndex = getArguments().getInt(ARG_TAB_INDEX);
            dbIndex = getArguments().getInt(ARG_DB_INDEX);
        }

//        myKinveyClient = ((MyApplication) getActivity().getApplication()).getKinveyClient();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_properies_list, container, false);

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        if (dbIndex == 0) {

            if (tabIndex == 1) {
                adapter = new ProperiesRVAdapter((MainActivity)getActivity(), mList, mListener, true);
            } else {
                adapter = new ProperiesRVAdapter((MainActivity)getActivity(), mList, mListener, false);
            }

        } else if (dbIndex == 1) {

            if (tabIndex == 1) {
                adapter = new Properies2RVAdapter((MainActivity)getActivity(), mList2, mListener, true);
            } else {
                adapter = new Properies2RVAdapter((MainActivity)getActivity(), mList2, mListener, false);
            }

        }



        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setAdapter(adapter);

        myKinveyClient = ((MyApplication)getActivity().getApplication()).getKinveyClient();

        dataStore = DataStore.collection("quincy", Property.class, StoreType.CACHE, myKinveyClient);
        dataStore2 = DataStore.collection("broomfield", Property2.class, StoreType.CACHE, myKinveyClient);

//        tv_mapLot = (TextView) view.findViewById(R.id.maplot);
        tv_id = (TextView) view.findViewById(R.id.id);
        tv_address = (TextView) view.findViewById(R.id.address);
        tv_type = (TextView) view.findViewById(R.id.type);
//        tv_distance = (TextView) view.findViewById(R.id.dist);

        iv_id = (ImageView) view.findViewById(R.id.iv_id);
        iv_addr = (ImageView) view.findViewById(R.id.iv_addr);
        iv_type = (ImageView) view.findViewById(R.id.iv_type);

//        tv_mapLot.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                doSort(0);
//            }
//        });

        tv_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSort(1);
            }
        });

        tv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSort(2);
            }
        });

        tv_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSort(3);
            }
        });

        recalc();

        return view;
    }

    public void recalc() {

        if (dbIndex == 0) {

            mList.clear();

            for (Property p : MainActivity.mList) {
                if (p.getStatus() == tabIndex) {
                    mList.add(p);
                }
            }

        } else if (dbIndex == 1) {

            mList2.clear();

            for (Property2 p : MainActivity.mList2) {
                if (p.getStatus() == tabIndex) {
                    mList2.add(p);
                }
            }

        }

        adapter.notifyDataSetChanged();
    }

    public void upload() {

        DataStore<Property> dataStoreSave = DataStore.collection("quincy", Property.class, StoreType.SYNC, myKinveyClient);
        dataStoreSave.setDeltaSetCachingEnabled(true);

        for (final Property p : mList) {
            Log.e("reali", "upload " + p.getId());

            PropChanges propCh = null;

            for (PropChanges pch : MainActivity.mListChanges) {
                if (pch.prop_id == p.getId()) {
                    propCh = pch;
                }
            }

            if (propCh != null) {

                p.getCard().setAc_type(propCh.acVal);
                p.getCard().setBath_count(propCh.bathroomVal);
                p.getCard().setBath_style(propCh.bathStyleVal);
                p.getCard().setBedroom_count(propCh.bedroomVal);
                p.getCard().setCeiling_type_commerc(propCh.ceilingTypeVal);
                p.getCard().setDepr_grade(propCh.deprGradeVal);
                p.getCard().setExtra_fixtures(propCh.fixturesVal);
                p.getCard().setHeat_fuel(propCh.fuelVal);
                p.getCard().setGrade(propCh.gradeVal);
                p.getCard().setHalf_bath_count(propCh.halfbathVal);
                p.getCard().setHeat_type(propCh.heatVal);
                p.getCard().setKitchen_style(propCh.kitchenVal);
                p.setLocation(new Occupancy(propCh.locVal));
                p.getCard().setOccupancy_code(propCh.occuCodeVal);
                p.getCard().setOccupancy(propCh.occuVal);
                p.getCard().setOther_rooms_count(propCh.otherRoomVal);
                p.getCard().setRoof_cover(propCh.roofCovVal);
                p.getCard().setRoof_structure(propCh.roofStrVal);
                p.getCard().setStories(propCh.storiesVal);
                p.getCard().setStyle(propCh.styleVal);
                p.setTopology(new Occupancy(propCh.topolVal));

                List<String> extWallCur = new ArrayList<>();
                for (NewValue nv : propCh.ext_wallNew) {
                    extWallCur.add(nv.value);
                }

                List<String> intWallCur = new ArrayList<>();
                for (NewValue nv : propCh.int_wallNew) {
                    intWallCur.add(nv.value);
                }

                List<String> intFloorCur = new ArrayList<>();
                for (NewValue nv : propCh.floorsNew) {
                    intFloorCur.add(nv.value);
                }

                List<String> featuresCur = new ArrayList<>();
                for (NewValue nv : propCh.ext_specNew) {
                    featuresCur.add(nv.value);
                }

                List<Occupancy> utilsCur = new ArrayList<>();
                for (NewValue nv : propCh.utilitiesListNew) {
                    utilsCur.add(new Occupancy(nv.value));
                }

                p.getCard().setExt_wall(extWallCur);
                p.getCard().setInt_wall(intWallCur);
                p.getCard().setInt_floor(intFloorCur);
                p.setFeatures(featuresCur);
                p.setUtilities(utilsCur);

            }

            try
            {
                dataStoreSave.save(p, new KinveyClientCallback<Property>(){
                    @Override
                    public void onSuccess(Property result) {
                        // Place your code here
                        // here we have a Book object with defined unique `_id`

                        Log.e("reali", "saved " + p.getId() + " " + result.getId());

                    }
                    @Override
                    public void onFailure(Throwable error) {
                        // Place your code here
                        Log.e("reali", "saved fail " + p.getId());
                    }
                });
            }
            catch (KinveyException ke)
            {
                // handle error
                ke.printStackTrace();
            }

            Query query = myKinveyClient.query().equals("prop_id", p.getId());

            DataStore<PropChanges> dataChDel = DataStore.collection("test", PropChanges.class, StoreType.SYNC, myKinveyClient);

            try {

                dataChDel.delete(query, new KinveyDeleteCallback() {
                    @Override
                    public void onSuccess(Integer integer) {
                        Log.e("reali", "delete propChanges OK " + integer);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("reali", "delete propChanges FAIL");
                    }
                });

            } catch (KinveyException ke) {
                // handle error
                ke.printStackTrace();
            }

//            try
//            {
//                dataStoreSave.push(new KinveyPushCallback(){
//                    @Override
//                    public void onSuccess(KinveyPushResponse kinveyPushResponse) {
//                        Log.e("reali", "push onSuccess");
//                    }
//
//                    @Override
//                    public void onFailure(Throwable throwable) {
//                        Log.e("reali", "push FAIL");
//                    }
//
//                    @Override
//                    public void onProgress(long l, long l1) {
//                        Log.e("reali", "push progress " + l + " " + l1);
//                    }
//                });
//            }
//            catch (KinveyException ke)
//            {
//                // handle error
//                ke.printStackTrace();
//            }

        }
    }

    private void setPropertiesType() {
        for (Property p : mList) {

            int prop_type = MainActivity.calcPropType(p.getCard().getOccupancy_code());

            p.setProperty_type(Constants.prop_types[prop_type]);
            p.prop_type = prop_type;

//            Log.e("reali", p.getId() + " _ " + p.getCard().getOccupancy_code() + " _ " + Constants.prop_types[prop_type]);
        }
    }

    private void setProperties2Type() {
        for (Property2 p : mList2) {

            int prop_type = MainActivity.calcProp2Type(p.getAccount_Type());

            p.setProperty_type(Constants.prop_types[prop_type]);
            p.prop_type = prop_type;

//            Log.e("reali", p.getId() + " _ " + p.getCard().getOccupancy_code() + " _ " + Constants.prop_types[prop_type]);
        }
    }

    public void showSearchResults(List<Property> searchResult) {

        mList.clear();
        mList.addAll(searchResult);

        adapter.notifyDataSetChanged();
    }

    public void showSearchResults(boolean second, List<Property2> searchResult) {

        mList2.clear();
        mList2.addAll(searchResult);

        adapter.notifyDataSetChanged();
    }

    public List<Property> getSelectedList() {

        List<Property> selected = new ArrayList<>();

        for (Property p : mList) {
            if (p.isSelected) {
                selected.add(p);
            }
        }

        return selected;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void save() {

        DataStore<Property> dataStoreSave = DataStore.collection("quincy", Property.class, StoreType.SYNC, myKinveyClient);
        DataStore<PropChanges> dataChSave = DataStore.collection("test", PropChanges.class, StoreType.SYNC, myKinveyClient);

        for (final Property p : mList) {
            Log.e("reali", "save " + p.getId());

            PropChanges propCh = null;

            for (PropChanges pch : MainActivity.mListChanges) {
                if (pch.prop_id == p.getId()) {
                    propCh = pch;
                }
            }

            if (propCh != null) {

                try
                {
                    dataChSave.save(propCh, new KinveyClientCallback<PropChanges>(){
                        @Override
                        public void onSuccess(PropChanges result) {
                            // Place your code here
                            // here we have a Book object with defined unique `_id`

                            Log.e("reali", "saved propCh " + p.getId() + " " + result.prop_id);

                        }
                        @Override
                        public void onFailure(Throwable error) {
                            // Place your code here
                            Log.e("reali", "saved propCh fail " + p.getId());
                        }
                    });
                }
                catch (KinveyException ke)
                {
                    // handle error
                    ke.printStackTrace();
                }

            }

            try
            {
                dataStoreSave.save(p, new KinveyClientCallback<Property>(){
                    @Override
                    public void onSuccess(Property result) {
                        // Place your code here
                        // here we have a Book object with defined unique `_id`

                        Log.e("reali", "saved " + p.getId() + " " + result.getId());

                    }
                    @Override
                    public void onFailure(Throwable error) {
                        // Place your code here
                        Log.e("reali", "saved fail " + p.getId());
                    }
                });
            }
            catch (KinveyException ke)
            {
                // handle error
                ke.printStackTrace();
            }

        }

    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Property item);
        void onListFragmentInteraction(Property2 item);
    }

    private void doSort(int i) {

        if (dbIndex == 0) {

            Query query = myKinveyClient.query().addSort("id", AbstractQuery.SortOrder.ASC); //.in("id", new String[]{"3347", "3348", "3349", "3350"})

            if (i == 1) { //id

                if (sort_id_asc) {

                    query = myKinveyClient.query().addSort("id", AbstractQuery.SortOrder.DESC);

                    uns();
                    sort_id_asc = false;

                    iv_id.setImageResource(R.drawable.down);
                    iv_id.setVisibility(View.VISIBLE);

                } else {

                    query = myKinveyClient.query().addSort("id", AbstractQuery.SortOrder.ASC);

                    uns();
                    sort_id_asc = true;

                    iv_id.setImageResource(R.drawable.up);
                    iv_id.setVisibility(View.VISIBLE);

                }

            } else if (i == 2) { //addr

                if (sort_addr_asc) {

                    query = myKinveyClient.query().addSort("street_name", AbstractQuery.SortOrder.DESC);

                    uns();
                    sort_addr_asc = false;

                    iv_addr.setImageResource(R.drawable.down);
                    iv_addr.setVisibility(View.VISIBLE);

                } else {

                    query = myKinveyClient.query().addSort("street_name", AbstractQuery.SortOrder.ASC);

                    uns();
                    sort_addr_asc = true;

                    iv_addr.setImageResource(R.drawable.up);
                    iv_addr.setVisibility(View.VISIBLE);

                }

            } else if (i == 3) { //type

                if (sort_type_asc) {

                    query = myKinveyClient.query().addSort("property_type", AbstractQuery.SortOrder.DESC);

                    uns();
                    sort_type_asc = false;

                    iv_type.setImageResource(R.drawable.down);
                    iv_type.setVisibility(View.VISIBLE);

                } else {

                    query = myKinveyClient.query().addSort("property_type", AbstractQuery.SortOrder.ASC);

                    uns();
                    sort_type_asc = true;

                    iv_type.setImageResource(R.drawable.up);
                    iv_type.setVisibility(View.VISIBLE);

                }

            }

            dataStore.find(query, new KinveyReadCallback<Property>() {
                @Override
                public void onSuccess(KinveyReadResponse<Property> kinveyReadResponse) {

                    List<Property> list = kinveyReadResponse.getResult();

                    Log.e("reali", "find " + list.size());

                    for (Property p : list) {
                        Log.e("reali", p.getId() + " _ " + p.getStreet_num() + " _ " + p.getMap_id() + " _ " + p.getProperty_type());
                    }

                    mList.clear();
                    mList.addAll(list);

                    setPropertiesType();

                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Throwable throwable) {

                    Log.e("reali", "onFailure " + throwable.getMessage());
                }
            });

        } else if (dbIndex == 1) {

            Query query = myKinveyClient.query().addSort("Account_Number", AbstractQuery.SortOrder.ASC); //.in("id", new String[]{"3347", "3348", "3349", "3350"})

            if (i == 1) { //id

                if (sort_id_asc) {

                    query = myKinveyClient.query().addSort("Account_Number", AbstractQuery.SortOrder.DESC);

                    uns();
                    sort_id_asc = false;

                    iv_id.setImageResource(R.drawable.down);
                    iv_id.setVisibility(View.VISIBLE);

                } else {

                    query = myKinveyClient.query().addSort("Account_Number", AbstractQuery.SortOrder.ASC);

                    uns();
                    sort_id_asc = true;

                    iv_id.setImageResource(R.drawable.up);
                    iv_id.setVisibility(View.VISIBLE);

                }

            } else if (i == 2) { //addr

                if (sort_addr_asc) {

                    query = myKinveyClient.query().addSort("Situs_Address", AbstractQuery.SortOrder.DESC);

                    uns();
                    sort_addr_asc = false;

                    iv_addr.setImageResource(R.drawable.down);
                    iv_addr.setVisibility(View.VISIBLE);

                } else {

                    query = myKinveyClient.query().addSort("Situs_Address", AbstractQuery.SortOrder.ASC);

                    uns();
                    sort_addr_asc = true;

                    iv_addr.setImageResource(R.drawable.up);
                    iv_addr.setVisibility(View.VISIBLE);

                }

            } else if (i == 3) { //type

                if (sort_type_asc) {

                    query = myKinveyClient.query().addSort("Account_Type", AbstractQuery.SortOrder.DESC);

                    uns();
                    sort_type_asc = false;

                    iv_type.setImageResource(R.drawable.down);
                    iv_type.setVisibility(View.VISIBLE);

                } else {

                    query = myKinveyClient.query().addSort("Account_Type", AbstractQuery.SortOrder.ASC);

                    uns();
                    sort_type_asc = true;

                    iv_type.setImageResource(R.drawable.up);
                    iv_type.setVisibility(View.VISIBLE);

                }

            }

            dataStore2.find(query, new KinveyReadCallback<Property2>() {
                @Override
                public void onSuccess(KinveyReadResponse<Property2> kinveyReadResponse) {

                    List<Property2> list = kinveyReadResponse.getResult();

                    Log.e("reali", "find " + list.size());

//                    for (Property2 p : list) {
//                        Log.e("reali", p.getId() + " _ " + p.getStreet_num() + " _ " + p.getMap_id() + " _ " + p.getProperty_type());
//                    }

                    mList2.clear();
                    mList2.addAll(list);

                    setProperties2Type();

                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Throwable throwable) {

                    Log.e("reali", "onFailure " + throwable.getMessage());
                }
            });

        }

    }

    private void uns() {
        sort_id_asc = false;
//        sort_map_asc = false;
        sort_addr_asc = false;
        sort_type_asc = false;
//        sort_dist_asc = false;



        iv_id.setVisibility(View.GONE);
        iv_addr.setVisibility(View.GONE);
        iv_type.setVisibility(View.GONE);
    }
}
