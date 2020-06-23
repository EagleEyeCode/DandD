package de.eagleeye.dandd.fragments.filter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
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

public class MonsterFilterFragment extends BaseFilterFragment {
    private ExpandableLayout sortDropExpandable;
    private ExpandableLayout alignmentsDropExpandable;
    private ExpandableLayout typesDropExpandable;
    private ExpandableLayout advancedDropExpandable;

    private ImageView sortDropImage;
    private ImageView alignmentsDropImage;
    private ImageView typesDropImage;
    private ImageView advancedDropImage;

    private RadioGroup type;
    private RadioGroup direction;

    private RecyclerView alignments;
    private FilterCheckListAdapter alignmentsAdapter;

    private RecyclerView types;
    private FilterCheckListAdapter typesAdapter;

    private Spinner hpOperation;
    private EditText hp;

    private Spinner sizeOperation;
    private EditText size;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RelativeLayout sortDropLayout = view.findViewById(R.id.fragment_filter_monsters_sort_drop_layout);
        sortDropImage = view.findViewById(R.id.fragment_filter_monsters_sort_drop_image);
        sortDropExpandable = view.findViewById(R.id.fragment_filter_monsters_sort_layout);
        setupExpandable(sortDropLayout, sortDropImage, sortDropExpandable);

        RelativeLayout alignmentsDropLayout = view.findViewById(R.id.fragment_filter_monsters_alignment_drop_layout);
        alignmentsDropImage = view.findViewById(R.id.fragment_filter_monsters_alignment_drop_image);
        alignmentsDropExpandable = view.findViewById(R.id.fragment_filter_monsters_alignment_layout);
        setupExpandable(alignmentsDropLayout, alignmentsDropImage, alignmentsDropExpandable);

        RelativeLayout typesDropLayout = view.findViewById(R.id.fragment_filter_monsters_type_drop_layout);
        typesDropImage = view.findViewById(R.id.fragment_filter_monsters_type_drop_image);
        typesDropExpandable = view.findViewById(R.id.fragment_filter_monsters_type_layout);
        setupExpandable(typesDropLayout, typesDropImage, typesDropExpandable);

        RelativeLayout advancedDropLayout = view.findViewById(R.id.fragment_filter_monsters_advanced_drop_layout);
        advancedDropImage = view.findViewById(R.id.fragment_filter_monsters_advanced_drop_image);
        advancedDropExpandable = view.findViewById(R.id.fragment_filter_monsters_advanced_layout);
        setupExpandable(advancedDropLayout, advancedDropImage, advancedDropExpandable);

        advancedDropExpandable.setExpanded(true, false);
        advancedDropExpandable.setExpanded(false, false);



        type = view.findViewById(R.id.sort_type);
        direction = view.findViewById(R.id.sort_direction);

        alignments = view.findViewById(R.id.fragment_filter_monsters_alignments_list);
        alignmentsAdapter = new FilterCheckListAdapter(getActivity(), "SELECT id, sourceId, text FROM alignments;");
        alignments.setLayoutManager(new NoScrollLinearLayoutManager(getActivity()));
        alignments.setHasFixedSize(true);
        alignments.setAdapter(alignmentsAdapter);
        alignments.setNestedScrollingEnabled(false);

        types = view.findViewById(R.id.fragment_filter_monsters_types_list);
        typesAdapter = new FilterCheckListAdapter(getActivity(), "SELECT id, sourceId, name FROM monsterTypes;");
        types.setLayoutManager(new NoScrollLinearLayoutManager(getActivity()));
        types.setHasFixedSize(true);
        types.setAdapter(typesAdapter);
        types.setNestedScrollingEnabled(false);

        hpOperation = view.findViewById(R.id.filter_monsters_hp_operation);
        hp = view.findViewById(R.id.filter_monsters_hp_number);

        sizeOperation = view.findViewById(R.id.filter_monsters_size_operation);
        size = view.findViewById(R.id.filter_monsters_size_number);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public int onLayoutId() {
        return R.layout.fragment_filter_monsters;
    }

    @Override
    protected String onTabName() {
        return "monsters";
    }

    @Override
    protected void load() {
        if(getActivity() != null){
            SharedPreferences prefs = getActivity().getSharedPreferences(onTabName(), Context.MODE_PRIVATE);

            type.check(prefs.getInt("monsters_sort_type", R.id.sort_type_monsters_name));
            direction.check(prefs.getInt("monsters_sort_direction", R.id.sort_direction_asc));

            alignmentsAdapter.load(prefs, "alignments");

            typesAdapter.load(prefs, "types");

            hpOperation.setSelection(prefs.getInt("filter_monsters_hp_operation", 0));
            hp.setText(String.valueOf(prefs.getInt("filter_monsters_hp_number", 0)));

            sizeOperation.setSelection(prefs.getInt("filter_monsters_size_operation", 0));
            size.setText(String.valueOf(prefs.getInt("filter_monsters_size_number", 0)));
        }
        super.load();
    }

    @Override
    protected void save() {
        if(getActivity() != null){
            SharedPreferences prefs = getActivity().getSharedPreferences(onTabName(), Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = prefs.edit();

            edit.putInt("monsters_sort_type", type.getCheckedRadioButtonId());
            edit.putInt("monsters_sort_direction", direction.getCheckedRadioButtonId());

            alignmentsAdapter.save(edit, "alignments");

            typesAdapter.save(edit, "types");

            edit.putInt("filter_monsters_hp_operation", hpOperation.getSelectedItemPosition());
            edit.putInt("filter_monsters_hp_number", Integer.parseInt(hp.getText().toString()));

            edit.putInt("filter_monsters_size_operation", sizeOperation.getSelectedItemPosition());
            edit.putInt("filter_monsters_size_number", Integer.parseInt(size.getText().toString()));

            edit.apply();
        }
        super.save();
    }

    @Override
    protected void clear() {
        closeExpendableIfOpen(sortDropExpandable, sortDropImage);
        closeExpendableIfOpen(alignmentsDropExpandable, alignmentsDropImage);
        closeExpendableIfOpen(typesDropExpandable, typesDropImage);
        closeExpendableIfOpen(advancedDropExpandable, advancedDropImage);

        super.clear();
    }

    @Override
    protected String generateFilter() {
        String[] parts = new String[]{getAlignmentsFilterPart(), getTypesFilterPart(), getAdvancedFilterPart()};
        StringBuilder wherePart = new StringBuilder();
        for(String part : parts){
            if(part.length() != 0){
                if(wherePart.length() == 0){
                    wherePart.append(" WHERE ");
                }else {
                    wherePart.append(" AND ");
                }
                wherePart.append(part);
            }
        }
        return wherePart.toString() + " " + getSortFilterPart() + ";";
    }

    private String getSortFilterPart(){
        if(getActivity() != null){
            SharedPreferences prefs = getActivity().getSharedPreferences(onTabName(), Context.MODE_PRIVATE);
            String sort;
            String dir;

            switch(prefs.getInt("monsters_sort_type", R.id.sort_type_monsters_name)){
                case R.id.sort_type_monsters_name:
                    sort = "monsters.name";
                    break;
                case R.id.sort_type_monsters_type:
                    sort = "monsters.typeId, monsters.typeSourceId";
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + prefs.getInt("monsters_sort_type", R.id.sort_type_monsters_name));
            }

            if(prefs.getInt("monsters_sort_direction", R.id.sort_direction_asc) == R.id.sort_direction_asc) dir = " asc";
            else dir = " desc";
            return " ORDER BY " + sort + dir;
        }else {
            return "";
        }
    }

    private String getAlignmentsFilterPart(){
        if(getActivity() != null){
            ArrayList<FilterCheckListItem> items = alignmentsAdapter.getItems();
            StringBuilder itemParts = new StringBuilder();
            for (FilterCheckListItem item : items){
                if(item.isChecked()){
                    if(itemParts.length() == 0) {
                        itemParts.append("((monsters.alignmentId=").append(item.getId()).append(" AND monsters.alignmentSourceId=").append(item.getSourceId()).append(")");
                    } else {
                        itemParts.append(" OR (monsters.alignmentId=").append(item.getId()).append(" AND monsters.alignmentSourceId=").append(item.getSourceId()).append(")");
                    }
                }
            }
            if(itemParts.length() != 0) itemParts.append(")");
            return itemParts.toString();
        }else {
            return "";
        }
    }

    private String getTypesFilterPart(){
        if(getActivity() != null){
            ArrayList<FilterCheckListItem> items = typesAdapter.getItems();
            StringBuilder itemParts = new StringBuilder();
            for (FilterCheckListItem item : items){
                if(item.isChecked()){
                    if(itemParts.length() == 0) {
                        itemParts.append("((monsters.typeId=").append(item.getId()).append(" AND monsters.typeSourceId=").append(item.getSourceId()).append(")");
                    } else {
                        itemParts.append(" OR (monsters.typeId=").append(item.getId()).append(" AND monsters.typeSourceId=").append(item.getSourceId()).append(")");
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
        if(getActivity() != null){
            SharedPreferences prefs = getActivity().getSharedPreferences(onTabName(), Context.MODE_PRIVATE);
            Resources resources = getResources();
            ArrayList<String> parts = new ArrayList<>();
            int hpOperationVal = prefs.getInt("filter_monsters_hp_operation", 0);
            int hpVal = prefs.getInt("filter_monsters_hp_number", 0);
            int sizeOperationVal = prefs.getInt("filter_monsters_size_operation", 0);
            int sizeVal = prefs.getInt("filter_monsters_size_number", 0);

            parts.add("IFNULL(monsters.hpMax, 0) " + resources.getStringArray(R.array.filter_operations)[hpOperationVal] + " " + hpVal);
            parts.add("IFNULL(monsters.size, 0) " + resources.getStringArray(R.array.filter_operations)[sizeOperationVal] + " " + sizeVal);

            StringBuilder filter = new StringBuilder();
            for(String s : parts){
                if(filter.length() != 0) filter.append(" AND ");
                filter.append(s);
            }
            return filter.toString();
        }else{
            return "";
        }
    }
}
