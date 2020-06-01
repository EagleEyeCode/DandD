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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.eagleeye.dandd.R;
import de.eagleeye.dandd.fragments.base.BaseFilterPartFragment;

public class FilterSpellsSearchPart extends BaseFilterPartFragment {
    private EditText search;
    private RadioGroup type;
    private RadioGroup direction;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter_spells_search, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        search = view.findViewById(R.id.et_search);
        type = view.findViewById(R.id.sort_type);
        direction = view.findViewById(R.id.sort_direction);
    }

    @Override
    public String onFilterRequest(SharedPreferences prefs, Resources resources) {
        String sort;
        String dir;

        switch(prefs.getInt("spells_sort_type", R.id.sort_type_spells_name)){
            case R.id.sort_type_spells_name:
                sort = "spells.name";
                break;
            case R.id.sort_type_spells_school:
                sort = "spells.schoolId";
                break;
            case R.id.sort_type_spells_level:
                sort = "spells.level";
                break;
            case R.id.sort_type_spells_range:
                sort = "spells.rangeUnit, spells.range";
                break;
            case R.id.sort_type_spells_duration:
                sort = "spells.durationUnit, spells.duration";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + prefs.getInt("spells_sort_type", R.id.sort_type_spells_name));
        }

        if(prefs.getInt("spells_sort_direction", R.id.sort_direction_asc) == R.id.sort_direction_asc) dir = " asc";
        else dir = " desc";
        return " ORDER BY " + sort + dir;
    }

    @Override
    protected void onLoad() {
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        search.setText(prefs.getString("spells_search", ""));
        type.check(prefs.getInt("spells_sort_type", R.id.sort_type_spells_name));
        direction.check(prefs.getInt("spells_sort_direction", R.id.sort_direction_asc));
    }

    @Override
    protected void onSave() {
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("spells_search", search.getText().toString());
        edit.putInt("spells_sort_type", type.getCheckedRadioButtonId());
        edit.putInt("spells_sort_direction", direction.getCheckedRadioButtonId());
        edit.apply();
    }

    @Override
    protected void onClear(SharedPreferences prefs) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("spells_search", "");
        edit.putInt("spells_sort_type", R.id.sort_type_spells_name);
        edit.putInt("spells_sort_direction", R.id.sort_direction_asc);
        edit.apply();
    }
}
