package de.eagleeye.dandd.fragments.filter.spells;

import java.util.ArrayList;
import java.util.Arrays;

import de.eagleeye.dandd.fragments.base.BaseFilterFragment;
import de.eagleeye.dandd.fragments.base.BaseFilterPartFragment;

public class FilterSpellsFragment extends BaseFilterFragment {
    @Override
    protected ArrayList<BaseFilterPartFragment> getFragments() {
        return new ArrayList<>(Arrays.asList(new FilterSpellsSearchPart(), new FilterSpellsTypesPart(), new FilterSpellsValuesPart()));
    }
}
