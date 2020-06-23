package de.eagleeye.dandd.fragments.show;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import de.eagleeye.dandd.R;
import de.eagleeye.dandd.activities.MainActivity;
import de.eagleeye.dandd.fragments.base.BaseDataShowFragment;
import de.eagleeye.dandd.list.BasicShowListAdapter;
import de.eagleeye.dandd.list.BasicShowListItem;
import de.eagleeye.dandd.list.NoScrollLinearLayoutManager;
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

    private RecyclerView savingThrowsList;
    private RecyclerView skillsList;
    private RecyclerView traitsList;
    private RecyclerView actionsList;

    private ArrayList<BasicShowListItem> baseListItems;
    private ArrayList<BasicShowListItem> savingThrowsListItems;

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

        savingThrowsList = view.findViewById(R.id.fragment_show_monster_saving_throws);
        skillsList = view.findViewById(R.id.fragment_show_monster_skills);
        traitsList = view.findViewById(R.id.fragment_show_monster_traits);
        actionsList = view.findViewById(R.id.fragment_show_monster_actions);

        baseListItems = new ArrayList<>();
        savingThrowsListItems = new ArrayList<>();

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

        db.query(languages, true);
        db.query(base, true);
        db.query(abilities, true);
        db.query(savingThrows, true);

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
}
