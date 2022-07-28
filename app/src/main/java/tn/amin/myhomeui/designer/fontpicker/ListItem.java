package tn.amin.myhomeui.designer.fontpicker;

public abstract class ListItem {
    public enum TYPE {
        HEADER,
        CHOOSE_FONT,
        FONT
    }

    public abstract TYPE getType();
}
