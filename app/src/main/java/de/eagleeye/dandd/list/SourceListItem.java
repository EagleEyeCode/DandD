package de.eagleeye.dandd.list;

public class SourceListItem {
    private String name;
    private int id;
    private String image;
    private boolean installed;
    private boolean wanted;

    public SourceListItem(String name, int id, String image, boolean installed, boolean wanted) {
        this.name = name;
        this.id = id;
        this.image = image;
        this.installed = installed;
        this.wanted = wanted;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public boolean isInstalled() {
        return installed;
    }

    public boolean isWanted() {
        return wanted;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInstalled(boolean installed) {
        this.installed = installed;
    }

    public void setWanted(boolean wanted) {
        this.wanted = wanted;
    }
}
