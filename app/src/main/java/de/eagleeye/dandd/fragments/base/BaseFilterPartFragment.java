package de.eagleeye.dandd.fragments.base;

import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.fragment.app.Fragment;

public abstract class BaseFilterPartFragment extends Fragment {
    @Override
    public void onStart() {
        super.onStart();
        onLoad();
    }

    public String getFilterPart(SharedPreferences prefs, Resources resources){
        if(isViewActive()) onSave();
        return onFilterRequest(prefs, resources);
    }

    @Override
    public void onPause() {
        if(isViewActive()) onSave();
        super.onPause();
    }

    public void requestClear(SharedPreferences prefs){
        onClear(prefs);
        if(isViewActive()) onLoad();
    }

    public boolean isViewActive(){
        return getView() != null;
    }

    public abstract String onFilterRequest(SharedPreferences prefs, Resources resources);
    protected abstract void onLoad();
    protected abstract void onSave();
    protected abstract void onClear(SharedPreferences prefs);
}
