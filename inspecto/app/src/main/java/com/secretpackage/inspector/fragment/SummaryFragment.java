package com.secretpackage.inspector.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.secretpackage.inspector.Chooser;
import com.secretpackage.inspector.DrawActivity;
import com.secretpackage.inspector.MainActivity;
import com.secretpackage.inspector.MyApplication;
import com.secretpackage.inspector.OnSyncPageChangeListener;
import com.secretpackage.inspector.PropertyDetailsActivity;
import com.secretpackage.inspector.R;
import com.secretpackage.inspector.adapter.BPermitsRVAdapter;
import com.secretpackage.inspector.adapter.NewPhotoAdapter;
import com.secretpackage.inspector.adapter.NewPhotoPreviewAdapter;
import com.secretpackage.inspector.adapter.UtilitiesRVAdapter;
import com.secretpackage.inspector.adapter.VisitsRVAdapter;
import com.secretpackage.inspector.model.NewValue;
import com.secretpackage.inspector.model.Property;
import com.secretpackage.inspector.model.Property2;
import com.secretpackage.inspector.util.Constants;
import com.kinvey.android.Client;

import com.squareup.picasso.Picasso;


import java.io.File;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SummaryFragment extends Fragment {

    private static final String ARG_BROOM = "param0";
    private static final String ARG_PARAM1 = "param1";

    private Client myKinveyClient;
    private Property property1;
    private Property2 property2;

    private boolean isBroom;

    private PropertyDetailsActivity pd_activity;

    private OnFragmentInteractionListener mListener;

//    @BindView(R.id.iv_photo) ImageView mainPhoto;
    @BindView(R.id.iv_sketch) public ImageView sketch;
    @BindView(R.id.et_notes) public EditText et_notes;

    @BindView(R.id.photo_preview) ViewPager photoPreviewPager;
    @BindView(R.id.photo_pager) ViewPager photoPager;

    @BindView(R.id.tv_last_assessed) TextView tv_assessed;
    @BindView(R.id.tv_last_sale) TextView tv_last_sale;

    @BindView(R.id.rv_b_permits) RecyclerView rv_b_permits;
    @BindView(R.id.rv_visits) RecyclerView rv_visits;

    @BindView(R.id.tv_location) TextView tv_location;
    @BindView(R.id.tv_topology) TextView tv_topology;
    @BindView(R.id.tv_occupancy_code) TextView tv_occupancy_code;
    @BindView(R.id.tv_occu) TextView tv_occu;

    @BindView(R.id.rv_spec_feat) RecyclerView rv_spec_feat;
    @BindView(R.id.rv_utilities) RecyclerView rv_utilities;

    @BindView(R.id.bt_add_spec_feat) Button bt_add_spec_feat;
    @BindView(R.id.bt_add_util) Button bt_add_util;
    @BindView(R.id.bt_del_spec_feat) Button bt_del_spec_feat;
    @BindView(R.id.bt_del_util) Button bt_del_util;

    @BindView(R.id.bt_change_loc) Button bt_change_loc;
    @BindView(R.id.bt_change_topology) Button bt_change_topology;
    @BindView(R.id.bt_change_occu_code) Button bt_change_occu_code;
    @BindView(R.id.bt_change_occu) Button bt_change_occu;

    @BindView(R.id.bt_take_photo) Button bt_take_photo;

    @BindView(R.id.iv_check_occu_code) ImageView iv_check_occu_code;
    @BindView(R.id.iv_check_occu) ImageView iv_check_occu;
    @BindView(R.id.iv_check_topology) ImageView iv_check_topology;
    @BindView(R.id.iv_check_loc) ImageView iv_check_loc;

    @BindView(R.id.bt_main) Button bt_main;
    @BindView(R.id.bt_del) Button bt_del;

    @BindView(R.id.occu_layout) View occu_layout;
    @BindView(R.id.spec_features_layout) View spec_features_layout;
    @BindView(R.id.b_perm_layout) View b_perm_layout;

    private final int CHANGE_LOC = 66;
    private final int CHANGE_UTIL = 70;
    private final int CHANGE_TOP = 67;
    private final int CHANGE_OCCU_CODE = 68;
    private final int CHANGE_TYPE_OCCU = 169;

    public UtilitiesRVAdapter ext_spec_features_adapter;
    public UtilitiesRVAdapter utilities_adapter;

    public BPermitsRVAdapter b_permits_adapter;

    public NewPhotoAdapter photoViewAdapter;
    public NewPhotoPreviewAdapter photoPreviewAdapter;

    public SummaryFragment() {
    }

    public static SummaryFragment newInstance(int id_prop) {
        SummaryFragment fragment = new SummaryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, id_prop);
        fragment.setArguments(args);
        return fragment;
    }

    public static SummaryFragment newInstance(boolean isBroom, String id_prop) {
        SummaryFragment fragment = new SummaryFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_BROOM, isBroom);
        args.putString(ARG_PARAM1, id_prop);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isBroom = getArguments().getBoolean(ARG_BROOM, false);

            if (isBroom) {

                String prop_id = getArguments().getString(ARG_PARAM1);

                for (Property2 p : MainActivity.mList2) {
                    if (p.getAccount_Number().equals(prop_id)) {
                        property2 = p;
                    }
                }

            } else {

                int prop_id = getArguments().getInt(ARG_PARAM1);

                for (Property p : MainActivity.mList) {
                    if (p.getId() == prop_id) {
                        property1 = p;
                    }
                }

            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_property_summary, container, false);
        ButterKnife.bind(this,mView);

        pd_activity = (PropertyDetailsActivity) getActivity();
        myKinveyClient = ((MyApplication)getActivity().getApplication()).getKinveyClient();

        et_notes.setText(pd_activity.notes);
        et_notes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (getActivity().getCurrentFocus() == et_notes) {
                    pd_activity.notes = editable.toString();
                }
            }
        });

        DecimalFormat formatter = new DecimalFormat("#,###"); //"#,###.00"

        if (!isBroom) {
            if (property1.getOwners().size() > 0) {
                tv_assessed.setText("$" + formatter.format(property1.getOwners().get(property1.getOwners().size()-1).getAssessed_value()));
                tv_last_sale.setText("$" + formatter.format(property1.getOwners().get(property1.getOwners().size()-1).getSale_price()));
            } else {
                tv_assessed.setText("-");
                tv_last_sale.setText("-");
            }

            /////

            if (pd_activity.propCh.isLocChecked) {
                iv_check_loc.setImageResource(R.drawable.check_gr);
                bt_change_loc.setText(pd_activity.propCh.locVal + " - " + Constants.Location_values.get(pd_activity.propCh.locVal));
            }

            if (pd_activity.propCh.isTopolChecked) {
                iv_check_topology.setImageResource(R.drawable.check_gr);
                bt_change_topology.setText(pd_activity.propCh.topolVal + " - " + Constants.Topology_values.get(pd_activity.propCh.topolVal));
            }

            if (pd_activity.propCh.isOccuChecked) {
                iv_check_occu.setImageResource(R.drawable.check_gr);
                bt_change_occu.setText(pd_activity.propCh.occuVal + " - " + Constants.Occu_values.get(pd_activity.propCh.occuVal));
            }

            if (pd_activity.propCh.isOccuCodeChecked) {
                iv_check_occu_code.setImageResource(R.drawable.check_gr);
                bt_change_occu_code.setText(pd_activity.propCh.occuCodeVal + " - " + Constants.Occu_code_values.get(pd_activity.propCh.occuCodeVal));
            }

            tv_location.setText(property1.getLocation().getCode() + " - " + Constants.Location_values.get(property1.getLocation().getCode()));
            tv_topology.setText(property1.getTopology().getCode() + " - " + Constants.Topology_values.get(property1.getTopology().getCode()));
            tv_occupancy_code.setText(property1.getCard().getOccupancy_code() + " - " + Constants.Occu_code_values.get(property1.getCard().getOccupancy_code()));
            tv_occu.setText(property1.getCard().getOccupancy());

            /////

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            rv_b_permits.setLayoutManager(linearLayoutManager);

            LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext());
            rv_visits.setLayoutManager(linearLayoutManager1);

            LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext());
            rv_utilities.setLayoutManager(linearLayoutManager2);

            LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(getContext());
            rv_spec_feat.setLayoutManager(linearLayoutManager3);

//            Collections.sort(property1.getB_permits());



            b_permits_adapter = new BPermitsRVAdapter(getActivity(), property1.getId(), pd_activity.propCh.b_permits);
            VisitsRVAdapter visits_adapter = new VisitsRVAdapter(property1.getVisits());

            ext_spec_features_adapter = new UtilitiesRVAdapter(getActivity(), pd_activity.ext_spec, pd_activity.propCh.ext_specNew, pd_activity.feature_type, 81);
            utilities_adapter = new UtilitiesRVAdapter(getActivity(), pd_activity.utilitiesList, pd_activity.propCh.utilitiesListNew, Chooser.TYPE_UTIL, 69);

            rv_b_permits.setAdapter(b_permits_adapter);
            rv_visits.setAdapter(visits_adapter);
            rv_utilities.setAdapter(utilities_adapter);
            rv_spec_feat.setAdapter(ext_spec_features_adapter);

            /////

            bt_add_util.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getActivity(), Chooser.class);
                    intent.putExtra("type", Chooser.TYPE_UTIL);
                    startActivityForResult(intent, CHANGE_UTIL);
                }
            });

            bt_del_util.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pd_activity.utilitiesList.size() > 0) {
                        pd_activity.utilitiesList.remove(pd_activity.utilitiesList.size()-1);
                        pd_activity.propCh.utilitiesListNew.remove(pd_activity.propCh.utilitiesListNew.size()-1);
                        utilities_adapter.notifyDataSetChanged();
                    }
                }
            });

            bt_change_loc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getActivity(), Chooser.class);
                    intent.putExtra("type", Chooser.TYPE_LOC);
                    startActivityForResult(intent, CHANGE_LOC);
                }
            });

            bt_change_topology.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getActivity(), Chooser.class);
                    intent.putExtra("type", Chooser.TYPE_TOP);
                    startActivityForResult(intent, CHANGE_TOP);
                }
            });

            bt_change_occu_code.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getActivity(), Chooser.class);
                    intent.putExtra("type", Chooser.TYPE_OCCU_CODE);
                    startActivityForResult(intent, CHANGE_OCCU_CODE);
                }
            });

            bt_change_occu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getActivity(), Chooser.class);
                    intent.putExtra("type", Chooser.TYPE_OCCUPANCY);
                    startActivityForResult(intent, CHANGE_TYPE_OCCU);

                }
            });

            bt_add_spec_feat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getActivity(), Chooser.class);
                    intent.putExtra("type", pd_activity.feature_type);
                    intent.putExtra("multiselect", true);
                    getActivity().startActivityForResult(intent, 79);
                }
            });

            bt_del_spec_feat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pd_activity.ext_spec.size() > 0) {
                        pd_activity.ext_spec.remove(pd_activity.ext_spec.size()-1);
                        pd_activity.propCh.ext_specNew.remove(pd_activity.propCh.ext_specNew.size()-1);
                        pd_activity.update_spec_features_adapters();
                    }
                }
            });

            //////

            iv_check_occu_code.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pd_activity.propCh.isOccuCodeChecked) {
                        bt_change_occu_code.setText("Change");
                        iv_check_occu_code.setImageResource(R.drawable.check);
                        pd_activity.propCh.isOccuCodeChecked = false;
                    } else {
                        bt_change_occu_code.setText(tv_occupancy_code.getText());
                        iv_check_occu_code.setImageResource(R.drawable.check_gr);
                        pd_activity.propCh.isOccuCodeChecked = true;
                        pd_activity.propCh.occuCodeVal = property1.getCard().getOccupancy_code();
                    }
                }
            });

            iv_check_occu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pd_activity.propCh.isOccuChecked) {
                        bt_change_occu.setText("Change");
                        iv_check_occu.setImageResource(R.drawable.check);
                        pd_activity.propCh.isOccuChecked = false;
                    } else {
                        bt_change_occu.setText(tv_occu.getText());
                        iv_check_occu.setImageResource(R.drawable.check_gr);
                        pd_activity.propCh.isOccuChecked = true;
                        pd_activity.propCh.occuVal = property1.getCard().getOccupancy();
                    }
                }
            });

            iv_check_topology.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pd_activity.propCh.isTopolChecked) {
                        bt_change_topology.setText("Change");
                        iv_check_topology.setImageResource(R.drawable.check);
                        pd_activity.propCh.isTopolChecked = false;
                    } else {
                        bt_change_topology.setText(tv_topology.getText());
                        iv_check_topology.setImageResource(R.drawable.check_gr);
                        pd_activity.propCh.isTopolChecked = true;
                        pd_activity.propCh.topolVal = property1.getTopology().getCode();
                    }
                }
            });

            iv_check_loc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pd_activity.propCh.isLocChecked) {
                        bt_change_loc.setText("Change");
                        iv_check_loc.setImageResource(R.drawable.check);
                        pd_activity.propCh.isLocChecked = false;
                    } else {
                        bt_change_loc.setText(tv_location.getText());
                        iv_check_loc.setImageResource(R.drawable.check_gr);
                        pd_activity.propCh.isLocChecked = true;
                        pd_activity.propCh.locVal = property1.getLocation().getCode();
                    }
                }
            });

        } else {

            tv_assessed.setText(property2.getActual_Value());
            tv_last_sale.setText(property2.getActual_Value());

            /////

            if (pd_activity.propCh.isLocChecked) {
                iv_check_loc.setImageResource(R.drawable.check_gr);
                bt_change_loc.setText(pd_activity.propCh.locVal);
            }

            if (pd_activity.propCh.isTopolChecked) {
                iv_check_topology.setImageResource(R.drawable.check_gr);
                bt_change_topology.setText("NONE");
            }

            if (pd_activity.propCh.isOccuCodeChecked) {
                iv_check_occu_code.setImageResource(R.drawable.check_gr);
                bt_change_occu_code.setText(pd_activity.propCh.occuCodeVal + " - " + Constants.Occu_code_values.get(pd_activity.propCh.occuCodeVal));
            }

            tv_location.setText(property2.getSitus_Address());
            tv_topology.setText("NONE");
            tv_occupancy_code.setText(property2.getProperty_Code_Description());
//            tv_occu.setText(property.getCard().getOccupancy());

            /////

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            rv_b_permits.setLayoutManager(linearLayoutManager);

            LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext());
            rv_visits.setLayoutManager(linearLayoutManager1);

            LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext());
            rv_utilities.setLayoutManager(linearLayoutManager2);

            LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(getContext());
            rv_spec_feat.setLayoutManager(linearLayoutManager3);

//            Collections.sort(property2.getB_permits());

            b_permits_adapter = new BPermitsRVAdapter(getActivity(), property2.getAccount_Number(), pd_activity.propCh.b_permits);
            VisitsRVAdapter visits_adapter = new VisitsRVAdapter(property2.getVisits());

            ext_spec_features_adapter = new UtilitiesRVAdapter(getActivity(), pd_activity.ext_spec, pd_activity.propCh.ext_specNew, pd_activity.feature_type, 81);
            utilities_adapter = new UtilitiesRVAdapter(getActivity(), pd_activity.utilitiesList, pd_activity.propCh.utilitiesListNew, Chooser.TYPE_UTIL, 69);

            rv_b_permits.setAdapter(b_permits_adapter);
            rv_visits.setAdapter(visits_adapter);
            rv_utilities.setAdapter(utilities_adapter);
            rv_spec_feat.setAdapter(ext_spec_features_adapter);

            //////

//            bt_add_util.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    Intent intent = new Intent(getActivity(), Chooser.class);
//                    intent.putExtra("type", Chooser.TYPE_UTIL);
//                    startActivityForResult(intent, 70);
//                }
//            });
//
//            bt_del_util.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (pd_activity.utilitiesList.size() > 0) {
//                        pd_activity.utilitiesList.remove(pd_activity.utilitiesList.size()-1);
//                        utilities_adapter.notifyDataSetChanged();
//                    }
//                }
//            });
//
//            bt_change_loc.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    Intent intent = new Intent(getActivity(), Chooser.class);
//                    intent.putExtra("type", Chooser.TYPE_LOC);
//                    startActivityForResult(intent, 66);
//                }
//            });
//
//            bt_change_topology.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    Intent intent = new Intent(getActivity(), Chooser.class);
//                    intent.putExtra("type", Chooser.TYPE_TOP);
//                    startActivityForResult(intent, 67);
//                }
//            });
//
//            bt_change_occu_code.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    Intent intent = new Intent(getActivity(), Chooser.class);
//                    intent.putExtra("type", Chooser.TYPE_OCCU_CODE);
//                    startActivityForResult(intent, 68);
//                }
//            });
//
//            bt_change_occu.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    Intent intent = new Intent(getActivity(), Chooser.class);
//                    intent.putExtra("type", Chooser.TYPE_OCCUPANCY);
//                    startActivityForResult(intent, 169);
//
//                }
//            });

            bt_add_spec_feat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getActivity(), Chooser.class);
                    intent.putExtra("type", pd_activity.feature_type);
                    intent.putExtra("multiselect", true);
                    getActivity().startActivityForResult(intent, 79);
                }
            });

            bt_del_spec_feat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pd_activity.ext_spec.size() > 0) {
                        pd_activity.ext_spec.remove(pd_activity.ext_spec.size()-1);
                        pd_activity.propCh.ext_specNew.remove(pd_activity.propCh.ext_specNew.size()-1);
                        pd_activity.update_spec_features_adapters();
                    }
                }
            });

            //////

            iv_check_occu_code.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pd_activity.propCh.isOccuCodeChecked) {
                        bt_change_occu_code.setText("Change");
                        iv_check_occu_code.setImageResource(R.drawable.check);
                        pd_activity.propCh.isOccuCodeChecked = false;
                    } else {
                        bt_change_occu_code.setText(tv_occupancy_code.getText());
                        iv_check_occu_code.setImageResource(R.drawable.check_gr);
                        pd_activity.propCh.isOccuCodeChecked = true;
                        pd_activity.propCh.occuCodeVal = property2.getProperty_Code_Description();
                    }
                }
            });

            iv_check_topology.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pd_activity.propCh.isTopolChecked) {
                        bt_change_topology.setText("Change");
                        iv_check_topology.setImageResource(R.drawable.check);
                        pd_activity.propCh.isTopolChecked = false;
                    } else {
                        bt_change_topology.setText(tv_topology.getText());
                        iv_check_topology.setImageResource(R.drawable.check_gr);
                        pd_activity.propCh.isTopolChecked = true;
                        pd_activity.propCh.topolVal = "";
                    }
                }
            });

            iv_check_loc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pd_activity.propCh.isLocChecked) {
                        bt_change_loc.setText("Change");
                        iv_check_loc.setImageResource(R.drawable.check);
                        pd_activity.propCh.isLocChecked = false;
                    } else {
                        bt_change_loc.setText(tv_location.getText());
                        iv_check_loc.setImageResource(R.drawable.check_gr);
                        pd_activity.propCh.isLocChecked = true;
                        pd_activity.propCh.locVal = "";
                    }
                }
            });

        }

        sketch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DrawActivity.class);
                intent.putExtra("path", pd_activity.mySketchFile);

                getActivity().startActivityForResult(intent, 1210);
            }
        });

        photoPreviewAdapter = new NewPhotoPreviewAdapter(getContext(), pd_activity.photoInfos, 1);
        photoViewAdapter = new NewPhotoAdapter(getContext(), pd_activity.photoInfos);

        photoPreviewPager.setAdapter(photoPreviewAdapter);
        photoPreviewPager.addOnPageChangeListener(new OnSyncPageChangeListener(photoPager, photoPreviewPager));

        photoPager.setAdapter(photoViewAdapter);
        photoPager.setOffscreenPageLimit(photoPreviewAdapter.getSidePreviewCount() * 2 + 1);
        photoPager.addOnPageChangeListener(new OnSyncPageChangeListener(photoPreviewPager, photoPager));

        bt_main.setVisibility(View.GONE);

        photoPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    bt_main.setVisibility(View.GONE);
                } else {
                    bt_main.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ((PropertyDetailsActivity)(getActivity())).photoPager = photoPager;

        if (pd_activity.sketchBitmap != null) {
            sketch.setImageBitmap(pd_activity.sketchBitmap);
        } else {
            Picasso.with(getContext()).load(pd_activity.mySketchFile).into(sketch);
        }

        /////

        bt_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (pd_activity.photoInfos.size() > 0) {

                    List<File> newList = new ArrayList<File>();
                    newList.add(pd_activity.photoInfos.get(photoPager.getCurrentItem()));
                    pd_activity.photoInfos.remove(photoPager.getCurrentItem());
                    newList.addAll(pd_activity.photoInfos);

                    pd_activity.photoInfos.clear();
                    pd_activity.photoInfos.addAll(newList);

                    photoViewAdapter.notifyDataSetChanged();
                    photoPreviewAdapter.notifyDataSetChanged();

                    photoPager.setCurrentItem(0);
                }

            }
        });

        bt_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
//                alert.setTitle("Alert window");
                alert.setMessage("Are you sure you want to delete this photo?");

                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (pd_activity.photoInfos.size() > 0) {
                            pd_activity.photoInfos.remove(photoPager.getCurrentItem());
                            photoViewAdapter.notifyDataSetChanged();
                            photoPreviewAdapter.notifyDataSetChanged();
                        }
                    }
                });

                alert.setNegativeButton("No", null);

                alert.show();

            }
        });

        bt_take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd_activity.checkCameraPermission();
            }
        });


        if (!isBroom) {

            if (property1.prop_type == Constants.PROP_COMMERCIAL || property1.prop_type == Constants.PROP_VACANT) {
                occu_layout.setVisibility(View.GONE);
            }

            if (property1.prop_type == Constants.PROP_VACANT) {
                spec_features_layout.setVisibility(View.GONE);
            }

        } else {

            occu_layout.setVisibility(View.GONE);

            if (property2.prop_type == Constants.PROP_AGRIC) {
                b_perm_layout.setVisibility(View.GONE);
            }

        }




        return mView;

    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHANGE_LOC && resultCode == 1313) {
            String[] mKeys = Constants.Location_values.keySet().toArray(new String[Constants.Location_values.size()]);
            int pos = data.getIntExtra("pos", 0);

            bt_change_loc.setText(mKeys[pos] + " - " + Constants.Location_values.get(mKeys[pos]));

            iv_check_loc.setImageResource(R.drawable.check_red);
            pd_activity.propCh.isLocChecked = true;
            pd_activity.propCh.locVal = mKeys[pos];

        } else if (requestCode == CHANGE_TOP && resultCode == 1313) {
            String[] mKeys = Constants.Topology_values.keySet().toArray(new String[Constants.Topology_values.size()]);
            int pos = data.getIntExtra("pos", 0);

            bt_change_topology.setText(mKeys[pos] + " - " + Constants.Topology_values.get(mKeys[pos]));

            iv_check_topology.setImageResource(R.drawable.check_gr);
            pd_activity.propCh.isTopolChecked = true;
            pd_activity.propCh.topolVal = mKeys[pos];

        } else if (requestCode == CHANGE_OCCU_CODE && resultCode == 1313) {
            final int pos = data.getIntExtra("pos", 0);
            final String[] mKeys = Constants.Occu_code_values.keySet().toArray(new String[Constants.Occu_code_values.size()]);

            final int newPropType = MainActivity.calcPropType(mKeys[pos]);

            if (property1.prop_type != newPropType) {

                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("By this action you are changing property type. All unsaved changes will be deleted!");
                alert.setMessage("Are you sure you want to do it?");

                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        bt_change_occu_code.setText(mKeys[pos] + " - " + Constants.Occu_code_values.get(mKeys[pos]));

                        iv_check_occu_code.setImageResource(R.drawable.check_red);
                        pd_activity.propCh.isOccuCodeChecked = true;

                        /////////////////////////

                        property1.setProperty_type(Constants.prop_types[newPropType]);
                        property1.getCard().setOccupancy_code(mKeys[pos]);
                        property1.prop_type = newPropType;

                        Intent intent = new Intent();
                        intent.putExtra("id", property1.getId());

                        pd_activity.setResult(PropertyDetailsActivity.CHANGED_PROP_TYPE, intent);
                        pd_activity.finish();

                    }
                });

                alert.setNegativeButton("No", null);

                alert.show();

            } else {

                bt_change_occu_code.setText(mKeys[pos] + " - " + Constants.Occu_code_values.get(mKeys[pos]));

                iv_check_occu_code.setImageResource(R.drawable.check_red);
                pd_activity.propCh.isOccuCodeChecked = true;
                pd_activity.propCh.occuCodeVal = mKeys[pos];

            }

        } else if (requestCode == CHANGE_TYPE_OCCU && resultCode == 1313) {
            int pos = data.getIntExtra("pos", 0);
            String[] mKeys = Constants.Occu_values.keySet().toArray(new String[Constants.Occu_values.size()]);

            bt_change_occu.setText(mKeys[pos]);

            iv_check_occu.setImageResource(R.drawable.check_gr);
            pd_activity.propCh.isOccuChecked = true;
            pd_activity.propCh.occuVal = mKeys[pos];

        } else if (requestCode == CHANGE_UTIL && resultCode == 1313) {
            int pos = data.getIntExtra("pos", 0);
            String[] mKeys = Constants.Utility_values.keySet().toArray(new String[Constants.Utility_values.size()]);

            pd_activity.utilitiesList.add(mKeys[pos] + " - " + Constants.Utility_values.get(mKeys[pos]));
            pd_activity.propCh.utilitiesListNew.add(new NewValue(mKeys[pos], Constants.Utility_values.get(mKeys[pos]), true));
            utilities_adapter.notifyDataSetChanged();
        }
    }

}
