package de.eagleeye.dandd.fragments.filter.spells;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import de.eagleeye.dandd.R;
import de.eagleeye.dandd.fragments.base.BaseFilterPartFragment;

public class FilterSpellsValuesPart extends BaseFilterPartFragment {
    private Spinner levelOperation;
    private EditText level;

    private RadioGroup v;
    private RadioGroup s;
    private RadioGroup m;
    private RadioGroup ritual;

//    private Spinner rangeUnit;
//    private Spinner rangeOperation;
//    private EditText range;
//
//    private Spinner durationUnit;
//    private Spinner durationOperation;
//    private EditText duration;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter_spells_values, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        levelOperation = view.findViewById(R.id.filter_spells_level_operation);
        level = view.findViewById(R.id.filter_spells_level_number);

        v = view.findViewById(R.id.filter_spells_v);
        s = view.findViewById(R.id.filter_spells_s);
        m = view.findViewById(R.id.filter_spells_m);
        ritual = view.findViewById(R.id.filter_spells_ritual);

//        rangeUnit = view.findViewById(R.id.filter_spells_range_unit);
//        rangeOperation = view.findViewById(R.id.filter_spells_range_operation);
//        range = view.findViewById(R.id.filter_spells_range_value);
//
//        durationUnit = view.findViewById(R.id.filter_spells_duration_unit);
//        durationOperation = view.findViewById(R.id.filter_spells_duration_operation);
//        duration = view.findViewById(R.id.filter_spells_duration_value);
    }

    @Override
    public String onFilterRequest(SharedPreferences prefs, Resources resources) {
        ArrayList<String> parts = new ArrayList<>();
        int levelOperationVal = prefs.getInt("filter_spells_level_operation", 0);
        int levelVal = prefs.getInt("filter_spells_level_number", 0);
        int vVal = prefs.getInt("filter_spells_level_v", R.id.filter_spells_v_both);
        int sVal = prefs.getInt("filter_spells_level_s", R.id.filter_spells_s_both);
        int mVal = prefs.getInt("filter_spells_level_m", R.id.filter_spells_m_both);
        int ritualVal = prefs.getInt("filter_spells_level_ritual", R.id.filter_spells_ritual_both);
//        int rangeUnitVal = prefs.getInt("filter_spells_range_Unit", 0);
//        int rangeOperationVal = prefs.getInt("filter_spells_range_operation", 0);
//        int rangeVal = prefs.getInt("filter_spells_range_value", 0);
//        int durationUnitVal = prefs.getInt("filter_spells_duration_Unit", 0);
//        int durationOperationVal = prefs.getInt("filter_spells_duration_operation", 0);
//        int durationVal = prefs.getInt("filter_spells_duration_value", 0);

        parts.add("IFNULL(spells.level, 0) " + resources.getStringArray(R.array.filter_operations)[levelOperationVal] + " " + levelVal);
        if(vVal == R.id.filter_spells_v_none) parts.add("spells.v ISNULL");
        if(vVal == R.id.filter_spells_v_only) parts.add("spells.v == 1");
        if(sVal == R.id.filter_spells_s_none) parts.add("spells.s ISNULL");
        if(sVal == R.id.filter_spells_s_only) parts.add("spells.s == 1");
        if(mVal == R.id.filter_spells_m_none) parts.add("spells.m ISNULL");
        if(mVal == R.id.filter_spells_m_only) parts.add("spells.m == 1");
        if(ritualVal == R.id.filter_spells_ritual_none) parts.add("spells.ritual ISNULL");
        if(ritualVal == R.id.filter_spells_ritual_only) parts.add("spells.ritual == 1");

//        parts.add("spells.range " + resources.getStringArray(R.array.filter_operations)[rangeOperationVal] + " " + rangeVal);
//        if(rangeUnitVal != 0) parts.add("spells.rangeUnit == " + (rangeUnitVal - 1));
//
//        parts.add("spells.duration " + resources.getStringArray(R.array.filter_operations)[durationOperationVal] + " " + durationVal);
//        if(durationUnitVal != 0) parts.add("spells.durationUnit == " + (durationUnitVal - 1));

        StringBuilder filter = new StringBuilder();
        for(String s : parts){
            if(filter.length() != 0) filter.append(" AND ");
            filter.append(s);
        }
        return filter.toString();
    }

    @Override
    protected void onLoad() {
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        levelOperation.setSelection(prefs.getInt("filter_spells_level_operation", 0));
        level.setText(String.valueOf(prefs.getInt("filter_spells_level_number", 0)));
        v.check(prefs.getInt("filter_spells_level_v", R.id.filter_spells_v_both));
        s.check(prefs.getInt("filter_spells_level_s", R.id.filter_spells_s_both));
        m.check(prefs.getInt("filter_spells_level_m", R.id.filter_spells_m_both));
        ritual.check(prefs.getInt("filter_spells_level_ritual", R.id.filter_spells_ritual_both));
//        rangeUnit.setSelection(prefs.getInt("filter_spells_range_Unit", 0));
//        rangeOperation.setSelection(prefs.getInt("filter_spells_range_operation", 0));
//        range.setText(String.valueOf(prefs.getInt("filter_spells_range_value", 0)));
//        durationUnit.setSelection(prefs.getInt("filter_spells_duration_Unit", 0));
//        durationOperation.setSelection(prefs.getInt("filter_spells_duration_operation", 0));
//        duration.setText(String.valueOf(prefs.getInt("filter_spells_duration_value", 0)));
    }

    @Override
    protected void onSave() {
        SharedPreferences.Editor edit = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
        edit.putInt("filter_spells_level_operation", levelOperation.getSelectedItemPosition());
        edit.putInt("filter_spells_level_number", Integer.parseInt(level.getText().toString()));
        edit.putInt("filter_spells_level_v", v.getCheckedRadioButtonId());
        edit.putInt("filter_spells_level_s", s.getCheckedRadioButtonId());
        edit.putInt("filter_spells_level_m", m.getCheckedRadioButtonId());
        edit.putInt("filter_spells_level_ritual", ritual.getCheckedRadioButtonId());
//        edit.putInt("filter_spells_range_Unit", rangeUnit.getSelectedItemPosition());
//        edit.putInt("filter_spells_range_operation", rangeOperation.getSelectedItemPosition());
//        edit.putInt("filter_spells_range_value", Integer.parseInt(range.getText().toString()));
//        edit.putInt("filter_spells_duration_Unit", durationUnit.getSelectedItemPosition());
//        edit.putInt("filter_spells_duration_operation", durationOperation.getSelectedItemPosition());
//        edit.putInt("filter_spells_duration_value", Integer.parseInt(duration.getText().toString()));
        edit.apply();
    }

    @Override
    protected void onClear(SharedPreferences prefs) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt("filter_spells_level_operation", 0);
        edit.putInt("filter_spells_level_number", 0);
        edit.putInt("filter_spells_level_v", R.id.filter_spells_v_both);
        edit.putInt("filter_spells_level_s", R.id.filter_spells_s_both);
        edit.putInt("filter_spells_level_m", R.id.filter_spells_m_both);
        edit.putInt("filter_spells_level_ritual", R.id.filter_spells_ritual_both);
//        edit.putInt("filter_spells_range_Unit", 0);
//        edit.putInt("filter_spells_range_operation", 0);
//        edit.putInt("filter_spells_range_value", 0);
//        edit.putInt("filter_spells_duration_Unit", 0);
//        edit.putInt("filter_spells_duration_operation", 0);
//        edit.putInt("filter_spells_duration_value", 0);
        edit.apply();
    }
}
