package com.secretpackage.inspector.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.secretpackage.inspector.Chooser;
import com.secretpackage.inspector.DrawActivity;
import com.secretpackage.inspector.MainActivity;
import com.secretpackage.inspector.MyApplication;
import com.secretpackage.inspector.OnSyncPageChangeListener;
import com.secretpackage.inspector.PropertyDetailsActivity;
import com.secretpackage.inspector.R;
import com.secretpackage.inspector.adapter.NewPhotoAdapter;
import com.secretpackage.inspector.adapter.NewPhotoPreviewAdapter;
import com.secretpackage.inspector.adapter.UtilitiesRVAdapter;
import com.secretpackage.inspector.model.NewValue;
import com.secretpackage.inspector.model.PhotoFile;
import com.secretpackage.inspector.model.Property;
import com.secretpackage.inspector.model.Property2;
import com.secretpackage.inspector.util.Constants;
import com.kinvey.android.Client;
import com.kinvey.android.callback.AsyncDownloaderProgressListener;
import com.kinvey.android.store.FileStore;
import com.kinvey.java.core.MediaHttpDownloader;
import com.kinvey.java.model.FileMetaData;
import com.kinvey.java.store.StoreType;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExteriorFragment extends Fragment {

    private static final String ARG_BROOM = "param0";
    private static final String ARG_PARAM1 = "param1";

    private Client myKinveyClient;
    private Property property1;
    private Property2 property2;

    private boolean isBroom;

    PropertyDetailsActivity pd_activity;

    public UtilitiesRVAdapter ext_wall_adapter;
    public UtilitiesRVAdapter ext_spec_features_adapter;

    public NewPhotoAdapter photoViewAdapter;
    public NewPhotoPreviewAdapter photoPreviewAdapter;

    @BindView(R.id.iv_photo) ImageView mainPhoto;
    @BindView(R.id.iv_sketch) public ImageView sketch;

    @BindView(R.id.photo_preview) ViewPager photoPreviewPager;
    @BindView(R.id.photo_pager) ViewPager photoPager;

    @BindView(R.id.tv_style) TextView tv_style;
    @BindView(R.id.tv_grade) TextView tv_grade;
    @BindView(R.id.tv_stories) TextView tv_stories;
    @BindView(R.id.tv_roof_struct) TextView tv_roof_struct;
    @BindView(R.id.tv_roof_cover) TextView tv_roof_cover;

    @BindView(R.id.tv_heat_fuel) public TextView tv_heat_fuel;
    @BindView(R.id.tv_heat_type) public TextView tv_heat_type;
    @BindView(R.id.tv_ac_type) public TextView tv_ac_type;

    @BindView(R.id.tv_building_use) TextView tv_building_use;
    @BindView(R.id.tv_ext_cond) TextView tv_ext_cond;
    @BindView(R.id.tv_story_height) TextView tv_story_height;
    @BindView(R.id.tv_foundation) TextView tv_foundation;
    @BindView(R.id.tv_frame) TextView tv_frame;

    @BindView(R.id.et_notes) public EditText et_notes;

    @BindView(R.id.bt_change_style) Button bt_change_style;
    @BindView(R.id.bt_change_grade) Button bt_change_grade;
    @BindView(R.id.bt_change_stories) Button bt_change_stories;
    @BindView(R.id.bt_change_roof_struct) Button bt_change_roof_struct;
    @BindView(R.id.bt_change_roof_cover) Button bt_change_roof_cover;

    @BindView(R.id.bt_change_heat_fuel) public Button bt_change_heat_fuel;
    @BindView(R.id.bt_change_heat_type) public Button bt_change_heat_type;
    @BindView(R.id.bt_change_ac_type) public Button bt_change_ac_type;

    @BindView(R.id.iv_check_heat_type) public ImageView iv_check_heat_type;
    @BindView(R.id.iv_check_fuel) public ImageView iv_check_fuel;
    @BindView(R.id.iv_check_ac) public ImageView iv_check_ac;

    @BindView(R.id.bt_take_photo) Button bt_take_photo;

    @BindView(R.id.iv_check_style) ImageView iv_check_style;
    @BindView(R.id.iv_check_grade) ImageView iv_check_grade;
    @BindView(R.id.iv_check_stories) ImageView iv_check_stories;
    @BindView(R.id.iv_check_roof_cov) ImageView iv_check_roof_cov;
    @BindView(R.id.iv_check_roof_str) ImageView iv_check_roof_str;

    @BindView(R.id.ext_wall_roof_layout) View ext_wall_roof_layout;
    @BindView(R.id.building_use_layout) View building_use_layout;
    @BindView(R.id.style_layout) View style_layout;
    @BindView(R.id.grade_layout) View grade_layout;
    @BindView(R.id.ext_cond_layout) View ext_cond_layout;
    @BindView(R.id.stor_layout) View stor_layout;
    @BindView(R.id.stor_height_layout) View stor_height_layout;
    @BindView(R.id.ext_wall_layout) View ext_wall_layout;
    @BindView(R.id.found_frame_layout) View found_frame_layout;
    @BindView(R.id.roof_layout) View roof_layout;
    @BindView(R.id.heat_fuel_layout) View heat_fuel_layout;

    @BindView(R.id.bt_add_spec_feat) Button bt_add_spec_feat;
    @BindView(R.id.bt_add_ext_wall) Button bt_add_ext_wall;
    @BindView(R.id.bt_del_spec_feat) Button bt_del_spec_feat;
    @BindView(R.id.bt_del_ext_wall) Button bt_del_ext_wall;

    @BindView(R.id.rv_spec_feat) RecyclerView rv_spec_feat;
    @BindView(R.id.rv_ext_wall) RecyclerView rv_ext_wall;

    @BindView(R.id.bt_main) Button bt_main;
    @BindView(R.id.bt_del) Button bt_del;

    public ExteriorFragment() {
        // Required empty public constructor
    }

    public static ExteriorFragment newInstance(int param1) {
        ExteriorFragment fragment = new ExteriorFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public static ExteriorFragment newInstance(boolean isBroom, String id_prop) {
        ExteriorFragment fragment = new ExteriorFragment();
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
        View view = inflater.inflate(R.layout.fragment_exterior, container, false);
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

        if (pd_activity.propCh.isStyleChecked) {
            iv_check_style.setImageResource(R.drawable.check_gr);
            bt_change_style.setText(pd_activity.propCh.styleVal + " - " + pd_activity.Style_values.get(pd_activity.propCh.styleVal));
        }

        if (pd_activity.propCh.isGradeChecked) {
            iv_check_grade.setImageResource(R.drawable.check_gr);
            bt_change_grade.setText(pd_activity.propCh.gradeVal + " - " + Constants.Grade_values.get(pd_activity.propCh.gradeVal));
        }

        if (pd_activity.propCh.isStoriesChecked) {
            iv_check_stories.setImageResource(R.drawable.check_gr);
            bt_change_stories.setText(pd_activity.propCh.storiesVal);
        }

        if (pd_activity.propCh.isRoofCovTypeChecked) {
            iv_check_roof_cov.setImageResource(R.drawable.check_gr);
            bt_change_roof_cover.setText(pd_activity.propCh.roofCovVal + " - " + Constants.Roof_Cover_values.get(pd_activity.propCh.roofCovVal));
        }

        if (pd_activity.propCh.isRoofStrChecked) {
            iv_check_roof_str.setImageResource(R.drawable.check_gr);
            bt_change_roof_struct.setText(pd_activity.propCh.roofStrVal + " - " + Constants.Roof_Structure_values.get(pd_activity.propCh.roofStrVal));
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

            tv_style.setText(property1.getCard().getStyle() + " - " + pd_activity.Style_values.get(property1.getCard().getStyle()));
            tv_grade.setText(property1.getCard().getGrade() + " - " + Constants.Grade_values.get(property1.getCard().getGrade()));
            tv_stories.setText(property1.getCard().getStories());
            tv_roof_struct.setText(property1.getCard().getRoof_structure() + " - " + Constants.Roof_Structure_values.get(property1.getCard().getRoof_structure()));
            tv_roof_cover.setText(property1.getCard().getRoof_cover() + " - " + Constants.Roof_Cover_values.get(property1.getCard().getRoof_cover()));

            tv_heat_fuel.setText(property1.getCard().getHeat_fuel() + " - " + Constants.Fuel_Type_values.get(property1.getCard().getHeat_fuel()));
            tv_heat_type.setText(property1.getCard().getHeat_type() + " - " + Constants.Heat_Type_values.get(property1.getCard().getHeat_type()));
            tv_ac_type.setText(property1.getCard().getAc_type() + " - " + Constants.AC_Type_values.get(property1.getCard().getAc_type()));

            bt_change_style.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getActivity(), Chooser.class);
                    intent.putExtra("type", pd_activity.style_type);
                    startActivityForResult(intent, 71);
                }
            });

            iv_check_style.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (pd_activity.propCh.isStyleChecked) {
                        bt_change_style.setText("Change");
                        iv_check_style.setImageResource(R.drawable.check);
                        pd_activity.propCh.isStyleChecked = false;
                    } else {
                        bt_change_style.setText(tv_style.getText());
                        iv_check_style.setImageResource(R.drawable.check_gr);
                        pd_activity.propCh.isStyleChecked = true;
                        pd_activity.propCh.styleVal = property1.getCard().getStyle();
                    }

                }
            });

            bt_change_grade.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getActivity(), Chooser.class);
                    intent.putExtra("type", Chooser.TYPE_GRADE);
                    startActivityForResult(intent, 72);
                }
            });

            iv_check_grade.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (pd_activity.propCh.isGradeChecked) {
                        bt_change_grade.setText("Change");
                        iv_check_grade.setImageResource(R.drawable.check);
                        pd_activity.propCh.isGradeChecked = false;
                    } else {
                        bt_change_grade.setText(tv_grade.getText());
                        iv_check_grade.setImageResource(R.drawable.check_gr);
                        pd_activity.propCh.isGradeChecked = true;
                        pd_activity.propCh.gradeVal = property1.getCard().getGrade();
                    }


                }
            });

            iv_check_stories.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (pd_activity.propCh.isStoriesChecked) {
                        bt_change_stories.setText("Change");
                        iv_check_stories.setImageResource(R.drawable.check);
                        pd_activity.propCh.isStoriesChecked = false;
                    } else {
                        bt_change_stories.setText(tv_stories.getText());
                        iv_check_stories.setImageResource(R.drawable.check_gr);
                        pd_activity.propCh.isStoriesChecked = true;
                        pd_activity.propCh.storiesVal = property1.getCard().getStories();
                    }

                }
            });

            bt_change_roof_struct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getActivity(), Chooser.class);
                    intent.putExtra("type", Chooser.TYPE_ROOF_STR);
                    startActivityForResult(intent, 74);
                }
            });

            iv_check_roof_str.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (pd_activity.propCh.isRoofStrChecked) {
                        bt_change_roof_struct.setText("Change");
                        iv_check_roof_str.setImageResource(R.drawable.check);
                        pd_activity.propCh.isRoofStrChecked = false;
                    } else {
                        bt_change_roof_struct.setText(tv_roof_struct.getText());
                        iv_check_roof_str.setImageResource(R.drawable.check_gr);
                        pd_activity.propCh.isRoofStrChecked = true;
                        pd_activity.propCh.roofStrVal = property1.getCard().getRoof_structure();
                    }

                }
            });

            bt_change_roof_cover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getActivity(), Chooser.class);
                    intent.putExtra("type", Chooser.TYPE_ROOF_COVER);
                    startActivityForResult(intent, 75);
                }
            });



            iv_check_roof_cov.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (pd_activity.propCh.isRoofCovTypeChecked) {
                        bt_change_roof_cover.setText("Change");
                        iv_check_roof_cov.setImageResource(R.drawable.check);
                        pd_activity.propCh.isRoofCovTypeChecked = false;
                    } else {
                        bt_change_roof_cover.setText(tv_roof_cover.getText());
                        iv_check_roof_cov.setImageResource(R.drawable.check_gr);
                        pd_activity.propCh.isRoofCovTypeChecked = true;
                        pd_activity.propCh.roofCovVal = property1.getCard().getRoof_cover();
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

            iv_check_heat_type.setOnClickListener(pd_activity.heatListener);
            iv_check_fuel.setOnClickListener(pd_activity.fuelListener);
            iv_check_ac.setOnClickListener(pd_activity.acListener);

        } else {

            if (property2.prop_type == Constants.PROP_COMM) {

                building_use_layout.setVisibility(View.VISIBLE);
                style_layout.setVisibility(View.GONE);
                ext_cond_layout.setVisibility(View.VISIBLE);
                stor_height_layout.setVisibility(View.VISIBLE);
                found_frame_layout.setVisibility(View.VISIBLE);

                tv_building_use.setText(property2.getBuilding_Use());
                tv_grade.setText(property2.getConstruction_Quality());
                tv_ext_cond.setText(property2.getExterior_Condition());
                tv_stories.setText(property2.getStories());
                tv_story_height.setText(property2.getStoryHeight());
                tv_foundation.setText(property2.getFoundation());
                tv_frame.setText(property2.getFrame());
                tv_roof_struct.setText(property2.getRoof_Structure());
                tv_roof_cover.setText(property2.getRoof_Cover());
                tv_heat_fuel.setText(property2.getHeating_Fuel());
                tv_heat_type.setText(property2.getHeating_Type());
                tv_ac_type.setText(property2.getAir_Conditioning());


            } else if (property2.prop_type == Constants.PROP_MULTRES) {

                stor_layout.setVisibility(View.GONE);
                grade_layout.setVisibility(View.GONE);
                ext_wall_layout.setVisibility(View.GONE);
                roof_layout.setVisibility(View.GONE);
                heat_fuel_layout.setVisibility(View.GONE);

                tv_style.setText(property2.getArchitectural_Style());

            } else if (property2.prop_type == Constants.PROP_SFR) {

                ext_cond_layout.setVisibility(View.VISIBLE);
                stor_layout.setVisibility(View.GONE);
                grade_layout.setVisibility(View.GONE);
                heat_fuel_layout.setVisibility(View.GONE);

                tv_style.setText(property2.getArchitectural_Style());
                tv_ext_cond.setText(property2.getExterior_Condition());
                tv_roof_struct.setText(property2.getRoof_Structure());
                tv_roof_cover.setText(property2.getRoof_Cover());
                tv_heat_type.setText(property2.getHeating_Type());
                tv_ac_type.setText(property2.getAir_Conditioning());

            }

        }



        photoPreviewAdapter = new NewPhotoPreviewAdapter(getContext(), pd_activity.photoInfos, 2);
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

        ((PropertyDetailsActivity)(getActivity())).photoPager2 = photoPager;

        if (pd_activity.sketchBitmap != null) {
            sketch.setImageBitmap(pd_activity.sketchBitmap);
        } else {
            Picasso.with(getContext()).load(pd_activity.mySketchFile).into(sketch);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rv_spec_feat.setLayoutManager(linearLayoutManager);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext());
        rv_ext_wall.setLayoutManager(linearLayoutManager1);

        ext_spec_features_adapter = new UtilitiesRVAdapter(getActivity(), pd_activity.ext_spec, pd_activity.propCh.ext_specNew, pd_activity.feature_type, 81);
        ext_wall_adapter = new UtilitiesRVAdapter(getActivity(), pd_activity.ext_wall, pd_activity.propCh.ext_wallNew, pd_activity.ext_wall_type, 82);

        rv_ext_wall.setAdapter(ext_wall_adapter);
        rv_spec_feat.setAdapter(ext_spec_features_adapter);

        bt_change_stories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent intent = new Intent(getActivity(), Chooser.class);
//                intent.putExtra("type", Chooser.TYPE_STORY);
//                startActivityForResult(intent, 73);

                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                final EditText edittext = new EditText(getContext());
                edittext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                alert.setMessage("Enter stories value");

                alert.setView(edittext);

                alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String stor_text = edittext.getText().toString();

                        float stor = 0f;
                        boolean ok = false;

                        try {
                            stor = Float.parseFloat(stor_text);

                            float remain = stor % 1;

                            if (remain == 0 || remain == 0.5 || remain == 0.75) {
                                ok = true;
                            }

                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            ok = false;
                        }

                        if (ok) {
                            bt_change_stories.setText(stor_text);

                            iv_check_stories.setImageResource(R.drawable.check_gr);
                            pd_activity.propCh.isStoriesChecked = true;
                            pd_activity.propCh.storiesVal = stor_text;
                        } else {
                            Toast.makeText(getContext(), "Error format", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                alert.show();

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

        bt_add_ext_wall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), Chooser.class);
                intent.putExtra("type", pd_activity.ext_wall_type);
                startActivityForResult(intent, 80);
            }
        });

        bt_del_ext_wall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pd_activity.ext_wall.size() > 0) {
                    pd_activity.ext_wall.remove(pd_activity.ext_wall.size()-1);
                    pd_activity.propCh.ext_wallNew.remove(pd_activity.propCh.ext_wallNew.size()-1);
                    ext_wall_adapter.notifyDataSetChanged();
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

//        if (property.prop_type == Constants.PROP_CONDO) {
//            ext_wall_roof_layout.setVisibility(View.GONE);
//        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 71 && resultCode == 1313) {
            String[] mKeys = pd_activity.Style_values.keySet().toArray(new String[pd_activity.Style_values.size()]);
            int pos = data.getIntExtra("pos", 0);

            bt_change_style.setText(mKeys[pos] + " - " + pd_activity.Style_values.get(mKeys[pos]));

            iv_check_style.setImageResource(R.drawable.check_red);
            pd_activity.propCh.isStyleChecked = true;
            pd_activity.propCh.styleVal = mKeys[pos];
        } else if (requestCode == 72 && resultCode == 1313) {
            String[] mKeys = Constants.Grade_values.keySet().toArray(new String[Constants.Grade_values.size()]);
            int pos = data.getIntExtra("pos", 0);

            bt_change_grade.setText(mKeys[pos] + " - " + Constants.Grade_values.get(mKeys[pos]));

            iv_check_grade.setImageResource(R.drawable.check_red);
            pd_activity.propCh.isGradeChecked = true;
            pd_activity.propCh.gradeVal = mKeys[pos];
        } else if (requestCode == 74 && resultCode == 1313) {
            int pos = data.getIntExtra("pos", 0);
            String[] mKeys = Constants.Roof_Structure_values.keySet().toArray(new String[Constants.Roof_Structure_values.size()]);

            bt_change_roof_struct.setText(mKeys[pos] + " - " + Constants.Roof_Structure_values.get(mKeys[pos]));

            iv_check_roof_str.setImageResource(R.drawable.check_gr);
            pd_activity.propCh.isRoofStrChecked = true;
            pd_activity.propCh.roofStrVal = mKeys[pos];
        } else if (requestCode == 75 && resultCode == 1313) {
            int pos = data.getIntExtra("pos", 0);
            String[] mKeys = Constants.Roof_Cover_values.keySet().toArray(new String[Constants.Roof_Cover_values.size()]);

            bt_change_roof_cover.setText(mKeys[pos] + " - " + Constants.Roof_Cover_values.get(mKeys[pos]));

            iv_check_roof_cov.setImageResource(R.drawable.check_gr);
            pd_activity.propCh.isRoofCovTypeChecked = true;
            pd_activity.propCh.roofCovVal = mKeys[pos];
        } else if (requestCode == 80 && resultCode == 1313) {
            int pos = data.getIntExtra("pos", 0);
            String[] mKeys = pd_activity.Ext_wall_values.keySet().toArray(new String[pd_activity.Ext_wall_values.size()]);

            pd_activity.ext_wall.add(mKeys[pos] + " - " + pd_activity.Ext_wall_values.get(mKeys[pos]));
            pd_activity.propCh.ext_wallNew.add(new NewValue(mKeys[pos], pd_activity.Ext_wall_values.get(mKeys[pos]), true));
            ext_wall_adapter.notifyDataSetChanged();
        }
    }

}
