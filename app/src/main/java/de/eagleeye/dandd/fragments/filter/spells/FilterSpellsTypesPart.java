package de.eagleeye.dandd.fragments.filter.spells;

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

public class FilterSpellsTypesPart extends BaseFilterPartFragment {
    private ArrayList<CheckBox> schools;
    private ArrayList<CheckBox> classes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter_spells_types, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        schools = new ArrayList<>();
        classes = new ArrayList<>();
        for(int i = 1; i <= 8; i++){
            schools.add(view.findViewById(getSchoolId(i - 1)));
        }

        for(int i = 1; i <= 8; i++){
            classes.add(view.findViewById(getClassId(i - 1)));
        }
    }
    @Override
    public String onFilterRequest(SharedPreferences prefs, Resources resources) {
        ArrayList<Integer> checkedSchools = new ArrayList<>();
        for(int i = 1; i <= 8; i++){
            if(prefs.getBoolean("spells_types_school_" + i, false)) checkedSchools.add(i - 1);
        }

        ArrayList<Integer> checkedClasses = new ArrayList<>();
        for(int i = 1; i <= 8; i++){
            if(prefs.getBoolean("spells_types_class_" + i, false)) checkedClasses.add(i - 1);
        }

        String schoolsPart;
        if(checkedSchools.size() != 0) {
            String middleSchools = "";
            for (Integer i : checkedSchools) {
                if (!middleSchools.isEmpty()) middleSchools = middleSchools + ", ";
                middleSchools = middleSchools + i;
            }
            schoolsPart = " spells.schoolId IN (" + middleSchools + ") ";
        }else{
            schoolsPart = "";
        }

        String classPart;
        if(checkedClasses.size() != 0) {
            String middleClass = "";
            for (Integer i : checkedClasses) {
                if (!middleClass.isEmpty()) middleClass = middleClass + ", ";
                middleClass = middleClass + i;
            }
            classPart = " spells.id IN (SELECT spellId FROM spellsClasses WHERE classId IN (" + middleClass + ")) AND spells.sourceId IN (SELECT spellSourceId FROM spellsClasses WHERE classId IN (" + middleClass + ")) ";
        }else{
            classPart = "";
        }

        String returnVal = schoolsPart;
        if(!returnVal.isEmpty()){
            if(!classPart.isEmpty()) returnVal = returnVal + "AND" + classPart;
        }else{
            returnVal = classPart;
        }

        return returnVal;
    }

    @Override
    protected void onLoad() {
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        for(int i = 1; i <= 8; i++){
            schools.get(i - 1).setChecked(prefs.getBoolean("spells_types_school_" + i, false));
        }

        for(int i = 1; i <= 8; i++){
            classes.get(i - 1).setChecked(prefs.getBoolean("spells_types_class_" + i, false));
        }
    }

    @Override
    protected void onSave() {
        SharedPreferences.Editor edit = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
        for(int i = 1; i <= 8; i++){
            edit.putBoolean("spells_types_school_" + i, schools.get(i -1).isChecked());
        }

        for(int i = 1; i <= 8; i++){
            edit.putBoolean("spells_types_class_" + i, classes.get(i -1).isChecked());
        }
        edit.apply();
    }

    @Override
    protected void onClear(SharedPreferences prefs) {
        SharedPreferences.Editor edit = prefs.edit();
        for(int i = 1; i <= 8; i++){
            edit.putBoolean("spells_types_school_" + i, false);
        }

        for(int i = 1; i <= 8; i++){
            edit.putBoolean("spells_types_class_" + i, false);
        }
        edit.apply();
    }

    private int getSchoolId(int i){
        switch (i){
            case 0: return R.id.filter_spells_type_school_1;
            case 1: return R.id.filter_spells_type_school_2;
            case 2: return R.id.filter_spells_type_school_3;
            case 3: return R.id.filter_spells_type_school_4;
            case 4: return R.id.filter_spells_type_school_5;
            case 5: return R.id.filter_spells_type_school_6;
            case 6: return R.id.filter_spells_type_school_7;
            case 7: return R.id.filter_spells_type_school_8;
            default: return -1;
        }
    }

    private int getClassId(int i){
        switch (i){
            case 0: return R.id.filter_spells_type_class_1;
            case 1: return R.id.filter_spells_type_class_2;
            case 2: return R.id.filter_spells_type_class_3;
            case 3: return R.id.filter_spells_type_class_4;
            case 4: return R.id.filter_spells_type_class_5;
            case 5: return R.id.filter_spells_type_class_6;
            case 6: return R.id.filter_spells_type_class_7;
            case 7: return R.id.filter_spells_type_class_8;
            default: return -1;
        }
    }
}
