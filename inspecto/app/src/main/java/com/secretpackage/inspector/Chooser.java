package com.secretpackage.inspector;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.secretpackage.inspector.adapter.ChooserAdapter;
import com.secretpackage.inspector.util.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Chooser extends AppCompatActivity {

    public static final int TYPE_UTIL = 1;
    public static final int TYPE_LOC = 2;
    public static final int TYPE_TOP = 3;
    public static final int TYPE_OCCU_CODE = 4;

    public static final int TYPE_STYLE_RES = 5;
    public static final int TYPE_STYLE_COM = 51;
    public static final int TYPE_GRADE = 6;
    public static final int TYPE_OCCUPANCY = 7;
//    public static final int TYPE_STORY = 7;
    public static final int TYPE_ROOF_STR = 8;
    public static final int TYPE_ROOF_COVER = 9;
    public static final int TYPE_HEAT_FUEL = 10;
    public static final int TYPE_HEAT_TYPE = 11;
    public static final int TYPE_AC_TYPE = 12;
    public static final int TYPE_EXT_WALL_RES = 13;
    public static final int TYPE_EXT_WALL_COM = 131;

    public static final int TYPE_INT_WALL = 14;
    public static final int TYPE_FEATURES_RES = 15;
    public static final int TYPE_FEATURES_COM = 151;
    public static final int TYPE_FEATURES_COND = 152;
    public static final int TYPE_KITCHEN = 16;
    public static final int TYPE_BATH = 17;
    public static final int TYPE_FLOOR = 18;
    public static final int TYPE_CEILING = 19;

    public static final int TYPE_VISIT = 20;

    public static final int TYPE_DEPR_GRADE = 21;

    GridView gridview;
    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chooser);

        Intent intent = getIntent();
        int type = intent.getIntExtra("type", 0);
        pos = intent.getIntExtra("pos", 0);
        boolean multiselect = intent.getBooleanExtra("multiselect", false);

        if (type == 0) {
            finish();
        }

        gridview = (GridView) findViewById(R.id.gridview);

        Map<String, String> mList = null;

        switch (type) {
            case TYPE_UTIL:
                mList = Constants.Utility_values;
                break;
            case TYPE_LOC:
                mList = Constants.Location_values;
                break;
            case TYPE_TOP:
                mList = Constants.Topology_values;
                break;
            case TYPE_OCCU_CODE:
                mList = Constants.Occu_code_values;
                break;
            case TYPE_STYLE_RES:
                mList = Constants.Style_res_values;
                break;
            case TYPE_STYLE_COM:
                mList = Constants.Style_com_values;
                break;
            case TYPE_GRADE:
                mList = Constants.Grade_values;
                break;
            case TYPE_OCCUPANCY:
                mList = Constants.Occu_values;
                break;
//            case TYPE_STORY:
//                mList = Constants.Story_values;
//                break;
            case TYPE_ROOF_STR:
                mList = Constants.Roof_Structure_values;
                break;
            case TYPE_ROOF_COVER:
                mList = Constants.Roof_Cover_values;
                break;
            case TYPE_HEAT_FUEL:
                mList = Constants.Fuel_Type_values;
                break;
            case TYPE_HEAT_TYPE:
                mList = Constants.Heat_Type_values;
                break;
            case TYPE_AC_TYPE:
                mList = Constants.AC_Type_values;
                break;
            case TYPE_EXT_WALL_RES:
                mList = Constants.Ext_Wall_res_values;
                break;
            case TYPE_EXT_WALL_COM:
                mList = Constants.Ext_Wall_com_values;
                break;
            case TYPE_INT_WALL:
                mList = Constants.Int_Wall_values;
                break;
            case TYPE_FEATURES_RES:
                mList = Constants.Feature_res_values;
                break;
            case TYPE_FEATURES_COM:
                mList = Constants.Feature_com_values;
                break;
            case TYPE_FEATURES_COND:
                mList = Constants.Feature_cond_values;
                break;
            case TYPE_KITCHEN:
                mList = Constants.Kitchen_values;
                break;
            case TYPE_BATH:
                mList = Constants.Bath_values;
                break;
            case TYPE_FLOOR:
                mList = Constants.Floor_values;
                break;
            case TYPE_CEILING:
                mList = Constants.Ceiling_com_values;
                break;
            case TYPE_VISIT:
                mList = Constants.Visit_values;
                break;
            case TYPE_DEPR_GRADE:
                mList = Constants.Depreciation_values;
                break;
        }

        Button bt_ok = (Button) findViewById(R.id.bt_ok);

        if (multiselect) {

            final List<Integer> selectedList= new ArrayList<>();
            for (int i=0; i<mList.size(); i++) {
                selectedList.add(0);
            }

            bt_ok.setVisibility(View.VISIBLE);

            ChooserAdapter adapter = new ChooserAdapter(Chooser.this, mList, selectedList, true);
            gridview.setAdapter(adapter);

            bt_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    for (Boolean b : selectedList) {
//                        Log.e("reali", "b - " + b);
//                    }

                    Intent intent = new Intent();
                    intent.putIntegerArrayListExtra("selected_list", (ArrayList)selectedList);
                    intent.putExtra("sub_pos", pos);

                    setResult(1314, intent);
                    finish();

                }
            });
        } else {

            bt_ok.setVisibility(View.GONE);

            ChooserAdapter adapter = new ChooserAdapter(Chooser.this, mList);
            gridview.setAdapter(adapter);

            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = new Intent();
                    intent.putExtra("pos", position+1);
                    intent.putExtra("sub_pos", pos);

                    setResult(1313, intent);
                    finish();

                }
            });
        }
    }
}
