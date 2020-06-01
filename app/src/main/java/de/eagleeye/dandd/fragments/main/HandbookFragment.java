package de.eagleeye.dandd.fragments.main;

import java.io.File;

import de.eagleeye.dandd.R;
import de.eagleeye.dandd.fragments.base.BasePdfFragment;

public class HandbookFragment extends BasePdfFragment {
    @Override
    protected int onLayoutID() {
        return R.layout.fragment_main_handbook;
    }

    @Override
    protected int onPdfViewID() {
        return R.id.pdf_view_handbook;
    }

    @Override
    protected File onFile() {
        return new File(getActivity().getFileStreamPath(""), "pdf/dnd5eng.pdf");
    }
}
