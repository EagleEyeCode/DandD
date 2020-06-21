package de.eagleeye.dandd.fragments.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import de.eagleeye.dandd.sql.BasicSQLiteHelper;
import de.eagleeye.dandd.sql.SQLRequest;

public abstract class BaseDataShowFragment extends Fragment {
    private int id;
    private int sourceId;
    private String name;

    private BasicSQLiteHelper db;

    protected abstract String tableName();
    protected abstract int layoutId();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(layoutId(), null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if(args != null) {
            id = args.getInt("id");
            sourceId = args.getInt("sourceId");

            db = new BasicSQLiteHelper(getActivity(), "data.db");
            db.query(new SQLRequest("SELECT name FROM " + tableName() + " WHERE id=" + id + " AND sourceId=" + sourceId + ";", cursor -> {
                if (cursor != null && cursor.moveToFirst()) {
                    name = cursor.getString(0);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(name);
                }
            }));
        }
    }

    protected int getDataId(){
        return id;
    }

    protected int getDataSourceId(){
        return sourceId;
    }

    protected BasicSQLiteHelper getDb(){
        return db;
    }
}
