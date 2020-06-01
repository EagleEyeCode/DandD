package de.eagleeye.dandd.fragments.filter.items;

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

public class FilterItemsSearchPart extends BaseFilterPartFragment {
    private EditText search;
    private RadioGroup type;
    private RadioGroup direction;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter_items_search, null);
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
        if(prefs.getInt("items_sort_type", R.id.sort_type_items_name) == R.id.sort_type_items_name) sort = "items.name";
        else sort = "items.type";
        if(prefs.getInt("items_sort_direction", R.id.sort_direction_asc) == R.id.sort_direction_asc) dir = " asc";
        else dir = " desc";
        return " ORDER BY " + sort + dir;
    }

    @Override
    public void onLoad() {
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        search.setText(prefs.getString("items_search", ""));
        type.check(prefs.getInt("items_sort_type", R.id.sort_type_items_name));
        direction.check(prefs.getInt("items_sort_direction", R.id.sort_direction_asc));
    }

    @Override
    public void onSave() {
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("items_search", search.getText().toString());
        edit.putInt("items_sort_type", type.getCheckedRadioButtonId());
        edit.putInt("items_sort_direction", direction.getCheckedRadioButtonId());
        edit.apply();
    }

    @Override
    public void onClear(SharedPreferences prefs) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("items_search", "");
        edit.putInt("items_sort_type", R.id.sort_type_items_name);
        edit.putInt("items_sort_direction", R.id.sort_direction_asc);
        edit.apply();
    }
}
