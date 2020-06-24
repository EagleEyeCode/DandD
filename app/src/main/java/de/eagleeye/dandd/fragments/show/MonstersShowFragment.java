package de.eagleeye.dandd.fragments.show;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.view.ViewManager;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import de.eagleeye.dandd.R;
import de.eagleeye.dandd.activities.MainActivity;
import de.eagleeye.dandd.fragments.base.BaseDataShowFragment;
import de.eagleeye.dandd.list.ActionListAdapter;
import de.eagleeye.dandd.list.ActionListItem;
import de.eagleeye.dandd.list.BasicShowListAdapter;
import de.eagleeye.dandd.list.BasicShowListItem;
import de.eagleeye.dandd.list.NoScrollLinearLayoutManager;
import de.eagleeye.dandd.list.TextListAdapter;
import de.eagleeye.dandd.sql.BasicSQLiteHelper;
import de.eagleeye.dandd.sql.SQLRequest;

public class MonstersShowFragment extends BaseDataShowFragment {
    private RecyclerView baseList;

    private TextView str;
    private TextView dex;
    private TextView con;
    private TextView inte;
    private TextView wis;
    private TextView cha;

    private ArrayList<BasicShowListItem> baseListItems;
    private ArrayList<BasicShowListItem> savingThrowsListItems;
    private ArrayList<BasicShowListItem> skillsListItems;
    private ArrayList<SpannableStringBuilder> traitsListItems;
    private ArrayList<ActionListItem> actionListItems;

    private ExpandableLayout savingThrowsCard;
    private ExpandableLayout skillsCard;
    private ExpandableLayout traitsCard;
    private ExpandableLayout actionsCard;

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

        baseList = view.findViewById(R.id.fragment_show_monster_base_list);

        str = view.findViewById(R.id.fragment_show_monster_str);
        dex = view.findViewById(R.id.fragment_show_monster_dex);
        con = view.findViewById(R.id.fragment_show_monster_con);
        inte= view.findViewById(R.id.fragment_show_monster_int);
        wis = view.findViewById(R.id.fragment_show_monster_wis);
        cha = view.findViewById(R.id.fragment_show_monster_cha);

        RecyclerView savingThrowsList = view.findViewById(R.id.fragment_show_monster_saving_throws);
        RecyclerView skillsList = view.findViewById(R.id.fragment_show_monster_skills);
        RecyclerView traitsList = view.findViewById(R.id.fragment_show_monster_traits);
        RecyclerView actionsList = view.findViewById(R.id.fragment_show_monster_actions);

        savingThrowsCard = view.findViewById(R.id.fragment_show_monster_card_saving_throws);
        skillsCard = view.findViewById(R.id.fragment_show_monster_card_skills);
        traitsCard = view.findViewById(R.id.fragment_show_monster_card_traits);
        actionsCard = view.findViewById(R.id.fragment_show_monster_card_actions);

        baseListItems = new ArrayList<>();
        savingThrowsListItems = new ArrayList<>();
        skillsListItems = new ArrayList<>();
        traitsListItems = new ArrayList<>();
        actionListItems = new ArrayList<>();

        BasicSQLiteHelper db = new BasicSQLiteHelper(getActivity(), "data.db");

        AtomicReference<String> languagesStr = new AtomicReference<>("--");

        SQLRequest languages = new SQLRequest("SELECT languages.name FROM monsterLanguages LEFT JOIN languages ON monsterLanguages.languageId=languages.id AND monsterLanguages.languageSourceId=languages.sourceId WHERE monsterLanguages.monsterId=" + getDataId() + " AND monsterLanguages.monsterSourceId=" + getDataSourceId() + ";", cursor -> {
            if(cursor != null && cursor.moveToFirst()){
                languagesStr.set("");
                do{
                    if(!languagesStr.get().equals(""))languagesStr.set(languagesStr.get() + ", ");
                    languagesStr.set(languagesStr.get() + cursor.getString(0));
                }while (cursor.moveToNext());
            }
        });

        SQLRequest base = new SQLRequest("SELECT monsters.hpMax, monsterTypes.name, monsters.size, monsters.speed, monsters.hd, monsters.ac, monsterArmors.name, alignments.text, monsters.immune, monsters.conditionImmune, monsters.senses, monsters.resist, monsters.passive, monsters.cr, monsters.vulnerable, monsters.environment FROM monsters LEFT JOIN monsterArmors ON monsters.armorId=monsterArmors.id AND monsters.armorSourceId=monsterArmors.sourceId LEFT JOIN alignments ON monsters.alignmentId=alignments.id AND monsters.alignmentSourceId=alignments.sourceId LEFT JOIN monsterTypes ON monsters.typeId=monsterTypes.id AND monsters.typeSourceID=monsterTypes.sourceId WHERE monsters.id=" + getDataId() + " AND monsters.sourceId=" + getDataSourceId() + ";", cursor -> {
            if(cursor != null && cursor.moveToFirst()){
                baseListItems.add(new BasicShowListItem("Type:", getSizeString(cursor.getString(2)) + " " + cursor.getString(1)));
                String ac = cursor.getString(5);
                String armor = cursor.getString(6);
                if(!ac.equals("null")){
                    if(armor != null && !armor.equals("null")){
                        baseListItems.add(new BasicShowListItem("Armor Class:", ac + " (" + armor + ")"));
                    }else {
                        baseListItems.add(new BasicShowListItem("Armor Class:", ac));
                    }
                }
                baseListItems.add(new BasicShowListItem("Hit Points:", cursor.getString(0) + " (" + cursor.getString(4) + ")"));
                baseListItems.add(new BasicShowListItem("Speed:", cursor.getString(3)));
                baseListItems.add(new BasicShowListItem("Languages", languagesStr.get()));
                baseListItems.add(new BasicShowListItem("Alignment:", cursor.getString(7)));
                String immune = cursor.getString(8);
                if(!immune.equals("null")){
                    baseListItems.add(new BasicShowListItem("Immune:", immune));
                }
                String conditionImmune = cursor.getString(9);
                if(!conditionImmune.equals("null")){
                    baseListItems.add(new BasicShowListItem("Condition Immune:", conditionImmune));
                }
                String resist = cursor.getString(11);
                if(!resist.equals("null")){
                    baseListItems.add(new BasicShowListItem("Resist:", resist));
                }
                String senses = cursor.getString(10);
                String passive = cursor.getString(12);
                if(!senses.equals("null")){
                    if(!passive.equals("null")){
                        senses = senses + ", passive Perception " + passive;
                    }
                }else{
                    if(!passive.equals("null")){
                        senses = "passive Perception " + passive;
                    }
                }
                if(!senses.equals("null")){
                    baseListItems.add(new BasicShowListItem("Senses:", senses));
                }
                String cr = cursor.getString(13);
                if(!cr.equals("null")){
                    baseListItems.add(new BasicShowListItem("Challenge:", getChallengeString(cr)));
                }
                String vulnerable = cursor.getString(14);
                if(!vulnerable.equals("null")){
                    baseListItems.add(new BasicShowListItem("Vulnerable:", vulnerable));
                }
                String environment = cursor.getString(15);
                if(!environment.equals("null")){
                    baseListItems.add(new BasicShowListItem("Environment:", getEnvironmentString(environment)));
                }
            }
        });

        SQLRequest abilities = new SQLRequest("SELECT monsterAbilities.abilityId, monsterAbilities.value FROM monsterAbilities LEFT JOIN abilities ON monsterAbilities.abilityId=abilities.id AND monsterAbilities.abilitySourceId=abilities.sourceId WHERE monsterAbilities.monsterId=" + getDataId() + " AND monsterAbilities.monsterSourceId=" + getDataSourceId() + ";", cursor -> {
            if(cursor != null && cursor.moveToFirst()){
                do{
                    switch (cursor.getInt(0)){
                        case 0:
                            str.setText(getAbilityString(cursor.getInt(1)));
                            break;
                        case 1:
                            dex.setText(getAbilityString(cursor.getInt(1)));
                            break;
                        case 2:
                            con.setText(getAbilityString(cursor.getInt(1)));
                            break;
                        case 3:
                            inte.setText(getAbilityString(cursor.getInt(1)));
                            break;
                        case 4:
                            wis.setText(getAbilityString(cursor.getInt(1)));
                            break;
                        case 5:
                            cha.setText(getAbilityString(cursor.getInt(1)));
                            break;
                    }
                }while (cursor.moveToNext());
            }
        });

        SQLRequest savingThrows = new SQLRequest("SELECT ability, modifier FROM savingThrows WHERE monsterId=" + getDataId() + " AND monsterSourceId=" + getDataSourceId() + ";", cursor -> {
            if(cursor != null && cursor.moveToFirst()){
                do{
                    switch(cursor.getString(0)){
                        case "null":
                            savingThrowsListItems.add(new BasicShowListItem("Strength Saving Throw", "+" + cursor.getString(1)));
                            break;
                        case "1":
                            savingThrowsListItems.add(new BasicShowListItem("Dexterity Saving Throw", "+" + cursor.getString(1)));
                            break;
                        case "2":
                            savingThrowsListItems.add(new BasicShowListItem("Constitution Saving Throw", "+" + cursor.getString(1)));
                            break;
                        case "3":
                            savingThrowsListItems.add(new BasicShowListItem("Intelligence Saving Throw", "+" + cursor.getString(1)));
                            break;
                        case "4":
                            savingThrowsListItems.add(new BasicShowListItem("Wisdom Saving Throw", "+" + cursor.getString(1)));
                            break;
                        case "5":
                            savingThrowsListItems.add(new BasicShowListItem("Charisma Saving Throw", "+" + cursor.getString(1)));
                            break;
                    }
                } while(cursor.moveToNext());
            }
        });

        SQLRequest skills = new SQLRequest("", cursor -> {
            if(cursor != null && cursor.moveToFirst()){
                do{

                } while(cursor.moveToNext());
            }
        });

        SQLRequest traits = new SQLRequest("SELECT name, text FROM traits WHERE monsterId=" + getDataId() + " AND monsterSourceId=" + getDataSourceId(), cursor -> {
            if(cursor != null && cursor.moveToFirst()){
                do{
                    String name = cursor.getString(0);
                    SpannableStringBuilder str = new SpannableStringBuilder(name + ".   " + cursor.getString(1));
                    str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, name.length() + 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    traitsListItems.add(str);
                } while(cursor.moveToNext());
            }
        });

        SQLRequest actions = new SQLRequest("SELECT actions.name, actions.text, actions.legendary, actions.recharge, attacks.name, attacks.atk, attacks.dmg FROM monsterAction LEFT JOIN actions ON monsterAction.actionId=actions.id AND monsterAction.actionSourceId=actions.sourceId LEFT JOIN attacks ON actions.attackId=attacks.id AND actions.attackSourceId=attacks.sourceId WHERE monsterAction.monsterId=" + getDataId() + " AND monsterAction.monsterSourceId=" + getDataSourceId() + ";", cursor -> {
            if(cursor != null && cursor.moveToFirst()){
                do{
                    boolean legendary = false;
                    if(cursor.getString(2).equals("True")) legendary = true;
                    Log.d("Legendary", cursor.getString(2));
                    actionListItems.add(new ActionListItem(cursor.getString(0), cursor.getString(1), legendary, cursor.getInt(3), cursor.getString(4), cursor.getInt(5), cursor.getString(6)));
                } while (cursor.moveToNext());
            }
        });

        db.query(languages, true);
        db.query(base, true);
        db.query(abilities, true);
        db.query(savingThrows, true);
        db.query(traits, true);
        db.query(actions, true);

        BasicShowListAdapter baseListAdapter = new BasicShowListAdapter();
        baseListAdapter.setItems(baseListItems);
        baseList.setLayoutManager(new NoScrollLinearLayoutManager(getActivity()));
        baseList.setHasFixedSize(true);
        baseList.setAdapter(baseListAdapter);

        BasicShowListAdapter savingThrowsAdapter = new BasicShowListAdapter();
        savingThrowsAdapter.setItems(savingThrowsListItems);
        savingThrowsList.setLayoutManager(new NoScrollLinearLayoutManager(getActivity()));
        savingThrowsList.setHasFixedSize(true);
        savingThrowsList.setAdapter(savingThrowsAdapter);

        TextListAdapter traitsAdapter = new TextListAdapter();
        traitsAdapter.setItems(traitsListItems);
        traitsList.setLayoutManager(new NoScrollLinearLayoutManager(getActivity()));
        traitsList.setHasFixedSize(true);
        traitsList.setAdapter(traitsAdapter);

        ActionListAdapter actionsAdapter = new ActionListAdapter();
        actionsAdapter.setItems(actionListItems);
        actionsList.setLayoutManager(new NoScrollLinearLayoutManager(getActivity()));
        actionsList.setHasFixedSize(true);
        actionsList.setAdapter(actionsAdapter);

        removeNotUsedCards();
    }

    @Override
    protected String tableName() {
        return "monsters";
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_show_monster;
    }

    private void removeNotUsedCards(){
        if(savingThrowsListItems.size() == 0) savingThrowsCard.setExpanded(false, false);
        if(skillsListItems.size() == 0) skillsCard.setExpanded(false, false);
        if(traitsListItems.size() == 0) traitsCard.setExpanded(false, false);
        if(actionListItems.size() == 0) actionsCard.setExpanded(false, false);
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

    private String getSizeString(String size){
        switch(size){
            case "0": return "Tiny";
            case "1": return "Small";
            case "3": return "Large";
            case "4": return "Huge";
            case "5": return "Gargantuan";
            default: return "Medium";
        }
    }

    private String getChallengeString(String challenge){
        switch (challenge){
            case "0": return "1/2 (100 XP)";
            case "-1": return "1/4 (50 XP)";
            case "-2": return "1/8 (25 XP)";
            case "-3": return "0 (10 XP)";
            case "null": return "1 (200 XP)";
            case "2": return challenge + " (450 XP)";
            case "3": return challenge + " (700 XP)";
            case "4": return challenge + " (1100 XP)";
            case "5": return challenge + " (1800 XP)";
            case "6": return challenge + " (2300 XP)";
            case "7": return challenge + " (2900 XP)";
            case "8": return challenge + " (3900 XP)";
            case "9": return challenge + " (5000 XP)";
            case "10": return challenge + " (5900 XP)";
            case "11": return challenge + " (7200 XP)";
            case "12": return challenge + " (8400 XP)";
            case "13": return challenge + " (10000 XP)";
            case "14": return challenge + " (11500 XP)";
            case "15": return challenge + " (13000 XP)";
            case "16": return challenge + " (15000 XP)";
            case "17": return challenge + " (18000 XP)";
            case "18": return challenge + " (20000 XP)";
            case "19": return challenge + " (22000 XP)";
            case "20": return challenge + " (25000 XP)";
            case "21": return challenge + " (33000 XP)";
            case "22": return challenge + " (41000 XP)";
            case "23": return challenge + " (50000 XP)";
            case "24": return challenge + " (62000 XP)";
            case "25": return challenge + " (75000 XP)";
            case "26": return challenge + " (90000 XP)";
            case "27": return challenge + " (105000 XP)";
            case "28": return challenge + " (120000 XP)";
            case "29": return challenge + " (135000 XP)";
            case "30": return challenge + " (155000 XP)";
            default: return challenge;
        }
    }

    private String getAbilityString(int value) {
        float partA = value - 10;
        float partB = partA / 2;
        int modifier;
        if(partB < 0){
            modifier = (int)(partB - 0.5);
            return value + " (" + modifier + ")";
        }else{
            modifier = (int) partB;
            return value + " (+" + modifier + ")";
        }
    }

    private String getEnvironmentString(String environment) {
        int value = Integer.parseInt(environment);
        ArrayList<String> environments = new ArrayList<>();
        if(isBitSet(value, 0)) environments.add("Arctic");
        if(isBitSet(value, 1)) environments.add("Coastal");
        if(isBitSet(value, 2)) environments.add("Dessert");
        if(isBitSet(value, 3)) environments.add("Forest");
        if(isBitSet(value, 4)) environments.add("Grassland");
        if(isBitSet(value, 5)) environments.add("Hill");
        if(isBitSet(value, 6)) environments.add("Mountain");
        if(isBitSet(value, 7)) environments.add("Swamp");
        if(isBitSet(value, 8)) environments.add("Underdark");
        if(isBitSet(value, 9)) environments.add("Underwater");
        if(isBitSet(value, 10)) environments.add("Urban");

        StringBuilder environmentString = new StringBuilder();
        for(String s : environments){
            if(environmentString.length() != 0) environmentString.append(", ");
            environmentString.append(s);
        }

        return environmentString.toString();
    }

    private boolean isBitSet(int integer, int index){
        return (integer & (1 << index)) != 0;
    }
}
