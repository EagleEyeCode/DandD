package de.eagleeye.dandd.list;

public class ActionListItem implements Comparable<ActionListItem> {
    private String name;
    private String text;
    private boolean legendary;
    private int recharge;
    private String attackName;
    private int attackAtk;
    private String attackDamage;

    public ActionListItem(String name, String text, boolean legendary, int recharge, String attackName, int attackAtk, String attackDamage) {
        this.name = name;
        this.text = text;
        this.legendary = legendary;
        this.recharge = recharge;
        this.attackName = attackName;
        this.attackAtk = attackAtk;
        this.attackDamage = attackDamage;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public boolean isLegendary() {
        return legendary;
    }

    public int getRecharge() {
        return recharge;
    }

    public String getAttackName() {
        return attackName;
    }

    public int getAttackAtk() {
        return attackAtk;
    }

    public String getAttackDamage() {
        return attackDamage;
    }

    public boolean hasAttack(){
        return attackDamage != null && !attackDamage.equals("null");
    }

    @Override
    public int compareTo(ActionListItem o) {
        if(legendary && o.isLegendary()) return name.compareTo(o.getName());
        if(!legendary && !o.isLegendary()) return name.compareTo(o.getName());
        if(legendary) return 1;
        else return -1;
    }
}
