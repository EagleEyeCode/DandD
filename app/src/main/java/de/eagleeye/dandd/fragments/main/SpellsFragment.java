package de.eagleeye.dandd.fragments.main;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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
        return "SELECT DISTINCT spells.id, spells.sourceId, spells.name, spellSchools.name, files.path, IFNULL(spells.level, '0') FROM spells LEFT JOIN spellSchools ON spells.schoolId = spellSchools.id AND spells.schoolSourceID=spellSchools.sourceId LEFT JOIN files ON spellSchools.imageId = files.id AND spellSchools.imageSourceId=files.sourceId JOIN spellsClasses ON spells.id=spellsClasses.spellId AND spells.sourceId=spellsClasses.spellSourceId ";
    }

    @Override
    protected String onDefaultFilter() {
        return "ORDER BY spells.name ASC";
    }

    @Override
    public void onSQLiteQueryResult(Cursor cursor) {
        if (cursor == null) {
            setItems(new ArrayList<>());
            return;
        }
        if (cursor.moveToFirst()) {
            ArrayList<BasicListItem> items = new ArrayList<>();
            do {
                items.add(new BasicListItem(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), "Level " + cursor.getString(5) + " " + cursor.getString(3) + " Spell", cursor.getString(4)));
            } while (cursor.moveToNext());
            setItems(items);
        }
    }

    @Override
    public void onClick(BasicListItem item) {
        if(getActivity() != null) {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_main_content);
            Bundle args = new Bundle();
            args.putInt("id", item.getId());
            args.putInt("sourceId", item.getSourceId());
            navController.navigate(R.id.nav_spells_show, args);
        }
    }
}
