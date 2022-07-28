package tn.amin.myhomeui.designer.fontpicker;

import java.io.File;

public class FontItem extends ListItem {
    public final File font;
    public boolean isCustom;

    public FontItem(File font, boolean custom) {
        this.font = font;
        isCustom = custom;
    }

    @Override
    public TYPE getType() {
        return TYPE.FONT;
    }
}
