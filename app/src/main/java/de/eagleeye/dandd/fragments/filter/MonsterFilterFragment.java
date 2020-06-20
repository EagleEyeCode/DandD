package de.eagleeye.dandd.fragments.filter;

import de.eagleeye.dandd.R;
import de.eagleeye.dandd.fragments.base.BaseFilterFragment;

public class MonsterFilterFragment extends BaseFilterFragment {
    @Override
    public int onLayoutId() {
        return R.layout.fragment_filter_monsters;
    }

    @Override
    protected String onTabName() {
        return "monsters";
    }

    @Override
    protected void load() {
        super.load();
    }

    @Override
    protected void save() {
        super.save();
    }

    @Override
    protected void clear() {
        super.clear();
    }

    @Override
    protected String generateFilter() {
        return "";
    }
}
