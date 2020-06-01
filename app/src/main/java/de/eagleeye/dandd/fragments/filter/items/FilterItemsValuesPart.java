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
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.eagleeye.dandd.R;
import de.eagleeye.dandd.fragments.base.BaseFilterPartFragment;

public class FilterItemsValuesPart extends BaseFilterPartFragment {
    private RadioGroup magic;
    private Spinner valueOperation;
    private Spinner weightOperation;
    private EditText value;
    private EditText weight;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter_items_values, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        magic = view.findViewById(R.id.filter_items_magic);
        valueOperation = view.findViewById(R.id.filter_items_value_operation);
        weightOperation = view.findViewById(R.id.filter_items_weight_operation);
        value = view.findViewById(R.id.filter_items_value_number);
        weight = view.findViewById(R.id.filter_items_weight_number);
    }

    @Override
    public String onFilterRequest(SharedPreferences prefs, Resources resources) {
        String magicStr = "";
        int magicSel = prefs.getInt("items_values_magic", R.id.filter_items_magic_both);
        if(magicSel == R.id.filter_items_magic_only) magicStr = " AND items.magic == '1'";
        if(magicSel == R.id.filter_items_magic_none) magicStr = " AND items.magic == '0'";

        String[] operations = resources.getStringArray(R.array.filter_operations);

        String ifNullValue = " IFNULL(items.value, 0) ";
        String ifNullWeight = " IFNULL(items.weight, 0) ";
        if(prefs.getFloat("items_values_valueNumber", 0) != 0) ifNullValue = " items.value ";
        if(prefs.getFloat("items_values_weightNumber", 0) != 0) ifNullWeight = " items.weight ";

        return ifNullValue + operations[prefs.getInt("items_values_valueOperation", 0)] + " " + prefs.getFloat("items_values_valueNumber", 0) +
               " AND" + ifNullWeight + operations[prefs.getInt("items_values_weightOperation", 0)] + " " + prefs.getFloat("items_values_weightNumber", 0) +
                magicStr;
    }

    @Override
    public void onLoad() {
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        magic.check(prefs.getInt("items_values_magic", 1));
        valueOperation.setSelection(prefs.getInt("items_values_valueOperation", 0));
        weightOperation.setSelection(prefs.getInt("items_values_weightOperation", 0));
        value.setText(String.valueOf(prefs.getFloat("items_values_valueNumber", 0)));
        weight.setText(String.valueOf(prefs.getFloat("items_values_weightNumber", 0)));
    }

    @Override
    public void onSave() {
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt("items_values_magic", magic.getCheckedRadioButtonId());
        edit.putInt("items_values_valueOperation", valueOperation.getSelectedItemPosition());
        edit.putInt("items_values_weightOperation", weightOperation.getSelectedItemPosition());
        edit.putFloat("items_values_valueNumber", Float.parseFloat(value.getText().toString()));
        edit.putFloat("items_values_weightNumber", Float.parseFloat(weight.getText().toString()));
        edit.apply();
    }

    @Override
    public void onClear(SharedPreferences prefs) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt("items_values_magic", R.id.filter_items_magic_both);
        edit.putInt("items_values_valueOperation", 0);
        edit.putInt("items_values_weightOperation", 0);
        edit.putFloat("items_values_valueNumber", 0);
        edit.putFloat("items_values_weightNumber", 0);
        edit.apply();
    }
}
