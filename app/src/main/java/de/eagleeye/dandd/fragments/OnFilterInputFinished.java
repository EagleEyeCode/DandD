package de.eagleeye.dandd.fragments;

public interface OnFilterInputFinished {
    void onFilterInputFinished(String filter);
    void showSnackbar(int filteredItemsCount);
}
