package de.eagleeye.dandd.fragments.base;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;

import de.eagleeye.dandd.activities.MainActivity;
import de.eagleeye.dandd.R;

public abstract class BaseFilterFragment extends Fragment {
    private EditText search;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(onLayoutId(), null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        search = view.findViewById(R.id.et_search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(getActivity() != null) {
                    getActivity().getSharedPreferences(onTabName(), Context.MODE_PRIVATE).edit().putString("search", s.toString()).apply();
                }
            }
        });

        setHasOptionsMenu(true);
        load();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.filter_clear) {
            clear();
            load();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        save();
    }

    protected void setupExpandable(RelativeLayout dropLayout, ImageView dropImage, ExpandableLayout dropExpandable){
        dropLayout.setOnClickListener(v -> {
            if(dropExpandable.isExpanded()){
                dropImage.setImageResource(R.drawable.drop_down_foreground);
                dropExpandable.setExpanded(false, true);
            }else{
                dropImage.setImageResource(R.drawable.drop_up_foreground);
                dropExpandable.setExpanded(true, true);
            }
        });
    }

    protected void closeExpendableIfOpen(ExpandableLayout expandable, ImageView dropImage){
        if(expandable.isExpanded()) {
            expandable.setExpanded(false, true);
            dropImage.setImageResource(R.drawable.drop_down_foreground);
        }
    }

    protected void load() {
        if(getActivity() != null) {
            String searchText = getActivity().getSharedPreferences(onTabName(), Context.MODE_PRIVATE).getString("search", "");
            search.setText(searchText);
        }
    }

    protected void save() {
        if(getActivity() != null) {
            getActivity().getSharedPreferences(onTabName(), Context.MODE_PRIVATE).edit().putString("filter", generateFilter()).apply();
        }
    }

    protected void clear() {
        if(getActivity() != null){
            getActivity().getSharedPreferences(onTabName(), Context.MODE_PRIVATE).edit().clear().apply();
        }
    }

    protected abstract int onLayoutId();
    protected abstract String onTabName();
    protected abstract String generateFilter();
}
