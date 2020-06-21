package de.eagleeye.dandd.list;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

import de.eagleeye.dandd.R;
import de.eagleeye.dandd.sql.BasicSQLiteHelper;
import de.eagleeye.dandd.sql.SQLRequest;

public class FilterCheckListAdapter extends RecyclerView.Adapter<FilterCheckListAdapter.ViewHolder> implements SQLRequest.OnQueryResult {
    private ArrayList<FilterCheckListItem> items;
    private Activity activity;
    private String loadQuery;

    public FilterCheckListAdapter(Activity activity, String loadQuery) {
        this.items = new ArrayList<>();
        this.activity = activity;
        this.loadQuery = loadQuery;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_filter_check_item, parent, false);
        return new FilterCheckListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.box.setChecked(items.get(position).isChecked());
        holder.name.setText(items.get(position).getName());

        holder.itemView.setOnClickListener(v -> {
            if(items.get(position).isChecked()){
                items.get(position).setChecked(false);
                holder.box.setChecked(false);
            }else {
                items.get(position).setChecked(true);
                holder.box.setChecked(true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void load(SharedPreferences prefs, String category){
        items = new ArrayList<>();
        for(String key : prefs.getAll().keySet()){
            if(key.startsWith(category + "_") && key.endsWith("_checked")){
                String name = key.split("_")[1];
                int id = prefs.getInt(category + "_" + name + "_id", 0);
                int sourceId = prefs.getInt(category + "_" + name + "_sourceId", 0);
                boolean checked = prefs.getBoolean(category + "_" + name + "_checked", false);
                items.add(new FilterCheckListItem(id, sourceId, name, checked));
            }
        }
        if(items.isEmpty()){
            initialLoad();
        }else {
            Collections.sort(items);
            notifyDataSetChanged();
        }
    }

    public void save(SharedPreferences.Editor edit, String category){
        for(FilterCheckListItem item : items){
            edit.putInt(category + "_" + item.getName() + "_id", item.getId());
            edit.putInt(category + "_" + item.getName() + "_sourceId", item.getSourceId());
            edit.putBoolean(category + "_" + item.getName() + "_checked", item.isChecked());
        }
        edit.apply();
    }

    public ArrayList<FilterCheckListItem> getItems(){
        return items;
    }

    private void initialLoad(){
        BasicSQLiteHelper db = new BasicSQLiteHelper(activity, "data.db");
        SQLRequest query = new SQLRequest(loadQuery, this);
        db.query(query);
    }

    @Override
    public void onSQLiteQueryResult(Cursor cursor) {
        items = new ArrayList<>();
        if(cursor != null && cursor.moveToFirst()){
            do{
                items.add(new FilterCheckListItem(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), false));
            }while (cursor.moveToNext());
        }
        Collections.sort(items);
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox box;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            box = itemView.findViewById(R.id.list_filter_check_item_box);
            name = itemView.findViewById(R.id.list_filter_check_item_name);
        }
    }
}
