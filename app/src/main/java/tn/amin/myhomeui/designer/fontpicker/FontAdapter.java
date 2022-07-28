package tn.amin.myhomeui.designer.fontpicker;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import tn.amin.myhomeui.R;
import tn.amin.myhomeui.designer.DesignerActivity;
import tn.amin.myhomeui.storage.StorageManager;

public class FontAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public final LazyFontList fontList;
    private FontPickerDialog.FontListener fontListener;

    private final ArrayList<ListItem> itemsList = new ArrayList<>();

    public FontAdapter(LazyFontList fontList) {
        this.fontList = fontList;

        itemsList.add(new HeaderItem("Custom fonts"));
        itemsList.add(new SelectFontItem());
        fontList.customFonts.forEach((font) -> { itemsList.add(new FontItem(font, true)); });
        itemsList.add(new HeaderItem("System fonts"));
        fontList.systemFonts.forEach((font) -> { itemsList.add(new FontItem(font, false)); });
    }

    public void setFontListener(FontPickerDialog.FontListener listener) {
        this.fontListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItem.TYPE type = ListItem.TYPE.values()[viewType];
        View view;
        switch (type) {
            case CHOOSE_FONT:
            case FONT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fontpicker_recycler_fontitem, parent, false);
                return new FontViewHolder(view);
            case HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fontpicker_recycler_headeritem, parent, false);
                return new HeaderViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListItem item = itemsList.get(position);
        ListItem.TYPE type = itemsList.get(position).getType();
        switch (type) {
            case FONT:
                File font = ((FontItem) item).font;
                ((FontViewHolder) holder).setFont(font, ((FontItem) item).isCustom);
                ((FontViewHolder) holder).setSelected(false);
                break;
            case CHOOSE_FONT:
                ((FontViewHolder) holder).setChooseFont();
                break;
            case HEADER:
                String header = ((HeaderItem) item).title;
                ((HeaderViewHolder) holder).setHeader(header);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return itemsList.get(position).getType().ordinal();
    }

    protected class FontViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView hintTextView;
        public FontViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
            hintTextView = itemView.findViewById(android.R.id.text2);
        }
        public void setFont(File font, boolean custom) {
            textView.setText(LazyFontList.simplifyFontName(font));
            Typeface[] typeface = { null };
            if (font != null && font.exists()) typeface[0] = Typeface.createFromFile(font);
            textView.setTypeface(typeface[0]);

            itemView.setOnClickListener((v) -> {
                if (fontListener != null)
                    fontListener.onFontSelected(typeface[0], font.getAbsolutePath());
            });
            if (custom) {
                itemView.setOnLongClickListener((v) -> {
                    removeCustomFont(font, getBindingAdapterPosition());
                    return true;
                });
            } else {
                itemView.setOnLongClickListener(null);
            }
        }

        public void setChooseFont() {
            textView.setText("Select...");
            hintTextView.setVisibility(View.INVISIBLE);

            itemView.setOnClickListener((v) -> {
                ((DesignerActivity) itemView.getContext()).queryFont(FontAdapter.this::importFont);
            });
        }

        public void setSelected(boolean selected) {
            hintTextView.setText("Current font");
            hintTextView.setVisibility(selected? View.VISIBLE: View.INVISIBLE);
        }
    }

    protected static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerView;
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerView = itemView.findViewById(android.R.id.text1);
        }

        public void setHeader(String header) {
            headerView.setText(header);
        }
    }

    protected void importFont(File font) {
        itemsList.add(2, new FontItem(font, false));
        fontList.addCustomFont(font);
        notifyItemInserted(2);
    }

    protected void removeCustomFont(File font, int position) {
        if (fontList.removeCustomFont(font)) {
            itemsList.remove(position);
            notifyItemRemoved(position);
        }
    }
}
