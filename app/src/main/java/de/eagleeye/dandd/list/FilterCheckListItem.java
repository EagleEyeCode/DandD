package de.eagleeye.dandd.list;

public class FilterCheckListItem implements Comparable<FilterCheckListItem> {
    private int id;
    private int sourceId;
    private String name;
    private boolean checked;

    public FilterCheckListItem(int id, int sourceId, String name, boolean checked) {
        this.id = id;
        this.sourceId = sourceId;
        this.name = name;
        this.checked = checked;
    }

    public int getId() {
        return id;
    }

    public int getSourceId() {
        return sourceId;
    }

    public String getName() {
        return name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public int compareTo(FilterCheckListItem o) {
        return this.name.compareTo(o.getName());
    }
}
