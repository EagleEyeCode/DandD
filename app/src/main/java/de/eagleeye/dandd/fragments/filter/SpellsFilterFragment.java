package de.eagleeye.dandd.fragments.filter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
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

public class SpellsFilterFragment extends BaseFilterFragment {
    private ExpandableLayout sortDropExpandable;
    private ExpandableLayout schoolsDropExpandable;
    private ExpandableLayout classesDropExpandable;
    private ExpandableLayout advancedDropExpandable;

    private ImageView sortDropImage;
    private ImageView schoolsDropImage;
    private ImageView classesDropImage;
    private ImageView advancedDropImage;


    private RadioGroup type;
    private RadioGroup direction;

    private RecyclerView schools;
    private FilterCheckListAdapter schoolsAdapter;

    private RecyclerView classes;
    private FilterCheckListAdapter classesAdapter;

    private Spinner levelOperation;
    private EditText level;

    private RadioGroup v;
    private RadioGroup s;
    private RadioGroup m;
    private RadioGroup ritual;
    
    @Override
    public int onLayoutId() {
        return R.layout.fragment_filter_spells;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RelativeLayout sortDropLayout = view.findViewById(R.id.fragment_filter_spells_sort_drop_layout);
        sortDropImage = view.findViewById(R.id.fragment_filter_spells_sort_drop_image);
        sortDropExpandable = view.findViewById(R.id.fragment_filter_spells_sort_layout);
        setupExpandable(sortDropLayout, sortDropImage, sortDropExpandable);

        RelativeLayout schoolsDropLayout = view.findViewById(R.id.fragment_filter_spells_schools_drop_layout);
        schoolsDropImage = view.findViewById(R.id.fragment_filter_spells_schools_drop_image);
        schoolsDropExpandable = view.findViewById(R.id.fragment_filter_spells_schools_layout);
        setupExpandable(schoolsDropLayout, schoolsDropImage, schoolsDropExpandable);

        RelativeLayout classesDropLayout = view.findViewById(R.id.fragment_filter_spells_classes_drop_layout);
        classesDropImage = view.findViewById(R.id.fragment_filter_spells_classes_drop_image);
        classesDropExpandable = view.findViewById(R.id.fragment_filter_spells_classes_layout);
        setupExpandable(classesDropLayout, classesDropImage, classesDropExpandable);

        RelativeLayout advancedDropLayout = view.findViewById(R.id.fragment_filter_spells_advanced_drop_layout);
        advancedDropImage = view.findViewById(R.id.fragment_filter_spells_advanced_drop_image);
        advancedDropExpandable = view.findViewById(R.id.fragment_filter_spells_advanced_layout);
        setupExpandable(advancedDropLayout, advancedDropImage, advancedDropExpandable);

        advancedDropExpandable.setExpanded(true, false);
        advancedDropExpandable.setExpanded(false, false);


        type = view.findViewById(R.id.sort_type);
        direction = view.findViewById(R.id.sort_direction);

        schools = view.findViewById(R.id.fragment_filter_spells_schools_list);
        schoolsAdapter = new FilterCheckListAdapter(getActivity(), "SELECT id, sourceId, name FROM spellSchools;");
        schools.setLayoutManager(new NoScrollLinearLayoutManager(getActivity()));
        schools.setHasFixedSize(true);
        schools.setAdapter(schoolsAdapter);
        schools.setNestedScrollingEnabled(false);

        classes = view.findViewById(R.id.fragment_filter_spells_classes_list);
        classesAdapter = new FilterCheckListAdapter(getActivity(), "SELECT id, sourceId, name FROM classes;");
        classes.setLayoutManager(new NoScrollLinearLayoutManager(getActivity()));
        classes.setHasFixedSize(true);
        classes.setAdapter(classesAdapter);
        classes.setNestedScrollingEnabled(false);

        levelOperation = view.findViewById(R.id.filter_spells_level_operation);
        level = view.findViewById(R.id.filter_spells_level_number);

        v = view.findViewById(R.id.filter_spells_v);
        s = view.findViewById(R.id.filter_spells_s);
        m = view.findViewById(R.id.filter_spells_m);
        ritual = view.findViewById(R.id.filter_spells_ritual);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected String onTabName() {
        return "spells";
    }

    @Override
    protected void load() {
        if(getActivity() != null){
            SharedPreferences prefs = getActivity().getSharedPreferences(onTabName(), Context.MODE_PRIVATE);

            type.check(prefs.getInt("spells_sort_type", R.id.sort_type_spells_name));
            direction.check(prefs.getInt("spells_sort_direction", R.id.sort_direction_asc));

            schoolsAdapter.load(prefs, "schools");

            classesAdapter.load(prefs, "classes");

            levelOperation.setSelection(prefs.getInt("filter_spells_level_operation", 0));
            level.setText(String.valueOf(prefs.getInt("filter_spells_level_number", 0)));
            v.check(prefs.getInt("filter_spells_level_v", R.id.filter_spells_v_both));
            s.check(prefs.getInt("filter_spells_level_s", R.id.filter_spells_s_both));
            m.check(prefs.getInt("filter_spells_level_m", R.id.filter_spells_m_both));
            ritual.check(prefs.getInt("filter_spells_level_ritual", R.id.filter_spells_ritual_both));
        }
        super.load();
    }

    @Override
    protected void save() {
        if(getActivity() != null){
            SharedPreferences prefs = getActivity().getSharedPreferences(onTabName(), Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = prefs.edit();

            edit.putInt("spells_sort_type", type.getCheckedRadioButtonId());
            edit.putInt("spells_sort_direction", direction.getCheckedRadioButtonId());

            schoolsAdapter.save(edit, "schools");

            classesAdapter.save(edit, "classes");

            edit.putInt("filter_spells_level_operation", levelOperation.getSelectedItemPosition());
            edit.putInt("filter_spells_level_number", Integer.parseInt(level.getText().toString()));
            edit.putInt("filter_spells_level_v", v.getCheckedRadioButtonId());
            edit.putInt("filter_spells_level_s", s.getCheckedRadioButtonId());
            edit.putInt("filter_spells_level_m", m.getCheckedRadioButtonId());
            edit.putInt("filter_spells_level_ritual", ritual.getCheckedRadioButtonId());

            edit.apply();
        }
        super.save();
    }

    @Override
    protected void clear() {
        closeExpendableIfOpen(sortDropExpandable, sortDropImage);
        closeExpendableIfOpen(schoolsDropExpandable, schoolsDropImage);
        closeExpendableIfOpen(classesDropExpandable, classesDropImage);
        closeExpendableIfOpen(advancedDropExpandable, advancedDropImage);
        super.clear();
    }

    @Override
    protected String generateFilter() {
        String[] parts = new String[]{getSchoolsFilterPart(), getClassesFilterPart(), getAdvancedFilterPart()};
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
                default:
                    throw new IllegalStateException("Unexpected value: " + prefs.getInt("spells_sort_type", R.id.sort_type_spells_name));
            }

            if(prefs.getInt("spells_sort_direction", R.id.sort_direction_asc) == R.id.sort_direction_asc) dir = " asc";
            else dir = " desc";
            return " ORDER BY " + sort + dir;
        }else {
            return "";
        }
    }

    private String getSchoolsFilterPart(){
        if(getActivity() != null){
            ArrayList<FilterCheckListItem> items = schoolsAdapter.getItems();
            StringBuilder itemParts = new StringBuilder();
            for (FilterCheckListItem item : items){
                if(item.isChecked()){
                    if(itemParts.length() == 0) {
                        itemParts.append("((spells.schoolId=").append(item.getId()).append(" AND spells.schoolSourceId=").append(item.getSourceId()).append(")");
                    } else {
                        itemParts.append(" OR (spells.schoolId=").append(item.getId()).append(" AND spells.schoolSourceId=").append(item.getSourceId()).append(")");
                    }
                }
            }
            if(itemParts.length() != 0) itemParts.append(")");
            return itemParts.toString();
        }else {
            return "";
        }
    }

    private String getClassesFilterPart(){
        if(getActivity() != null){
            ArrayList<FilterCheckListItem> items = classesAdapter.getItems();
            StringBuilder itemParts = new StringBuilder();
            for (FilterCheckListItem item : items){
                if(item.isChecked()){
                    if(itemParts.length() == 0) {
                        itemParts.append("((spellsClasses.classId=").append(item.getId()).append(" AND spellsClasses.classSourceId=").append(item.getSourceId()).append(")");
                    } else {
                        itemParts.append(" OR (spellsClasses.classId=").append(item.getId()).append(" AND spellsClasses.classSourceId=").append(item.getSourceId()).append(")");
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
            int levelOperationVal = prefs.getInt("filter_spells_level_operation", 0);
            int levelVal = prefs.getInt("filter_spells_level_number", 0);
            int vVal = prefs.getInt("filter_spells_level_v", R.id.filter_spells_v_both);
            int sVal = prefs.getInt("filter_spells_level_s", R.id.filter_spells_s_both);
            int mVal = prefs.getInt("filter_spells_level_m", R.id.filter_spells_m_both);
            int ritualVal = prefs.getInt("filter_spells_level_ritual", R.id.filter_spells_ritual_both);

            parts.add("IFNULL(spells.level, 0) " + resources.getStringArray(R.array.filter_operations)[levelOperationVal] + " " + levelVal);
            if(vVal == R.id.filter_spells_v_none) parts.add("spells.v LIKE 'null'");
            if(vVal == R.id.filter_spells_v_only) parts.add("spells.v == 1");
            if(sVal == R.id.filter_spells_s_none) parts.add("spells.s LIKE 'null'");
            if(sVal == R.id.filter_spells_s_only) parts.add("spells.s == 1");
            if(mVal == R.id.filter_spells_m_none) parts.add("spells.m LIKE 'null'");
            if(mVal == R.id.filter_spells_m_only) parts.add("spells.m == 1");
            if(ritualVal == R.id.filter_spells_ritual_none) parts.add("spells.ritual ISNULL");
            if(ritualVal == R.id.filter_spells_ritual_only) parts.add("spells.ritual == 1");

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
