package de.eagleeye.dandd.fragments.base;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.eagleeye.dandd.activities.MainActivity;
import de.eagleeye.dandd.R;
import de.eagleeye.dandd.list.BasicListAdapter;
import de.eagleeye.dandd.list.BasicListItem;
import de.eagleeye.dandd.sql.BasicSQLiteHelper;
import de.eagleeye.dandd.sql.SQLRequest;

public abstract class BaseSQLFragment extends Fragment implements SQLRequest.OnQueryResult {
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(onLayoutID(), container, false);
        sqLiteHelper = new BasicSQLiteHelper(getActivity().getApplicationContext(), "data.db");

        list = rootView.findViewById(onRecyclerViewID());
        adapter = new BasicListAdapter(getActivity(), items);

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
                adapter.setSearch(search.getText().toString());
                getActivity().getPreferences(Context.MODE_PRIVATE).edit().putString(onTabName() + "_search", search.getText().toString()).apply();
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

        if(adapter.hasNoItems()) sqLiteHelper.query(request);

        if(getActivity() instanceof MainActivity) ((MainActivity) getActivity()).setOnFilterUpdate(() -> {
            search.setText(getActivity().getPreferences(Context.MODE_PRIVATE).getString(onTabName() + "_search", ""));
            adapter.setSearch(search.getText().toString());
            request = new SQLRequest(onQuery() + getFilter(), this);
            sqLiteHelper.query(request);
        });

        search.setText(getActivity().getPreferences(Context.MODE_PRIVATE).getString(onTabName() + "_search", ""));
        adapter.setSearch(search.getText().toString());

        return rootView;
    }

    private String getFilter(){
        if(!(getActivity() instanceof MainActivity)) return "";
        return ((MainActivity) getActivity()).getFilter();
    }

    protected void setItems(ArrayList<BasicListItem> itemList){
        this.items = itemList;
        adapter.setItems(this.items);
    }
}
