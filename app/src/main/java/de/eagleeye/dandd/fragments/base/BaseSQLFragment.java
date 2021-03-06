package de.eagleeye.dandd.fragments.base;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.SQLException;
import java.util.ArrayList;

import de.eagleeye.dandd.activities.MainActivity;
import de.eagleeye.dandd.R;
import de.eagleeye.dandd.list.BasicListAdapter;
import de.eagleeye.dandd.list.BasicListItem;
import de.eagleeye.dandd.sql.BasicSQLiteHelper;
import de.eagleeye.dandd.sql.SQLRequest;

public abstract class BaseSQLFragment extends Fragment implements SQLRequest.OnQueryResult, BasicListAdapter.OnClick {
    private RecyclerView list;
    private BasicListAdapter adapter;
    private EditText search;

    private BasicSQLiteHelper sqLiteHelper;
    private SQLRequest request;
    private ArrayList<BasicListItem> items;

    protected abstract int onLayoutID();
    protected abstract int onRecyclerViewID();
    protected abstract String onTabName();
    protected abstract String onQuery();
    protected abstract String onDefaultFilter();

    private boolean paused;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(onLayoutID(), container, false);
        sqLiteHelper = new BasicSQLiteHelper(getActivity().getApplicationContext(), "data.db");

        list = rootView.findViewById(onRecyclerViewID());
        adapter = new BasicListAdapter(getActivity(), items);
        adapter.addListener(this);

        search = rootView.findViewById(R.id.et_search);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!paused) {
                    adapter.setSearch(s.toString());
                    getActivity().getSharedPreferences(onTabName(), Context.MODE_PRIVATE).edit().putString("search", s.toString()).apply();
                }
            }
        });

        request = new SQLRequest(onQuery() + getFilter(), this);
        items = new ArrayList<>();

        LinearLayoutManager layout =new LinearLayoutManager(getActivity().getApplicationContext());
        list.setLayoutManager(layout);
        list.setHasFixedSize(true);
        list.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                layout.scrollToPositionWithOffset(0,0);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(this::updateList, 20);
    }

    @Override
    public void onPause() {
        super.onPause();
        paused = true;
    }

    public void updateList(){
        if(getActivity() != null) {
            try {
                String searchText = getActivity().getSharedPreferences(onTabName(), Context.MODE_PRIVATE).getString("search", "");
                search.setText(searchText);
                adapter.setSearch(search.getText().toString());
                request = new SQLRequest(onQuery() + getFilter(), this);
                sqLiteHelper.query(request);
                paused = false;
            }catch (SQLiteException e){
                onSQLError(e);
            }
        }
    }

    public void onSQLError(SQLiteException e){
        Log.e(getClass().getSimpleName(), onTabName(), e);
        Toast.makeText(getActivity(), R.string.sql_error, Toast.LENGTH_LONG).show();
        if(getActivity() != null){
            getActivity().getSharedPreferences(onTabName(), Context.MODE_PRIVATE).edit().clear().apply();
        }
    }

    private String getFilter(){
        if(getActivity() != null) {
            return getActivity().getSharedPreferences(onTabName(), Context.MODE_PRIVATE).getString("filter", onDefaultFilter());
        }else {
            return "";
        }
    }

    protected void setItems(ArrayList<BasicListItem> itemList){
        this.items = itemList;
        adapter.setItems(this.items);
    }
}
