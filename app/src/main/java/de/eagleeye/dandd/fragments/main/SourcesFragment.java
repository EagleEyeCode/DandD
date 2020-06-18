package de.eagleeye.dandd.fragments.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.eagleeye.dandd.R;
import de.eagleeye.dandd.list.SourceListAdapter;

public class SourcesFragment extends Fragment {
    private RecyclerView list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_sources, null);
        list = view.findViewById(R.id.sources_list);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SourceListAdapter adapter = new SourceListAdapter(getActivity());
        list.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        list.setHasFixedSize(true);
        list.setAdapter(adapter);
    }
}
