package de.eagleeye.dandd.fragments.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

import de.eagleeye.dandd.activities.MainActivity;
import de.eagleeye.dandd.R;

public abstract class BaseFilterFragment extends Fragment {
    private ArrayList<BaseFilterPartFragment> fragments;
    protected abstract ArrayList<BaseFilterPartFragment> getFragments();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_filter, null);

        fragments = getFragments();

        ViewPager viewPager = root.findViewById(R.id.filter_view_pager);
        MyViewPagerAdapter adapter = new MyViewPagerAdapter(getActivity().getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, fragments);
        viewPager.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroy() {
        if (getActivity() instanceof MainActivity) ((MainActivity) getActivity()).onFilterInputFinished(generateFilter());
        super.onDestroy();
    }

    private String generateFilter(){
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        String where = fragments.get(1).getFilterPart(prefs, getResources());
        String values = fragments.get(2).getFilterPart(prefs, getResources());

        if(where.isEmpty()){
            where = values;
        }else{
            if(!values.isEmpty()) where = where + " AND " + values;
        }

        if(where.isEmpty()){
            return " " + fragments.get(0).getFilterPart(prefs, getResources());
        }else{
            return " WHERE " + where + " " + fragments.get(0).getFilterPart(prefs, getResources());
        }
    }

    public void clear() {
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        for(BaseFilterPartFragment fragment : fragments){
            fragment.requestClear(prefs);
        }
        getActivity().onBackPressed();
    }

    private static class MyViewPagerAdapter extends FragmentStatePagerAdapter{
        private ArrayList<BaseFilterPartFragment> fragments;

        private MyViewPagerAdapter(@NonNull FragmentManager fm, int behavior, @NonNull ArrayList<BaseFilterPartFragment> fragments) {
            super(fm, behavior);
            this.fragments = fragments;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch(position){
                case 0: return "Search";
                case 1: return "Types";
                case 2: return "Values";
                default: return super.getPageTitle(position);
            }
        }
    }
}
