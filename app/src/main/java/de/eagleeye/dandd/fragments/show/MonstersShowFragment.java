package de.eagleeye.dandd.fragments.show;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.eagleeye.dandd.R;
import de.eagleeye.dandd.activities.MainActivity;
import de.eagleeye.dandd.fragments.base.BaseDataShowFragment;
import de.eagleeye.dandd.sql.SQLRequest;

public class MonstersShowFragment extends BaseDataShowFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getActivity() instanceof MainActivity) {
            if(hasModel()) {
                ((MainActivity) getActivity()).setModelMonsterId(new int[]{getDataId(), getDataSourceId()});
            }else {
                ((MainActivity) getActivity()).setModelMonsterId(null);
            }
        }
    }

    @Override
    protected String tableName() {
        return "monsters";
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_show_monster;
    }

    private boolean hasModel(){
        final boolean[] returnVal = {false};
        getDb().query(new SQLRequest("SELECT id, modelId, modelSourceId FROM monsters WHERE id=" + getDataId() + " AND sourceID=" + getDataSourceId() + " AND modelId NOT LIKE 'null' AND modelSourceId NOT LIKE 'null';", cursor -> {
            if(cursor != null && cursor.getCount() != 0) {
                returnVal[0] = true;
            }
        }), true);
        return returnVal[0];
    }
}
