package de.eagleeye.dandd.fragments.main;

import android.database.Cursor;
import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.ArrayList;

import de.eagleeye.dandd.R;
import de.eagleeye.dandd.activities.MainActivity;
import de.eagleeye.dandd.fragments.base.BaseSQLFragment;
import de.eagleeye.dandd.list.BasicListItem;

public class BooksFragment extends BaseSQLFragment {
    @Override
    protected int onLayoutID() {
        return R.layout.fragment_main_books;
    }

    @Override
    protected int onRecyclerViewID() {
        return R.id.fragment_main_books_list;
    }

    @Override
    protected String onTabName() {
        return "books";
    }

    @Override
    protected String onQuery() {
        return "SELECT files.id, files.sourceId, files.path, files.name, sources.name FROM files JOIN sources ON files.sourceId=sources.id WHERE files.typeSourceId=0 AND files.type=0;";
    }

    @Override
    protected String onDefaultFilter() {
        return ";";
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
                BasicListItem item = new BasicListItem(cursor.getInt(0), cursor.getInt(1), cursor.getString(3), "Source: " + cursor.getString(4), "");
                item.putExtra("path", cursor.getString(2));
                items.add(item);
            } while (cursor.moveToNext());
            setItems(items);
        }
    }

    @Override
    public void onClick(BasicListItem item) {
        if(getActivity() instanceof MainActivity){
            MainActivity activity = (MainActivity) getActivity();
            NavController navController = Navigation.findNavController(activity, R.id.nav_main_content);
            Bundle args = new Bundle();
            args.putString("name", item.getTitle());
            args.putString("path", item.getExtras().getString("path"));
            navController.navigate(R.id.nav_pdf, args);
        }
    }
}
