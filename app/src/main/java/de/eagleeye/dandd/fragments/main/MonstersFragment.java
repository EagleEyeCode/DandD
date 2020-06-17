package de.eagleeye.dandd.fragments.main;

import android.database.Cursor;

import java.sql.ResultSet;

import de.eagleeye.dandd.R;
import de.eagleeye.dandd.fragments.base.BaseSQLFragment;

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
        return "SELECT * FROM items WHERE id = 1";
    }

    @Override
    public void onSQLiteQueryResult(Cursor cursor) {

    }
}
