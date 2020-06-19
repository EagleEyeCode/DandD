package de.eagleeye.dandd.fragments.filter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.cachapa.expandablelayout.ExpandableLayout;

import de.eagleeye.dandd.R;

public class ItemsFilterFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_items, null);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        RelativeLayout sortDropLayout = view.findViewById(R.id.fragment_filter_items_sort_drop_layout);
        ImageView sortDropImage = view.findViewById(R.id.fragment_filter_items_sort_drop_image);
        ExpandableLayout sortDropExpandable = view.findViewById(R.id.fragment_filter_items_sort_layout);
        setupExpandable(sortDropLayout, sortDropImage, sortDropExpandable);

        RelativeLayout typeDropLayout = view.findViewById(R.id.fragment_filter_items_type_drop_layout);
        ImageView typeDropImage = view.findViewById(R.id.fragment_filter_items_type_drop_image);
        ExpandableLayout typeDropExpandable = view.findViewById(R.id.fragment_filter_items_type_layout);
        setupExpandable(typeDropLayout, typeDropImage, typeDropExpandable);

        RelativeLayout advancedDropLayout = view.findViewById(R.id.fragment_filter_items_advanced_drop_layout);
        ImageView advancedDropImage = view.findViewById(R.id.fragment_filter_items_advanced_drop_image);
        ExpandableLayout advancedDropExpandable = view.findViewById(R.id.fragment_filter_items_advanced_layout);
        setupExpandable(advancedDropLayout, advancedDropImage, advancedDropExpandable);

        advancedDropExpandable.setExpanded(true, false);
        advancedDropExpandable.setExpanded(false, false);
    }

    private void setupExpandable(RelativeLayout dropLayout, ImageView dropImage, ExpandableLayout dropExpandable){
        dropLayout.setOnClickListener(v -> {
            if(dropExpandable.isExpanded()){
                dropImage.setImageResource(R.drawable.drop_down_foreground);
                dropExpandable.setExpanded(false, true);
            }else{
                dropImage.setImageResource(R.drawable.drop_up_foreground);
                dropExpandable.setExpanded(true, true);
            }
        });
    }

}
