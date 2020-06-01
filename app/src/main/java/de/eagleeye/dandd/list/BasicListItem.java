package de.eagleeye.dandd.list;

public class BasicListItem {
    private int id;
    private String title;
    private String subTitle;
    private String image;

    public BasicListItem(int id, String title, String subTitle, String image) {
        this.id = id;
        this.title = title;
        this.subTitle = subTitle;
        this.image = image;
    }

    public int getId() {
        return id;
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
}
