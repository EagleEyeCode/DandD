package de.eagleeye.dandd.fragments.show;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;

import de.eagleeye.dandd.R;
import de.eagleeye.dandd.fragments.base.BaseDataShowFragment;
import de.eagleeye.dandd.list.BasicShowListAdapter;
import de.eagleeye.dandd.list.BasicShowListItem;
import de.eagleeye.dandd.list.NoScrollLinearLayoutManager;
import de.eagleeye.dandd.list.TextListAdapter;
import de.eagleeye.dandd.sql.BasicSQLiteHelper;
import de.eagleeye.dandd.sql.SQLRequest;

public class ItemsShowFragment extends BaseDataShowFragment {
    private ArrayList<BasicShowListItem> baseItems;
    private ArrayList<SpannableStringBuilder> detailItems;

    private ExpandableLayout detailCard;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        baseItems = new ArrayList<>();
        detailItems = new ArrayList<>();

        detailCard = view.findViewById(R.id.fragment_show_item_detail_card);

        RecyclerView baseItemsList = view.findViewById(R.id.fragment_show_item_base_list);
        RecyclerView detailItemsList = view.findViewById(R.id.fragment_show_item_detail_list);

        BasicSQLiteHelper db = getDb();

        SQLRequest request = new SQLRequest("SELECT itemTypes.name, items.text, items.value, items.weight, items.detail, items.magic, items.damage1H, items.damageType, items.weaponProperty, items.damage2H, items.ac, items.stealth, items.strength, items.weaponLongRange, items.weaponRange FROM items LEFT JOIN itemTypes ON items.type=itemTypes.id AND items.typeSourceId=itemTypes.sourceId WHERE items.id=" + getDataId() + " AND items.sourceId=" + getDataSourceId() + ";", cursor -> {
           if(cursor != null && cursor.moveToFirst()){
               //Base List
               String damage2 = " (" +  cursor.getString(9) + ")";
               if(damage2.contains("null")) damage2 = "";
               String range = " (range " +  cursor.getString(14) + "/" + cursor.getString(13) + ")";
               if(range.contains("null")) range = "";

               String type = cursor.getString(0);
               if(!type.equals("null")) baseItems.add(new BasicShowListItem("Type:", type));
               String damage = cursor.getString(6);
               if(!damage.equals("null")) baseItems.add(new BasicShowListItem("Damage:", damage));
               String property = cursor.getString(8);
               if(!property.equals("null")) baseItems.add(new BasicShowListItem("Property:", getPropertyString(property, damage2, range)));
               String ac = cursor.getString(10);
               if(!ac.equals("null")) baseItems.add(new BasicShowListItem("Armor Class:", ac));
               String strength = cursor.getString(12);
               if(!strength.equals("null")) baseItems.add(new BasicShowListItem("Strength:", strength));
               String stealth = cursor.getString(12);
               if(!stealth.equals("null") && !stealth.equals("false")) baseItems.add(new BasicShowListItem("Stealth:", "Disadvantage"));
               String value = cursor.getString(2);
               if(!value.equals("null")) baseItems.add(new BasicShowListItem("Cost:", getCoinString(value)));
               else baseItems.add(new BasicShowListItem("Cost:", "--"));
               String weight = cursor.getString(2);
               if(!weight.equals("null")) baseItems.add(new BasicShowListItem("Weight:", weight + " lbs."));
               else baseItems.add(new BasicShowListItem("Weight:", "--"));

               //Detail List
               boolean magic = cursor.getString(5).equals("1");
               if(magic) {
                   SpannableStringBuilder str = new SpannableStringBuilder("Magical");
                   str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                   detailItems.add(str);
               }
               String detail = cursor.getString(4);
               if(detail.length() != 0 && !detail.equals("null")) {
                   SpannableStringBuilder str = new SpannableStringBuilder(detail);
                   str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, detail.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                   detailItems.add(str);
               }
               String text = cursor.getString(1);
               if(text.length() != 0 && !text.equals("null")) {
                   SpannableStringBuilder str = new SpannableStringBuilder("Description: " + text);
                   str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                   detailItems.add(str);
               }
           }
        });

        db.query(request, true);

        BasicShowListAdapter baseAdapter = new BasicShowListAdapter();
        baseAdapter.setItems(baseItems);
        baseItemsList.setLayoutManager(new NoScrollLinearLayoutManager(getActivity()));
        baseItemsList.setHasFixedSize(true);
        baseItemsList.setNestedScrollingEnabled(false);
        baseItemsList.setAdapter(baseAdapter);

        TextListAdapter detailAdapter = new TextListAdapter();
        detailAdapter.setItems(detailItems);
        detailItemsList.setLayoutManager(new NoScrollLinearLayoutManager(getActivity()));
        detailItemsList.setHasFixedSize(true);
        detailItemsList.setNestedScrollingEnabled(false);
        detailItemsList.setAdapter(detailAdapter);

        removeEmptyCards();
    }

    private String getCoinString(String value) {
        float v = Float.parseFloat(value) * 100;
        if(v % 1000 == 0) return ((int) v / 1000) + " pp";
        if(v % 100 == 0) return ((int) v / 100) + " gp";
        if(v % 50 == 0) return ((int) v / 50) + " ep";
        if(v % 10 == 0) return ((int) v / 10) + " sp";
        return ((int) v) + " cp";
    }

    private String getPropertyString(String property, String damage2, String range) {
        ArrayList<String> properties = new ArrayList<>();
        int p = Integer.parseInt(property);
        if(isBitSet(p, 0)) properties.add("Ammunition" + range);
        if(isBitSet(p, 1)) properties.add("Finesse");
        if(isBitSet(p, 2)) properties.add("Heavy");
        if(isBitSet(p, 3)) properties.add("Light");
        if(isBitSet(p, 4)) properties.add("Loading");
        if(isBitSet(p, 5)) properties.add("Reach");
        if(isBitSet(p, 6)) properties.add("Special");
        if(isBitSet(p, 7)) properties.add("Thrown" + range);
        if(isBitSet(p, 8)) properties.add("Two-Handed");
        if(isBitSet(p, 9)) properties.add("Versatile" + damage2);
        if(isBitSet(p, 10)) properties.add("Martial");

        StringBuilder builder = new StringBuilder();
        for(String s : properties){
            if(builder.length() != 0) builder.append(", ");
            builder.append(s);
        }

        return builder.toString();
    }

    @Override
    protected String tableName() {
        return "items";
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_show_item;
    }

    private void removeEmptyCards(){
        if(detailItems.size() == 0) detailCard.setExpanded(false, false);
    }

    private boolean isBitSet(int integer, int index){
        return (integer & (1 << index)) != 0;
    }
}
