package de.eagleeye.dandd.fragments.filter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;

import de.eagleeye.dandd.R;
import de.eagleeye.dandd.fragments.base.BaseFilterFragment;
import de.eagleeye.dandd.list.FilterCheckListAdapter;
import de.eagleeye.dandd.list.FilterCheckListItem;
import de.eagleeye.dandd.list.NoScrollLinearLayoutManager;

public class ItemsFilterFragment extends BaseFilterFragment {
    private ExpandableLayout sortDropExpandable;
    private ExpandableLayout typeDropExpandable;
    private ExpandableLayout advancedDropExpandable;

    private ImageView sortDropImage;
    private ImageView typeDropImage;
    private ImageView advancedDropImage;


    private RadioGroup type;
    private RadioGroup direction;

    private RecyclerView types;
    private FilterCheckListAdapter typesAdapter;

    private RadioGroup magic;
    private Spinner valueOperation;
    private Spinner weightOperation;
    private EditText value;
    private EditText weight;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RelativeLayout sortDropLayout = view.findViewById(R.id.fragment_filter_items_sort_drop_layout);
        sortDropImage = view.findViewById(R.id.fragment_filter_items_sort_drop_image);
        sortDropExpandable = view.findViewById(R.id.fragment_filter_items_sort_layout);
        setupExpandable(sortDropLayout, sortDropImage, sortDropExpandable);

        RelativeLayout typeDropLayout = view.findViewById(R.id.fragment_filter_items_type_drop_layout);
        typeDropImage = view.findViewById(R.id.fragment_filter_items_type_drop_image);
        typeDropExpandable = view.findViewById(R.id.fragment_filter_items_type_layout);
        setupExpandable(typeDropLayout, typeDropImage, typeDropExpandable);

        RelativeLayout advancedDropLayout = view.findViewById(R.id.fragment_filter_items_advanced_drop_layout);
        advancedDropImage = view.findViewById(R.id.fragment_filter_items_advanced_drop_image);
        advancedDropExpandable = view.findViewById(R.id.fragment_filter_items_advanced_layout);
        setupExpandable(advancedDropLayout, advancedDropImage, advancedDropExpandable);

        advancedDropExpandable.setExpanded(true, false);
        advancedDropExpandable.setExpanded(false, false);


        type = view.findViewById(R.id.sort_type);
        direction = view.findViewById(R.id.sort_direction);

        types = view.findViewById(R.id.fragment_filter_items_types);
        typesAdapter = new FilterCheckListAdapter(getActivity(), "SELECT id, sourceId, name FROM itemTypes;");
        types.setLayoutManager(new NoScrollLinearLayoutManager(getActivity()));
        types.setHasFixedSize(true);
        types.setAdapter(typesAdapter);
        types.setNestedScrollingEnabled(false);

        magic = view.findViewById(R.id.filter_items_magic);
        valueOperation = view.findViewById(R.id.filter_items_value_operation);
        weightOperation = view.findViewById(R.id.filter_items_weight_operation);
        value = view.findViewById(R.id.filter_items_value_number);
        weight = view.findViewById(R.id.filter_items_weight_number);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public int onLayoutId() {
        return R.layout.fragment_filter_items;
    }

    @Override
    protected String onTabName() {
        return "items";
    }

    @Override
    protected void load() {
        if(getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences(onTabName(), Context.MODE_PRIVATE);

            type.check(prefs.getInt("items_sort_type", R.id.sort_type_items_name));
            direction.check(prefs.getInt("items_sort_direction", R.id.sort_direction_asc));

            magic.check(prefs.getInt("items_values_magic", R.id.filter_items_magic_both));
            valueOperation.setSelection(prefs.getInt("items_values_valueOperation", 0));
            weightOperation.setSelection(prefs.getInt("items_values_weightOperation", 0));
            value.setText(String.valueOf(prefs.getFloat("items_values_valueNumber", 0)));
            weight.setText(String.valueOf(prefs.getFloat("items_values_weightNumber", 0)));

            typesAdapter.load(prefs, onTabName());
        }
        super.load();
    }

    @Override
    protected void save() {
        if(getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences(onTabName(), Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = prefs.edit();

            edit.putInt("items_sort_type", type.getCheckedRadioButtonId());
            edit.putInt("items_sort_direction", direction.getCheckedRadioButtonId());

            edit.putInt("items_values_magic", magic.getCheckedRadioButtonId());
            edit.putInt("items_values_valueOperation", valueOperation.getSelectedItemPosition());
            edit.putInt("items_values_weightOperation", weightOperation.getSelectedItemPosition());
            edit.putFloat("items_values_valueNumber", Float.parseFloat(value.getText().toString()));
            edit.putFloat("items_values_weightNumber", Float.parseFloat(weight.getText().toString()));

            typesAdapter.save(edit, onTabName());

            edit.apply();
        }
        super.save();
    }

    @Override
    protected void clear() {
        closeExpendableIfOpen(sortDropExpandable, sortDropImage);
        closeExpendableIfOpen(typeDropExpandable, typeDropImage);
        closeExpendableIfOpen(advancedDropExpandable, advancedDropImage);
        super.clear();
    }

    @Override
    protected String generateFilter() {
        String typePart = getTypeFilterPart();
        if(typePart.length() == 0){
            return " WHERE " + getAdvancedFilterPart() + getSortFilterPart() + ";";
        }else{
            return " WHERE " + getAdvancedFilterPart() + " AND " + typePart + getSortFilterPart() + ";";
        }
    }

    private String getSortFilterPart(){
        if(getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences(onTabName(), Context.MODE_PRIVATE);
            String sort;
            String dir;
            if (prefs.getInt("items_sort_type", R.id.sort_type_items_name) == R.id.sort_type_items_name)
                sort = "items.name";
            else sort = "items.type";
            if (prefs.getInt("items_sort_direction", R.id.sort_direction_asc) == R.id.sort_direction_asc)
                dir = " ASC";
            else dir = " DESC";
            return " ORDER BY " + sort + dir;
        }else {
            return "";
        }
    }

    private String getTypeFilterPart(){
        if(getActivity() != null){
            ArrayList<FilterCheckListItem> items = typesAdapter.getItems();
            StringBuilder itemParts = new StringBuilder();
            for (FilterCheckListItem item : items){
                if(item.isChecked()){
                    if(itemParts.length() == 0) {
                        itemParts.append("((items.type=").append(item.getId()).append(" AND items.typeSourceId=").append(item.getSourceId()).append(")");
                    } else {
                        itemParts.append(" OR (items.type=").append(item.getId()).append(" AND items.typeSourceId=").append(item.getSourceId()).append(")");
                    }
                }
            }
            if(itemParts.length() != 0) itemParts.append(")");
            return itemParts.toString();
        }else {
            return "";
        }
    }

    private String getAdvancedFilterPart(){
        if(getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences(onTabName(), Context.MODE_PRIVATE);
            Resources resources = getActivity().getResources();
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
        }else {
            return "";
        }
    }
}
