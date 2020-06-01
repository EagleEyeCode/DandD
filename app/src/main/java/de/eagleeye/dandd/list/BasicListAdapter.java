package de.eagleeye.dandd.list;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import de.eagleeye.dandd.R;
import de.eagleeye.dandd.fragments.OnFilterInputFinished;

public class BasicListAdapter extends RecyclerView.Adapter<BasicListAdapter.ViewHolder>{
    private Activity activity;
    private String search;
    private ArrayList<BasicListItem> items;
    private ArrayList<BasicListItem> searchedItems;

    public BasicListAdapter(Activity activity, ArrayList<BasicListItem> items){
        this.activity = activity;
        this.search = "";
        this.items = items;
        searchedItems = new ArrayList<>();
        applySearch();
    }

    public void setItems(ArrayList<BasicListItem> items) {
        this.items = items;
        applySearch();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.basic_list_item_view, parent, false);
        return new BasicListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imagePath = activity.getFileStreamPath("").getAbsolutePath() + "/" + searchedItems.get(position).getImage();
        holder.title.setText(searchedItems.get(position).getTitle());
        holder.subTitle.setText(searchedItems.get(position).getSubTitle());
        holder.image.setImageBitmap(BitmapFactory.decodeFile(imagePath));
    }

    @Override
    public int getItemCount() {
        if(searchedItems != null) {
            return searchedItems.size();
        }else{
            return 0;
        }
    }

    public void setSearch(String search) {
        this.search = search;
        applySearch();
    }

    private void applySearch(){
        if(items == null) return;
        if(!search.equals("")){
            searchedItems = new ArrayList<>();
            for(BasicListItem item : items){
                if(item.getTitle().toLowerCase().startsWith(search.toLowerCase())) searchedItems.add(item);
            }
            notifyDataSetChanged();
        }else{
            searchedItems = items;
            notifyDataSetChanged();
        }
        if(activity instanceof OnFilterInputFinished) ((OnFilterInputFinished) activity).showSnackbar(searchedItems.size());
    }

    public boolean hasNoItems() {
        if(items == null) return true;
        return items.isEmpty();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView subTitle;
        ImageView image;

        ViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.basic_list_item_title);
            subTitle = v.findViewById(R.id.basic_list_item_sub_title);
            image = v.findViewById(R.id.basic_list_item_image);
        }
    }
}
