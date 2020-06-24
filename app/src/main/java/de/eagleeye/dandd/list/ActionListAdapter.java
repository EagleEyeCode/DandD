package de.eagleeye.dandd.list;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.Collections;

import de.eagleeye.dandd.R;

public class ActionListAdapter extends RecyclerView.Adapter<ActionListAdapter.ViewHolder>{
    private ArrayList<ActionListItem> items;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_action_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActionListItem item = items.get(position);
        if(item.isLegendary()){
            SpannableStringBuilder str = new SpannableStringBuilder(item.getName() + ". (Legendary)  " + item.getText());
            str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, item.getName().length() + 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.main.setText(str);
        }else {
            SpannableStringBuilder str = new SpannableStringBuilder(item.getName() + ".   " + item.getText());
            str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, item.getName().length() + 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.main.setText(str);
        }
        if(item.hasAttack()){
            if(!item.getAttackName().equals("null")) holder.attackName.setText("Attack:");
            if(!item.getName().equals("") && !item.getName().equals("null")){
                holder.attackDetail.setText(item.getAttackName() + " +" + item.getAttackAtk() + " for Hit (" + item.getAttackDamage() + " damage)");
            }else {
                holder.attackDetail.setText("+" + item.getAttackAtk() + " for Hit (" + item.getAttackDamage() + " damage)");
            }
            holder.attackLayout.setExpanded(true, false);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(ArrayList<ActionListItem> items) {
        this.items = items;
        Collections.sort(items);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView main;
        TextView attackName;
        TextView attackDetail;
        ExpandableLayout attackLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            main = itemView.findViewById(R.id.list_action_main);
            attackName = itemView.findViewById(R.id.list_action_attack_name);
            attackDetail = itemView.findViewById(R.id.list_action_attack_detail);
            attackLayout = itemView.findViewById(R.id.list_action_attack_layout);
        }
    }
}
