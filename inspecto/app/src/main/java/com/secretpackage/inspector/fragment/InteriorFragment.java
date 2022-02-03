package com.secretpackage.inspector.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.secretpackage.inspector.PropertyDetailsActivity;
import com.secretpackage.inspector.R;
import com.secretpackage.inspector.adapter.UtilitiesRVAdapter;
import com.secretpackage.inspector.model.NewValue;
import com.secretpackage.inspector.model.Property;
import com.secretpackage.inspector.model.Property2;
import com.secretpackage.inspector.util.Constants;
import com.kinvey.android.Client;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InteriorFragment extends Fragment {

    private static final String ARG_BROOM = "param0";
    private static final String ARG_PARAM1 = "param1";

    private Client myKinveyClient;
    private Property property1;
    private Property2 property2;

    private boolean isBroom;

    int bedrooms;
    int bathrooms;
    int half_baths;
    int other_rooms;
    int xtra_fixtures;

    int total_rooms;

    private PropertyDetailsActivity pd_activity;

    public UtilitiesRVAdapter ext_spec_features_adapter;
    public UtilitiesRVAdapter int_wall_adapter;
    public UtilitiesRVAdapter floors_adapter;

    @BindView(R.id.rv_spec_feat) RecyclerView rv_spec_feat;
    @BindView(R.id.rv_int_wall) RecyclerView rv_int_wall;
    @BindView(R.id.rv_floor) RecyclerView rv_floor;

    @BindView(R.id.iv_sketch) public ImageView sketch;

    @BindView(R.id.tv_bedrooms) TextView tv_bedrooms;
    @BindView(R.id.tv_bathrooms) TextView tv_bathrooms;
    @BindView(R.id.tv_half_baths) TextView tv_half_baths;
    @BindView(R.id.tv_other_rooms) TextView tv_other_rooms;
    @BindView(R.id.tv_xtra_fixtures) TextView tv_xtra_fixtures;
    @BindView(R.id.tv_total_rooms) TextView tv_total_rooms;

    @BindView(R.id.tv_new_bedrooms) TextView tv_new_bedrooms;
    @BindView(R.id.tv_new_bathrooms) TextView tv_new_bathrooms;
    @BindView(R.id.tv_new_half_baths) TextView tv_new_half_baths;
    @BindView(R.id.tv_new_other_rooms) TextView tv_new_other_rooms;
    @BindView(R.id.tv_new_xtra_fixtures) TextView tv_new_xtra_fixtures;

    @BindView(R.id.tv_kitchen) TextView tv_kitchen;
    @BindView(R.id.tv_bath_style) TextView tv_bath_style;

    @BindView(R.id.tv_ceiling_type) TextView tv_ceiling_type;
    @BindView(R.id.tv_depr_grade) TextView tv_depr_grade;

    @BindView(R.id.et_notes) public EditText et_notes;

    @BindView(R.id.tv_heat_fuel) public TextView tv_heat_fuel;
    @BindView(R.id.tv_heat_type) public TextView tv_heat_type;
    @BindView(R.id.tv_ac_type) public TextView tv_ac_type;

    @BindView(R.id.tv_int_wall_height) TextView tv_int_wall_height;
    @BindView(R.id.tv_basement) TextView tv_basement;
    @BindView(R.id.tv_basement_bedrooms) TextView tv_basement_bedrooms;
    @BindView(R.id.tv_window) TextView tv_window;

    @BindView(R.id.bt_change_heat_fuel) public Button bt_change_heat_fuel;
    @BindView(R.id.bt_change_heat_type) public Button bt_change_heat_type;
    @BindView(R.id.bt_change_ac_type) public Button bt_change_ac_type;

    @BindView(R.id.bt_add_interior_wall) Button bt_add_interior_wall;
    @BindView(R.id.bt_add_floor) Button bt_add_floor;
    @BindView(R.id.bt_add_spec_feat) Button bt_add_spec_feat;

    @BindView(R.id.bt_del_interior_wall) Button bt_del_interior_wall;
    @BindView(R.id.bt_del_floor) Button bt_del_floor;
    @BindView(R.id.bt_del_spec_feat) Button bt_del_spec_feat;

    @BindView(R.id.bt_pls_1_bedrooms) Button bt_pls_1_bedrooms;
    @BindView(R.id.bt_pls_1_bathrooms) Button bt_pls_1_bathrooms;
    @BindView(R.id.bt_pls_1_half_baths) Button bt_pls_1_half_baths;
    @BindView(R.id.bt_pls_1_other_rooms) Button bt_pls_1_other_rooms;
    @BindView(R.id.bt_pls_1_xtra_fixtures) Button bt_pls_1_xtra_fixtures;

    @BindView(R.id.bt_min_1_bedrooms) Button bt_min_1_bedrooms;
    @BindView(R.id.bt_min_1_bathrooms) Button bt_min_1_bathrooms;
    @BindView(R.id.bt_min_1_half_baths) Button bt_min_1_half_baths;
    @BindView(R.id.bt_min_1_other_rooms) Button bt_min_1_other_rooms;
    @BindView(R.id.bt_min_1_xtra_fixtures) Button bt_min_1_xtra_fixtures;

    @BindView(R.id.bt_change_kitchen) Button bt_change_kitchen;
    @BindView(R.id.bt_change_bath_style) Button bt_change_bath_style;
    @BindView(R.id.bt_change_ceiling_type) Button bt_change_ceiling_type;
    @BindView(R.id.bt_change_depr_grade) Button bt_change_depr_grade;

    @BindView(R.id.iv_check_heat_type) public ImageView iv_check_heat_type;
    @BindView(R.id.iv_check_fuel) public ImageView iv_check_fuel;
    @BindView(R.id.iv_check_ac) public ImageView iv_check_ac;

    @BindView(R.id.iv_check_kitchen) ImageView iv_check_kitchen;
    @BindView(R.id.iv_check_bath_style) ImageView iv_check_bath_style;
    @BindView(R.id.iv_check_ceiling_type) ImageView iv_check_ceiling_type;
    @BindView(R.id.iv_check_depr_grade) ImageView iv_check_depr_grade;

    @BindView(R.id.iv_check_bedrooms) ImageView iv_check_bedrooms;
    @BindView(R.id.iv_check_bathrooms) ImageView iv_check_bathrooms;
    @BindView(R.id.iv_check_half_bath) ImageView iv_check_half_bath;
    @BindView(R.id.iv_check_other_rooms) ImageView iv_check_other_rooms;
    @BindView(R.id.iv_check_fixtures) ImageView iv_check_fixtures;

    @BindView(R.id.rooms_layout) View rooms_layout;
    @BindView(R.id.int_wall_height_layout) View int_wall_height_layout;
    @BindView(R.id.heat_fuel_layout) View heat_fuel_layout;
    @BindView(R.id.half_baths_etc_layout) View half_baths_etc_layout;
    @BindView(R.id.inw_wall_floor_layout) View inw_wall_floor_layout;
    @BindView(R.id.basement_layout) View basement_layout;
    @BindView(R.id.basement_bedrooms_layout) View basement_bedrooms_layout;
    @BindView(R.id.bedroom_layout) View bedroom_layout;
    @BindView(R.id.window_layout) View window_layout;
    @BindView(R.id.ceiling_layout) View ceiling_layout;

    public InteriorFragment() {
        // Required empty public constructor
    }

    public static InteriorFragment newInstance(int id_prop) {
        InteriorFragment fragment = new InteriorFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, id_prop);
        fragment.setArguments(args);
        return fragment;
    }

    public static InteriorFragment newInstance(boolean isBroom, String id_prop) {
        InteriorFragment fragment = new InteriorFragment();
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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_interior, container, false);
        ButterKnife.bind(this, view);

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

        if (pd_activity.sketchBitmap != null) {
            sketch.setImageBitmap(pd_activity.sketchBitmap);
        } else {
            Picasso.with(getContext()).load(pd_activity.mySketchFile).into(sketch);
        }

        if (pd_activity.propCh.isBedroomChecked) {
            iv_check_bedrooms.setImageResource(R.drawable.check_gr);
            tv_new_bedrooms.setText(pd_activity.propCh.bedroomVal + "");
        }

        if (pd_activity.propCh.isBathroomChecked) {
            iv_check_bathrooms.setImageResource(R.drawable.check_gr);
            tv_new_bathrooms.setText(pd_activity.propCh.bathroomVal + "");
        }

        if (pd_activity.propCh.isHalfbathChecked) {
            iv_check_half_bath.setImageResource(R.drawable.check_gr);
            tv_new_half_baths.setText(pd_activity.propCh.halfbathVal + "");
        }

        if (pd_activity.propCh.isOtherRoomChecked) {
            iv_check_other_rooms.setImageResource(R.drawable.check_gr);
            tv_new_other_rooms.setText(pd_activity.propCh.otherRoomVal + "");
        }

        if (pd_activity.propCh.isFixturesChecked) {
            iv_check_fixtures.setImageResource(R.drawable.check_gr);
            tv_new_xtra_fixtures.setText(pd_activity.propCh.fixturesVal + "");
        }

        if (pd_activity.propCh.isKitchenChecked) {
            iv_check_kitchen.setImageResource(R.drawable.check_gr);
            bt_change_kitchen.setText(pd_activity.propCh.kitchenVal + " - " + Constants.Kitchen_values.get(pd_activity.propCh.kitchenVal));
        }

        if (pd_activity.propCh.isBathStyleChecked) {
            iv_check_bath_style.setImageResource(R.drawable.check_gr);
            bt_change_bath_style.setText(pd_activity.propCh.bathStyleVal + " - " + Constants.Bath_values.get(pd_activity.propCh.bathStyleVal));
        }

        if (pd_activity.propCh.isCeilingTypeChecked) {
            iv_check_ceiling_type.setImageResource(R.drawable.check_gr);
            bt_change_ceiling_type.setText(pd_activity.propCh.ceilingTypeVal + " - " + Constants.Ceiling_com_values.get(pd_activity.propCh.ceilingTypeVal));
        }

        if (pd_activity.propCh.isDeprGradeChecked) {
            iv_check_depr_grade.setImageResource(R.drawable.check_gr);
            bt_change_depr_grade.setText(pd_activity.propCh.deprGradeVal + " - " + Constants.Depreciation_values.get(pd_activity.propCh.deprGradeVal));
        }

        if (pd_activity.propCh.isHeatTypeChecked) {
            iv_check_heat_type.setImageResource(R.drawable.check_gr);
            bt_change_heat_type.setText(pd_activity.propCh.heatVal + " - " + Constants.Heat_Type_values.get(pd_activity.propCh.heatVal));
        }

        if (pd_activity.propCh.isFuelChecked) {
            iv_check_fuel.setImageResource(R.drawable.check_gr);
            bt_change_heat_fuel.setText(pd_activity.propCh.fuelVal + " - " + Constants.Fuel_Type_values.get(pd_activity.propCh.fuelVal));
        }

        if (pd_activity.propCh.isAcChecked) {
            iv_check_ac.setImageResource(R.drawable.check_gr);
            bt_change_ac_type.setText(pd_activity.propCh.acVal + " - " + Constants.AC_Type_values.get(pd_activity.propCh.acVal));
        }

        if (!isBroom) {

            bedrooms = property1.getCard().getBedroom_count();
            bathrooms = property1.getCard().getBath_count();
            half_baths = property1.getCard().getHalf_bath_count();
            xtra_fixtures = property1.getCard().getExtra_fixtures();
            other_rooms = property1.getCard().getOther_rooms_count();

            tv_bedrooms.setText(bedrooms + "");
            tv_bathrooms.setText(bathrooms + "");
            tv_half_baths.setText(half_baths + "");
            tv_other_rooms.setText(other_rooms + "");
            tv_xtra_fixtures.setText(xtra_fixtures + "");

            recalcTotal();

            tv_kitchen.setText(property1.getCard().getKitchen_style() + " - " + Constants.Kitchen_values.get(property1.getCard().getKitchen_style()));
            tv_bath_style.setText(property1.getCard().getBath_style() + " - " + Constants.Bath_values.get(property1.getCard().getBath_style()));

            tv_heat_fuel.setText(property1.getCard().getHeat_fuel() + " - " + Constants.Fuel_Type_values.get(property1.getCard().getHeat_fuel()));
            tv_heat_type.setText(property1.getCard().getHeat_type() + " - " + Constants.Heat_Type_values.get(property1.getCard().getHeat_type()));
            tv_ac_type.setText(property1.getCard().getAc_type() + " - " + Constants.AC_Type_values.get(property1.getCard().getAc_type()));

            tv_ceiling_type.setText(property1.getCard().getCeiling_type_commerc() + " - " + Constants.Ceiling_com_values.get(property1.getCard().getCeiling_type_commerc()));
            tv_depr_grade.setText(property1.getCard().getDepr_grade() + " - " + Constants.Depreciation_values.get(property1.getCard().getDepr_grade()));

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext());
            LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext());

            ext_spec_features_adapter = new UtilitiesRVAdapter(getActivity(), pd_activity.ext_spec, pd_activity.propCh.ext_specNew, pd_activity.feature_type, 81);
            int_wall_adapter = new UtilitiesRVAdapter(getActivity(), pd_activity.int_wall, pd_activity.propCh.int_wallNew, Chooser.TYPE_INT_WALL, 94);
            floors_adapter = new UtilitiesRVAdapter(getActivity(), pd_activity.floors, pd_activity.propCh.floorsNew, Chooser.TYPE_FLOOR, 95);

            rv_spec_feat.setLayoutManager(linearLayoutManager);
            rv_int_wall.setLayoutManager(linearLayoutManager1);
            rv_floor.setLayoutManager(linearLayoutManager2);

            rv_spec_feat.setAdapter(ext_spec_features_adapter);
            rv_int_wall.setAdapter(int_wall_adapter);
            rv_floor.setAdapter(floors_adapter);

            bt_change_kitchen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getActivity(), Chooser.class);
                    intent.putExtra("type", Chooser.TYPE_KITCHEN);
                    startActivityForResult(intent, 90);
                }
            });

            bt_change_bath_style.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getActivity(), Chooser.class);
                    intent.putExtra("type", Chooser.TYPE_BATH);
                    startActivityForResult(intent, 91);
                }
            });

            bt_add_interior_wall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getActivity(), Chooser.class);
                    intent.putExtra("type", Chooser.TYPE_INT_WALL);
                    startActivityForResult(intent, 92);
                }
            });

            bt_del_interior_wall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pd_activity.int_wall.size() > 0) {
                        pd_activity.int_wall.remove(pd_activity.int_wall.size()-1);
                        pd_activity.propCh.int_wallNew.remove(pd_activity.propCh.int_wallNew.size()-1);
                        int_wall_adapter.notifyDataSetChanged();
                    }
                }
            });

            bt_add_floor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getActivity(), Chooser.class);
                    intent.putExtra("type", Chooser.TYPE_FLOOR);
                    startActivityForResult(intent, 93);
                }
            });

            bt_del_floor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pd_activity.floors.size() > 0) {
                        pd_activity.floors.remove(pd_activity.floors.size()-1);
                        pd_activity.propCh.floorsNew.remove(pd_activity.propCh.floorsNew.size()-1);
                        floors_adapter.notifyDataSetChanged();
                    }
                }
            });

            bt_change_heat_fuel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getActivity(), Chooser.class);
                    intent.putExtra("type", Chooser.TYPE_HEAT_FUEL);
                    getActivity().startActivityForResult(intent, 76);
                }
            });

            bt_change_heat_type.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getActivity(), Chooser.class);
                    intent.putExtra("type", Chooser.TYPE_HEAT_TYPE);
                    getActivity().startActivityForResult(intent, 77);
                }
            });

            bt_change_ac_type.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getActivity(), Chooser.class);
                    intent.putExtra("type", Chooser.TYPE_AC_TYPE);
                    getActivity().startActivityForResult(intent, 78);
                }
            });

            bt_change_ceiling_type.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), Chooser.class);
                    intent.putExtra("type", Chooser.TYPE_CEILING);
                    startActivityForResult(intent, 94);
                }
            });

            bt_change_depr_grade.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), Chooser.class);
                    intent.putExtra("type", Chooser.TYPE_DEPR_GRADE);
                    startActivityForResult(intent, 95);
                }
            });

            bt_pls_1_bedrooms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bedrooms += 1;
                    tv_new_bedrooms.setText(bedrooms + "");

                    iv_check_bedrooms.setImageResource(R.drawable.check_red);
                    pd_activity.propCh.isBedroomChecked = true;
                    pd_activity.propCh.bedroomVal = bedrooms;

                    recalcTotal();
                }
            });

            bt_pls_1_bathrooms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bathrooms += 1;
                    tv_new_bathrooms.setText(bathrooms + "");

                    iv_check_bathrooms.setImageResource(R.drawable.check_gr);
                    pd_activity.propCh.isBathroomChecked = true;
                    pd_activity.propCh.bathroomVal = bathrooms;

                    recalcTotal();
                }
            });

            bt_pls_1_half_baths.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    half_baths += 1;
                    tv_new_half_baths.setText(half_baths + "");

                    iv_check_half_bath.setImageResource(R.drawable.check_gr);
                    pd_activity.propCh.isHalfbathChecked = true;
                    pd_activity.propCh.halfbathVal = half_baths;

                    recalcTotal();
                }
            });

            bt_pls_1_other_rooms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    other_rooms += 1;
                    tv_new_other_rooms.setText(other_rooms + "");

                    iv_check_other_rooms.setImageResource(R.drawable.check_gr);
                    pd_activity.propCh.isOtherRoomChecked = true;
                    pd_activity.propCh.otherRoomVal = other_rooms;

                    recalcTotal();
                }
            });

            bt_min_1_bedrooms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (bedrooms > 0) {
                        bedrooms -= 1;
                        tv_new_bedrooms.setText(bedrooms + "");

                        iv_check_bedrooms.setImageResource(R.drawable.check_red);
                        pd_activity.propCh.isBedroomChecked = true;
                        pd_activity.propCh.bedroomVal = bedrooms;

                        recalcTotal();
                    }
                }
            });

            bt_min_1_bathrooms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (bathrooms > 0) {
                        bathrooms -= 1;
                        tv_new_bathrooms.setText(bathrooms + "");

                        iv_check_bathrooms.setImageResource(R.drawable.check_gr);
                        pd_activity.propCh.isBathroomChecked = true;
                        pd_activity.propCh.bathroomVal = bathrooms;

                        recalcTotal();
                    }
                }
            });

            bt_min_1_half_baths.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (half_baths > 0) {
                        half_baths -= 1;
                        tv_new_half_baths.setText(half_baths + "");

                        iv_check_half_bath.setImageResource(R.drawable.check_gr);
                        pd_activity.propCh.isHalfbathChecked = true;
                        pd_activity.propCh.halfbathVal = half_baths;

                        recalcTotal();
                    }
                }
            });

            bt_min_1_other_rooms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (other_rooms > 0) {
                        other_rooms -= 1;
                        tv_new_other_rooms.setText(other_rooms + "");

                        iv_check_other_rooms.setImageResource(R.drawable.check_gr);
                        pd_activity.propCh.isOtherRoomChecked = true;
                        pd_activity.propCh.otherRoomVal = other_rooms;

                        recalcTotal();
                    }
                }
            });

            bt_pls_1_xtra_fixtures.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    xtra_fixtures += 1;
                    tv_new_xtra_fixtures.setText(xtra_fixtures + "");

                    iv_check_fixtures.setImageResource(R.drawable.check_gr);
                    pd_activity.propCh.isFixturesChecked = true;
                    pd_activity.propCh.fixturesVal = xtra_fixtures;
                }
            });

            bt_min_1_xtra_fixtures.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (xtra_fixtures > 0) {
                        xtra_fixtures -= 1;
                        tv_new_xtra_fixtures.setText(xtra_fixtures + "");

                        iv_check_fixtures.setImageResource(R.drawable.check_gr);
                        pd_activity.propCh.isFixturesChecked = true;
                        pd_activity.propCh.fixturesVal = xtra_fixtures;
                    }
                }
            });

            iv_check_kitchen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pd_activity.propCh.isKitchenChecked) {
                        bt_change_kitchen.setText("Change");
                        iv_check_kitchen.setImageResource(R.drawable.check);
                        pd_activity.propCh.isKitchenChecked = false;
                    } else {
                        bt_change_kitchen.setText(tv_kitchen.getText());
                        iv_check_kitchen.setImageResource(R.drawable.check_gr);
                        pd_activity.propCh.isKitchenChecked = true;
                        pd_activity.propCh.kitchenVal = property1.getCard().getKitchen_style();
                    }
                }
            });

            iv_check_bath_style.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pd_activity.propCh.isBathStyleChecked) {
                        bt_change_bath_style.setText("Change");
                        iv_check_bath_style.setImageResource(R.drawable.check);
                        pd_activity.propCh.isBathStyleChecked = false;
                    } else {
                        bt_change_bath_style.setText(tv_bath_style.getText());
                        iv_check_bath_style.setImageResource(R.drawable.check_gr);
                        pd_activity.propCh.isBathStyleChecked = true;
                        pd_activity.propCh.bathStyleVal = property1.getCard().getBath_style();
                    }
                }
            });

            iv_check_ceiling_type.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pd_activity.propCh.isCeilingTypeChecked) {
                        bt_change_ceiling_type.setText("Change");
                        iv_check_ceiling_type.setImageResource(R.drawable.check);
                        pd_activity.propCh.isCeilingTypeChecked = false;
                    } else {
                        bt_change_ceiling_type.setText(tv_ceiling_type.getText());
                        iv_check_ceiling_type.setImageResource(R.drawable.check_gr);
                        pd_activity.propCh.isCeilingTypeChecked = true;
                        pd_activity.propCh.ceilingTypeVal = property1.getCard().getCeiling_type_commerc();
                    }
                }
            });

            iv_check_depr_grade.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pd_activity.propCh.isDeprGradeChecked) {
                        bt_change_depr_grade.setText("Change");
                        iv_check_depr_grade.setImageResource(R.drawable.check);
                        pd_activity.propCh.isDeprGradeChecked = false;
                    } else {
                        bt_change_depr_grade.setText(tv_depr_grade.getText());
                        iv_check_depr_grade.setImageResource(R.drawable.check_gr);
                        pd_activity.propCh.isDeprGradeChecked = true;
                        pd_activity.propCh.deprGradeVal = property1.getCard().getDepr_grade();
                    }
                }
            });

            iv_check_heat_type.setOnClickListener(pd_activity.heatListener);
            iv_check_fuel.setOnClickListener(pd_activity.fuelListener);
            iv_check_ac.setOnClickListener(pd_activity.acListener);

            iv_check_bedrooms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pd_activity.propCh.isBedroomChecked) {
                        tv_new_bedrooms.setText("");
                        iv_check_bedrooms.setImageResource(R.drawable.check);
                        pd_activity.propCh.isBedroomChecked = false;
                    } else {
                        tv_new_bedrooms.setText(tv_bedrooms.getText());
                        iv_check_bedrooms.setImageResource(R.drawable.check_gr);
                        pd_activity.propCh.isBedroomChecked = true;
                        pd_activity.propCh.bedroomVal = property1.getCard().getBedroom_count();
                    }
                }
            });

            iv_check_bathrooms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pd_activity.propCh.isBathroomChecked) {
                        tv_new_bathrooms.setText("");
                        iv_check_bathrooms.setImageResource(R.drawable.check);
                        pd_activity.propCh.isBathroomChecked = false;
                    } else {
                        tv_new_bathrooms.setText(tv_bathrooms.getText());
                        iv_check_bathrooms.setImageResource(R.drawable.check_gr);
                        pd_activity.propCh.isBathroomChecked = true;
                        pd_activity.propCh.bathroomVal = property1.getCard().getBath_count();
                    }
                }
            });

            iv_check_half_bath.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pd_activity.propCh.isHalfbathChecked) {
                        tv_new_half_baths.setText("");
                        iv_check_half_bath.setImageResource(R.drawable.check);
                        pd_activity.propCh.isHalfbathChecked = false;
                    } else {
                        tv_new_half_baths.setText(tv_half_baths.getText());
                        iv_check_half_bath.setImageResource(R.drawable.check_gr);
                        pd_activity.propCh.isHalfbathChecked = true;
                        pd_activity.propCh.halfbathVal = property1.getCard().getHalf_bath_count();
                    }
                }
            });

            iv_check_other_rooms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pd_activity.propCh.isOtherRoomChecked) {
                        tv_new_other_rooms.setText("");
                        iv_check_other_rooms.setImageResource(R.drawable.check);
                        pd_activity.propCh.isOtherRoomChecked = false;
                    } else {
                        tv_new_other_rooms.setText(tv_other_rooms.getText());
                        iv_check_other_rooms.setImageResource(R.drawable.check_gr);
                        pd_activity.propCh.isOtherRoomChecked = true;
                        pd_activity.propCh.otherRoomVal = property1.getCard().getOther_rooms_count();
                    }
                }
            });

            iv_check_fixtures.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pd_activity.propCh.isFixturesChecked) {
                        tv_new_xtra_fixtures.setText("");
                        iv_check_fixtures.setImageResource(R.drawable.check);
                        pd_activity.propCh.isFixturesChecked = false;
                    } else {
                        tv_new_xtra_fixtures.setText(tv_xtra_fixtures.getText());
                        iv_check_fixtures.setImageResource(R.drawable.check_gr);
                        pd_activity.propCh.isFixturesChecked = true;
                        pd_activity.propCh.fixturesVal = property1.getCard().getExtra_fixtures();
                    }
                }
            });

        } else {

            basement_layout.setVisibility(View.VISIBLE);
            half_baths_etc_layout.setVisibility(View.GONE);
            inw_wall_floor_layout.setVisibility(View.GONE);

            bathrooms = 0;
            bedrooms = 0;
            other_rooms = 0;

            if (property2.prop_type == Constants.PROP_COMM) {
                bedroom_layout.setVisibility(View.GONE);
            } else {
                heat_fuel_layout.setVisibility(View.GONE);
                int_wall_height_layout.setVisibility(View.VISIBLE);
                basement_bedrooms_layout.setVisibility(View.VISIBLE);
                window_layout.setVisibility(View.VISIBLE);

                tv_bedrooms.setText(property2.getBedrooms());
                tv_int_wall_height.setText(property2.getInterior_Wall_Height());
                tv_basement_bedrooms.setText(property2.getBasement_Bedrooms());
                tv_window.setText(property2.getWindows());
            }

            tv_bathrooms.setText(property2.getBaths());
            tv_basement.setText(property2.getBasement());

            recalcTotal();

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            rv_spec_feat.setLayoutManager(linearLayoutManager);
            ext_spec_features_adapter = new UtilitiesRVAdapter(getActivity(), pd_activity.ext_spec, pd_activity.propCh.ext_specNew, pd_activity.feature_type, 81);
            rv_spec_feat.setAdapter(ext_spec_features_adapter);

            tv_heat_fuel.setText(property2.getHeating_Fuel());
            tv_heat_type.setText(property2.getHeating_Type());
            tv_ac_type.setText(property2.getAir_Conditioning());

            ceiling_layout.setVisibility(View.GONE);
            tv_depr_grade.setText(property2.getInterior_Condition());

        }

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

        sketch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DrawActivity.class);
                intent.putExtra("path", pd_activity.mySketchFile);

                getActivity().startActivityForResult(intent, 1210);
            }
        });

        if (!isBroom) {

            if (property1.prop_type == Constants.PROP_COMMERCIAL) {
                rooms_layout.setVisibility(View.GONE);
                ceiling_layout.setVisibility(View.VISIBLE);
            } else {
                ceiling_layout.setVisibility(View.GONE);
            }

        } else {



        }

        return view;
    }

    private void recalcTotal() {
        total_rooms = bedrooms + other_rooms;

        tv_total_rooms.setText(total_rooms + "");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 90 && resultCode == 1313) {
            String[] mKeys = Constants.Kitchen_values.keySet().toArray(new String[Constants.Kitchen_values.size()]);
            int pos = data.getIntExtra("pos", 0);

            bt_change_kitchen.setText(mKeys[pos] + " - " + Constants.Kitchen_values.get(mKeys[pos]));

            iv_check_kitchen.setImageResource(R.drawable.check_gr);
            pd_activity.propCh.isKitchenChecked = true;
            pd_activity.propCh.kitchenVal = mKeys[pos];

        } else if (requestCode == 91 && resultCode == 1313) {
            String[] mKeys = Constants.Bath_values.keySet().toArray(new String[Constants.Bath_values.size()]);
            int pos = data.getIntExtra("pos", 0);

            bt_change_bath_style.setText(mKeys[pos] + " - " + Constants.Bath_values.get(mKeys[pos]));

            iv_check_bath_style.setImageResource(R.drawable.check_gr);
            pd_activity.propCh.isBathStyleChecked = true;
            pd_activity.propCh.bathStyleVal = mKeys[pos];

        } else if (requestCode == 92 && resultCode == 1313) {
            String[] mKeys = Constants.Int_Wall_values.keySet().toArray(new String[Constants.Int_Wall_values.size()]);
            int pos = data.getIntExtra("pos", 0);

            pd_activity.int_wall.add(mKeys[pos] + " - " + Constants.Int_Wall_values.get(mKeys[pos]));
            pd_activity.propCh.int_wallNew.add(new NewValue(mKeys[pos], Constants.Int_Wall_values.get(mKeys[pos]), true));
            int_wall_adapter.notifyDataSetChanged();
        } else if (requestCode == 93 && resultCode == 1313) {
            String[] mKeys = Constants.Floor_values.keySet().toArray(new String[Constants.Floor_values.size()]);
            int pos = data.getIntExtra("pos", 0);

            pd_activity.floors.add(mKeys[pos] + " - " + Constants.Floor_values.get(mKeys[pos]));
            pd_activity.propCh.floorsNew.add(new NewValue(mKeys[pos], Constants.Floor_values.get(mKeys[pos]), true));
            floors_adapter.notifyDataSetChanged();
        } else if (requestCode == 94 && resultCode == 1313) {
            String[] mKeys = Constants.Ceiling_com_values.keySet().toArray(new String[Constants.Ceiling_com_values.size()]);
            int pos = data.getIntExtra("pos", 0);

            bt_change_ceiling_type.setText(mKeys[pos] + " - " + Constants.Ceiling_com_values.get(mKeys[pos]));

            iv_check_ceiling_type.setImageResource(R.drawable.check_gr);
            pd_activity.propCh.isCeilingTypeChecked = true;
            pd_activity.propCh.ceilingTypeVal = mKeys[pos];
        } else if (requestCode == 95 && resultCode == 1313) {
            String[] mKeys = Constants.Depreciation_values.keySet().toArray(new String[Constants.Depreciation_values.size()]);
            int pos = data.getIntExtra("pos", 0);

            bt_change_depr_grade.setText(mKeys[pos] + " - " + Constants.Depreciation_values.get(mKeys[pos]));

            iv_check_depr_grade.setImageResource(R.drawable.check_red);
            pd_activity.propCh.isDeprGradeChecked = true;
            pd_activity.propCh.deprGradeVal = mKeys[pos];
        }
    }
}
