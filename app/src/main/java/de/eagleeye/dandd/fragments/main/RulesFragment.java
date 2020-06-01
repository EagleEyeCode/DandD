package de.eagleeye.dandd.fragments.main;

import java.io.File;

import de.eagleeye.dandd.R;
import de.eagleeye.dandd.fragments.base.BasePdfFragment;

public class RulesFragment extends BasePdfFragment {
    @Override
    protected int onLayoutID() {
        return R.layout.fragment_main_rules;
    }

    @Override
    protected int onPdfViewID() {
        return R.id.pdf_view_rules;
    }

    @Override
    protected File onFile() {
        return new File(getActivity().getFileStreamPath(""), "pdf/DnD_BasicRules_2018.pdf");
    }
}
