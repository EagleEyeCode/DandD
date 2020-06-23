package de.eagleeye.dandd.fragments.show;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.eagleeye.dandd.R;
import de.eagleeye.dandd.fragments.base.BaseDataShowFragment;
import de.eagleeye.dandd.list.BasicShowListAdapter;
import de.eagleeye.dandd.list.BasicShowListItem;
import de.eagleeye.dandd.list.FilterCheckListAdapter;
import de.eagleeye.dandd.list.NoScrollLinearLayoutManager;
import de.eagleeye.dandd.sql.BasicSQLiteHelper;
import de.eagleeye.dandd.sql.SQLRequest;

public class SpellsShowFragment extends BaseDataShowFragment {
    private RecyclerView list;
    private TextView text;

    private ArrayList<BasicShowListItem> items;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        list = view.findViewById(R.id.fragment_show_spell_list);
        text = view.findViewById(R.id.fragment_show_spell_text);

        items = new ArrayList<>();

        BasicSQLiteHelper db = new BasicSQLiteHelper(getActivity(), "data.db");

        SQLRequest main = new SQLRequest("SELECT spellSchools.name, spells.level, spells.range, spells.duration, spells.time, spells.materials, spells.text, spells.v, spells.s, spells.m, spells.ritual FROM spells LEFT JOIN spellSchools ON spells.schoolId=spellSchools.id AND spells.schoolSourceId=spellSchools.sourceId WHERE spells.id=" + getDataId() + " AND spells.sourceId=" + getDataSourceId() + ";", cursor -> {
            if(cursor != null && cursor.moveToFirst()) {
                items.add(new BasicShowListItem("School:", cursor.getString(0)));
                items.add(new BasicShowListItem("Level:", cursor.getString(1)));
                items.add(new BasicShowListItem("Range:", cursor.getString(2)));
                items.add(new BasicShowListItem("Duration:", cursor.getString(3)));
                items.add(new BasicShowListItem("Time:", cursor.getString(4)));

                ArrayList<String> components = new ArrayList<>();
                if(cursor.getInt(7) == 1) components.add("V");
                if(cursor.getInt(8) == 1) components.add("S");
                if(cursor.getInt(9) == 1) components.add("M");
                if(cursor.getInt(10) == 1) components.add("Ritual");

                StringBuilder componentsStr = new StringBuilder();
                for(String s : components){
                    if(componentsStr.length() != 0) componentsStr.append(", ");
                    componentsStr.append(s);
                }

                if(componentsStr.length() != 0){
                    items.add(new BasicShowListItem("Components:", componentsStr.toString()));
                }

                String materials = cursor.getString(5);
                if(materials != null && !materials.equals("null")) {
                    items.add(new BasicShowListItem("Materials:", materials));
                }

                SpannableStringBuilder str = new SpannableStringBuilder("Description:   " + cursor.getString(6));
                str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                text.setText(str);
            }
        });

        SQLRequest classes = new SQLRequest("SELECT classes.name FROM classes JOIN spellsClasses ON classes.id=spellsClasses.classId AND classes.sourceId=spellsClasses.classSourceId WHERE spellsClasses.spellId=" + getDataId() + " AND spellsClasses.spellSourceId=" + getDataSourceId() + ";", cursor -> {
            if(cursor != null && cursor.moveToFirst()) {
                StringBuilder str = new StringBuilder();
                do{
                    if(str.length() != 0) str.append(", ");
                    str.append(cursor.getString(0));
                } while (cursor.moveToNext());
                if(cursor.getCount() == 1) {
                    items.add(new BasicShowListItem("Class:", str.toString()));
                }else {
                    items.add(new BasicShowListItem("Classes:", str.toString()));
                }
            }
        });


        db.query(main, true);
        db.query(classes, true);

        BasicShowListAdapter adapter = new BasicShowListAdapter();
        adapter.setItems(items);
        list.setLayoutManager(new NoScrollLinearLayoutManager(getActivity()));
        list.setHasFixedSize(true);
        list.setAdapter(adapter);
        list.setNestedScrollingEnabled(false);
    }

    @Override
    protected String tableName() {
        return "spells";
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_show_spell;
    }
}
