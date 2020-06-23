package de.eagleeye.dandd.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.eagleeye.dandd.R;

public class BasicShowListAdapter extends RecyclerView.Adapter<BasicShowListAdapter.ViewHolder> {
    private ArrayList<BasicShowListItem> items;

    public BasicShowListAdapter() {
        this.items = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewHolder holder = new ViewHolder(inflater.inflate(R.layout.list_show_item, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.caption.setText(items.get(position).getCaption());
        holder.content.setText(items.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public ArrayList<BasicShowListItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<BasicShowListItem> items) {
        this.items = items;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView caption;
        TextView content;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            caption = itemView.findViewById(R.id.list_show_item_cation);
            content = itemView.findViewById(R.id.list_show_item_content);
        }
    }
}
