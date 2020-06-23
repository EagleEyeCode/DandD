package de.eagleeye.dandd.list;

public class BasicShowListItem {
    private String caption;
    private String content;

    public BasicShowListItem(String caption, String content) {
        this.caption = caption;
        this.content = content;
    }

    public String getCaption() {
        return caption;
    }

    public String getContent() {
        return content;
    }
}
