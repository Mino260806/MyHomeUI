package tn.amin.myhomeui.designer.fontpicker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

import tn.amin.myhomeui.designer.DesignerActivity;

public class FontPickerDialog extends DialogFragment {
    private static final LazyFontList fontList = new LazyFontList();
    private static Thread fontLoaderThread = null;
    private FontListener fontListener = null;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity())
                .setTitle("Pick Font Family");

        FontAdapter adapter = new FontAdapter(fontList);
        adapter.setFontListener(((typeface, path) -> {
            dismiss();
            fontListener.onFontSelected(typeface, path);
        }));
        RecyclerView recyclerView = new RecyclerView(requireActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        builder.setView(recyclerView);

        return builder.create();
    }

    public FontPickerDialog setSelectedFont(File selectedFont) {
        fontList.setSelectedFont(selectedFont);
        return this;
    }

    public FontPickerDialog setFontListener(FontListener listener) {
        fontListener = listener;
        return this;
    }

    public void show(@NonNull FragmentManager manager, @Nullable String tag, DesignerActivity activity) {
        if (fontLoaderThread != null && fontLoaderThread.isAlive()) return;
        if (!fontList.hasLoaded) {
            fontLoaderThread = activity.indicateProgress(fontList::loadFonts, () -> show(manager, tag));
        } else {
            show(manager, tag);
        }
    }

    public interface FontListener {
        void onFontSelected(Typeface typeface, String path);
    }
}
