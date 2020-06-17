package de.eagleeye.dandd.fragments.main;

import android.database.Cursor;

import java.sql.ResultSet;
import java.util.ArrayList;

import de.eagleeye.dandd.R;
import de.eagleeye.dandd.fragments.base.BaseSQLFragment;
import de.eagleeye.dandd.list.BasicListItem;

public class SpellsFragment extends BaseSQLFragment {
    @Override
    protected int onLayoutID() {
        return R.layout.fragment_main_basic_list;
    }

    @Override
    protected int onRecyclerViewID() {
        return R.id.fragment_basic_list_list_view;
    }

    @Override
    protected String onTabName() {
        return "spells";
    }

    @Override
    protected String onQuery() {
        return "SELECT spells.name, spellSchools.name, files.path, IFNULL(spells.level, 0) FROM spells LEFT JOIN spellSchools ON spells.schoolId = spellSchools.id LEFT JOIN files ON spellSchools.imageId = files.id ";
    }

    @Override
    public void onSQLiteQueryResult(Cursor cursor) {
        if(cursor == null){
            setItems(new ArrayList<>());
            return;
        }
        if (cursor.moveToFirst()) {
            ArrayList<BasicListItem> items = new ArrayList<>();
            do {
                items.add(new BasicListItem(0, cursor.getString(0), "Level " + cursor.getString(3) + " " + cursor.getString(1) + " Spell", cursor.getString(2)));
            } while (cursor.moveToNext());
            setItems(items);
        }
    }
}
