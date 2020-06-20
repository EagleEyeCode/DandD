package de.eagleeye.dandd.fragments.main;

import android.database.Cursor;
import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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
        return "SELECT items.id, items.sourceId, items.name, itemTypes.name, files.path FROM items LEFT JOIN itemTypes ON items.type = itemTypes.id AND items.typeSourceID=itemTypes.sourceId LEFT JOIN files ON itemTypes.imageId = files.id AND itemTypes.imageSourceId=files.sourceId ";
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
                items.add(new BasicListItem(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
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
            navController.navigate(R.id.nav_items_show, args);
        }
    }
}
