package tn.amin.myhomeui.designer.fontpicker;

public class HeaderItem extends ListItem {
    public final String title;

    public HeaderItem(String title) {
        this.title = title;
    }

    @Override
    public TYPE getType() {
        return TYPE.HEADER;
    }
}
