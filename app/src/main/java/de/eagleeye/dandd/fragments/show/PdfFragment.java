package de.eagleeye.dandd.fragments.show;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

import de.eagleeye.dandd.R;

public class PdfFragment extends Fragment {
    private PDFView pdfView;
    private File file;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main_pdf, container, false);
        pdfView = root.findViewById(R.id.fragment_main_pdf_view);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if(args != null && getActivity() != null){
            if(args.containsKey("name")){
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(args.getString("name"));
            }
            if(args.containsKey("path")){
                file = new File(getActivity().getFileStreamPath(""), Objects.requireNonNull(args.getString("path")));
                loadFile(file);
            }
        }
    }

    @Override
    public void onPause() {
        if(getActivity() != null) {
            int page = pdfView.getCurrentPage();
            String key = file.getAbsoluteFile() + "PageNo";
            getActivity().getSharedPreferences("books", Context.MODE_PRIVATE).edit().putInt(key, page).apply();
        }
        super.onPause();
    }

    public void jumpToLastSeenPage(){
        if(getActivity() != null) {
            String key = file.getAbsoluteFile() + "PageNo";
            int page = getActivity().getSharedPreferences("books", Context.MODE_PRIVATE).getInt(key, 0);
            pdfView.jumpTo(page, true);
        }
    }

    public void loadFile(File file){
        try {
            if(getActivity() != null) {
                InputStream is = new FileInputStream(file);
                pdfView.fromStream(is).onLoad(nbPages -> jumpToLastSeenPage()).load();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
