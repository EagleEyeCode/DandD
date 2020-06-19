package de.eagleeye.dandd.list;

import android.os.Bundle;

public class BasicListItem {
    private int id;
    private int sourceId;
    private String title;
    private String subTitle;
    private String image;
    private Bundle extras;

    public BasicListItem(int id, int sourceId, String title, String subTitle, String image) {
        this.id = id;
        this.sourceId = sourceId;
        this.title = title;
        this.subTitle = subTitle;
        this.image = image;
        this.extras = new Bundle();
    }

    public int getId() {
        return id;
    }

    public int getSourceId() {
        return sourceId;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getImage() {
        return image;
    }

    public Bundle getExtras() {
        return extras;
    }

    public void putExtra(String key, String value){
        extras.putString(key, value);
    }
}
