package org.disrupted.ibits.userinterface.adapter;

/**
 * @author
 */
public class IconTextItem {

    private int icon;
    private String text;
    private int id;

    public IconTextItem(int icon, String text, int id) {
        this.icon = icon;
        this.text = text;
        this.id = id;
    }

    public int getIcon() {
        return this.icon;
    }

    public String getText() {
        return this.text;
    }

    public int getID() {
        return id;
    }
}
