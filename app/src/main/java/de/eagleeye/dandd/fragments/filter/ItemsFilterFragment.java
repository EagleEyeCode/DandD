package de.eagleeye.dandd.fragments.filter;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.cachapa.expandablelayout.ExpandableLayout;

import de.eagleeye.dandd.R;
import de.eagleeye.dandd.fragments.base.BaseFilterFragment;

public class ItemsFilterFragment extends BaseFilterFragment {
    private ExpandableLayout sortDropExpandable;
    private ExpandableLayout typeDropExpandable;
    private ExpandableLayout advancedDropExpandable;

    private ImageView sortDropImage;
    private ImageView typeDropImage;
    private ImageView advancedDropImage;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        RelativeLayout sortDropLayout = view.findViewById(R.id.fragment_filter_items_sort_drop_layout);
        sortDropImage = view.findViewById(R.id.fragment_filter_items_sort_drop_image);
        sortDropExpandable = view.findViewById(R.id.fragment_filter_items_sort_layout);
        setupExpandable(sortDropLayout, sortDropImage, sortDropExpandable);

        RelativeLayout typeDropLayout = view.findViewById(R.id.fragment_filter_items_type_drop_layout);
        typeDropImage = view.findViewById(R.id.fragment_filter_items_type_drop_image);
        typeDropExpandable = view.findViewById(R.id.fragment_filter_items_type_layout);
        setupExpandable(typeDropLayout, typeDropImage, typeDropExpandable);

        RelativeLayout advancedDropLayout = view.findViewById(R.id.fragment_filter_items_advanced_drop_layout);
        advancedDropImage = view.findViewById(R.id.fragment_filter_items_advanced_drop_image);
        advancedDropExpandable = view.findViewById(R.id.fragment_filter_items_advanced_layout);
        setupExpandable(advancedDropLayout, advancedDropImage, advancedDropExpandable);

        advancedDropExpandable.setExpanded(true, false);
        advancedDropExpandable.setExpanded(false, false);
    }

    @Override
    public int onLayoutId() {
        return R.layout.fragment_filter_items;
    }

    @Override
    protected String onTabName() {
        return "items";
    }

    @Override
    protected void load() {
        super.load();
    }

    @Override
    protected void save() {
        super.save();
    }

    @Override
    protected void clear() {
        super.clear();
        closeExpendableIfOpen(sortDropExpandable, sortDropImage);
        closeExpendableIfOpen(typeDropExpandable, typeDropImage);
        closeExpendableIfOpen(advancedDropExpandable, advancedDropImage);
    }

    @Override
    protected String generateFilter() {
        return "";
    }
}
