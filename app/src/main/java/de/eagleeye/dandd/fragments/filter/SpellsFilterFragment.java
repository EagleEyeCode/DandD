package de.eagleeye.dandd.fragments.filter;

import de.eagleeye.dandd.R;
import de.eagleeye.dandd.fragments.base.BaseFilterFragment;

public class SpellsFilterFragment extends BaseFilterFragment {
    @Override
    public int onLayoutId() {
        return R.layout.fragment_filter_spells;
    }

    @Override
    protected String onTabName() {
        return "spells";
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
