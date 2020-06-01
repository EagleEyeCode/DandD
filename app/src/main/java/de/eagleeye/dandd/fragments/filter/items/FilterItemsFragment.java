package de.eagleeye.dandd.fragments.filter.items;

import java.util.ArrayList;
import java.util.Arrays;

import de.eagleeye.dandd.fragments.base.BaseFilterFragment;
import de.eagleeye.dandd.fragments.base.BaseFilterPartFragment;
import de.eagleeye.dandd.fragments.filter.items.FilterItemsSearchPart;
import de.eagleeye.dandd.fragments.filter.items.FilterItemsTypesPart;
import de.eagleeye.dandd.fragments.filter.items.FilterItemsValuesPart;

public class FilterItemsFragment extends BaseFilterFragment {
    @Override
    protected ArrayList<BaseFilterPartFragment> getFragments() {
        return new ArrayList<>(Arrays.asList(new FilterItemsSearchPart(), new FilterItemsTypesPart(), new FilterItemsValuesPart()));
    }
}
