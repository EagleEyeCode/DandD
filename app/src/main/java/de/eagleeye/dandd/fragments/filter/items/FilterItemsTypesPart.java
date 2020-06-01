package de.eagleeye.dandd.fragments.filter.items;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import de.eagleeye.dandd.R;
import de.eagleeye.dandd.fragments.base.BaseFilterPartFragment;

public class FilterItemsTypesPart extends BaseFilterPartFragment {
    private ArrayList<CheckBox> checkBoxes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter_items_types, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkBoxes = new ArrayList<>();
        for(int i = 1; i <= 16; i++){
            checkBoxes.add(view.findViewById(getId(i)));
        }
    }

    @Override
    public String onFilterRequest(SharedPreferences prefs, Resources resources) {
        ArrayList<Integer> checked = new ArrayList<>();
        for(int i = 1; i <= 16; i++){
            if(prefs.getBoolean("items_types_" + i, false)) checked.add(i);
        }
        if(checked.size() == 0) return "";
        String middle = "";
        for(Integer i : checked){
            if(!middle.isEmpty()) middle = middle + ", ";
            middle = middle + i;
        }
        return "items.type IN ("+middle+") ";
    }

    @Override
    public void onLoad() {
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        for(int i = 1; i <= 16; i++){
            checkBoxes.get(i - 1).setChecked(prefs.getBoolean("items_types_" + i, false));
        }
    }

    @Override
    public void onSave() {
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        for(int i = 1; i <= 16; i++){
            edit.putBoolean("items_types_" + i, checkBoxes.get(i - 1).isChecked());
        }
        edit.apply();
    }

    @Override
    public void onClear(SharedPreferences prefs) {
        SharedPreferences.Editor edit = prefs.edit();
        for(int i = 1; i <= 16; i++){
            edit.putBoolean("items_types_" + i, false);
        }
        edit.apply();
    }

    private int getId(int i){
        switch(i){
            case 1: return R.id.filter_items_type_1;
            case 2: return R.id.filter_items_type_2;
            case 3: return R.id.filter_items_type_3;
            case 4: return R.id.filter_items_type_4;
            case 5: return R.id.filter_items_type_5;
            case 6: return R.id.filter_items_type_6;
            case 7: return R.id.filter_items_type_7;
            case 8: return R.id.filter_items_type_8;
            case 9: return R.id.filter_items_type_9;
            case 10: return R.id.filter_items_type_10;
            case 11: return R.id.filter_items_type_11;
            case 12: return R.id.filter_items_type_12;
            case 13: return R.id.filter_items_type_13;
            case 14: return R.id.filter_items_type_14;
            case 15: return R.id.filter_items_type_15;
            case 16: return R.id.filter_items_type_16;
            default: return -1;
        }
    }
}
