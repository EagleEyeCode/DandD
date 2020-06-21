package de.eagleeye.dandd.fragments.show;

import de.eagleeye.dandd.R;
import de.eagleeye.dandd.fragments.base.BaseDataShowFragment;

public class MonstersModelShowFragment extends BaseDataShowFragment {
    @Override
    protected String tableName() {
        return "monsters";
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_show_monster_model;
    }
}
