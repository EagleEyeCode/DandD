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

public class MonstersFragment extends BaseSQLFragment {
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
        return "monsters";
    }

    @Override
    protected String onQuery() {
        return "SELECT monsters.id, monsters.sourceId, monsters.name, monsterTypes.name, files.path, f.path FROM monsters LEFT JOIN monsterTypes ON monsters.typeId=monsterTypes.id AND monsters.typeSourceId=monsterTypes.sourceId LEFT JOIN files ON monsterTypes.imageId = files.id AND monsterTypes.imageSourceId=files.sourceId LEFT JOIN files f ON monsters.imageId = f.id AND monsters.imageSourceId=f.sourceId ";
    }

    @Override
    protected String onDefaultFilter() {
        return " ORDER BY monsters.name ASC;";
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
                String image = cursor.getString(5);
                if(image == null) image = cursor.getString(4);
                BasicListItem item = new BasicListItem(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), firstCharacterToUpperCase(cursor.getString(3)), image);
                items.add(item);
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
            navController.navigate(R.id.nav_monsters_show, args);
        }
    }

    private String firstCharacterToUpperCase(String s){
        String s1 = s.substring(0, 1).toUpperCase();
        return s1 + s.substring(1);
    }
}
