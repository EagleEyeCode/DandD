package de.eagleeye.dandd.fragments.show;

import de.eagleeye.dandd.R;
import de.eagleeye.dandd.fragments.base.BaseDataShowFragment;

public class ItemsShowFragment extends BaseDataShowFragment {
    @Override
    protected String tableName() {
        return "items";
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_show_item;
    }
}
