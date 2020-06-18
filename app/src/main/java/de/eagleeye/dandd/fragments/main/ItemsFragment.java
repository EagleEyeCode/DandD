package de.eagleeye.dandd.fragments.main;

import android.database.Cursor;

import java.sql.ResultSet;
import java.util.ArrayList;

import de.eagleeye.dandd.R;
import de.eagleeye.dandd.fragments.base.BaseSQLFragment;
import de.eagleeye.dandd.list.BasicListItem;

public class ItemsFragment extends BaseSQLFragment {
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
        return "items";
    }

    @Override
    protected String onQuery() {
        return "SELECT items.name, itemTypes.name, files.path FROM items LEFT JOIN itemTypes ON items.type = itemTypes.id AND items.typeSourceID=itemTypes.sourceId LEFT JOIN files ON itemTypes.imageId = files.id AND itemTypes.imageSourceId=files.sourceId ";
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
                items.add(new BasicListItem(0, cursor.getString(0), cursor.getString(1), cursor.getString(2)));
            } while (cursor.moveToNext());
            setItems(items);
        }
    }
}
