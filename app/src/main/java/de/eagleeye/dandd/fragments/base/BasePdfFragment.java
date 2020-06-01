package de.eagleeye.dandd.fragments.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import de.eagleeye.dandd.activities.MainActivity;
import de.eagleeye.dandd.fragments.OnFilterUpdate;

public abstract class BasePdfFragment extends Fragment implements OnFilterUpdate {
    private PDFView pdfView;

    protected abstract int onLayoutID();
    protected abstract int onPdfViewID();
    protected abstract File onFile();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(onLayoutID(), container, false);
        pdfView = root.findViewById(onPdfViewID());
        try {
            InputStream is = new FileInputStream(onFile());
            int page = getActivity().getPreferences(Context.MODE_PRIVATE).getInt(onFile().getAbsoluteFile() + "PageNo", 0);
            pdfView.fromStream(is).onRender((nbPages, pageWidth, pageHeight) -> pdfView.fitToWidth(page)).load();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return root;
    }

    @Override
    public void onPause() {
        int page = pdfView.getCurrentPage();
        getActivity().getPreferences(Context.MODE_PRIVATE).edit().putInt(onFile().getAbsoluteFile() + "PageNo", page).apply();
        super.onPause();
    }

    private String getFilter(){
        if(!(getActivity() instanceof MainActivity)) return "";
        return ((MainActivity) getActivity()).getFilter();
    }

    public void onFilterUpdate(){
        pdfView.jumpTo(Integer.parseInt(getFilter()), true);
    }
}
