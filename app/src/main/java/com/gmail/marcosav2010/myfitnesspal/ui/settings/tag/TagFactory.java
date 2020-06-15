package com.gmail.marcosav2010.myfitnesspal.ui.settings.tag;

import android.content.Context;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.gmail.marcosav2010.myfitnesspal.R;

import java.util.function.BiConsumer;

public class TagFactory {

    private static final int KEY_RADIUS = 20;

    private final Context context;

    private int KEY_COLOR;
    private int KEY_CLICK_COLOR;
    private int ADD_COLOR;
    private int ADD_CLICK_COLOR;

    public TagFactory(Context context) {
        this.context = context;

        KEY_COLOR = ContextCompat.getColor(context, R.color.tag_color);
        KEY_CLICK_COLOR = ContextCompat.getColor(context, R.color.tag_click_color);

        ADD_COLOR = ContextCompat.getColor(context, R.color.add_tag_color);
        ADD_CLICK_COLOR = ContextCompat.getColor(context, R.color.add_tag_click_color);
    }

    public TagGroup createTagView(String name, boolean entry) {
        TagGroup tags = new TagGroup(context, entry);

        TagView.LayoutParams layout = new TagView.LayoutParams(TagView.LayoutParams.MATCH_PARENT, TagView.LayoutParams.WRAP_CONTENT);
        layout.setMargins(20, 25, 20, 25);
        tags.setLayoutParams(layout);

        tags.setLineMargin(5);
        tags.setTagMargin(5);

        tags.settextPaddingBottom(4);
        tags.setTextPaddingLeft(8);
        tags.setTextPaddingRight(8);
        tags.setTextPaddingTop(4);

        tags.setOnTagDeleteListener((view, tag, position) -> tags.remove(position));

        tags.setOnTagClickListener((tag, position) -> {
            if (position == tags.getTags().size() - 1) {
                showInsertInput(tags, entry);
            } else {
                showEditInput(tags, entry, tag, position);
            }
        });

        return tags;
    }

    public Tag createAddTag() {
        return createTag("+", ADD_COLOR, ADD_CLICK_COLOR, 100, false);
    }

    public ETag createETag(Object name, Object value) {
        return new ETag(createTag(""), name, value);
    }

    public Tag createTag(Object name) {
        return createTag(name, KEY_COLOR, KEY_CLICK_COLOR, KEY_RADIUS, true);
    }

    public Tag createTag(Object name, int c, int cc, float r, boolean deletable) {
        return createTag(name, c, cc, r, deletable, 14);
    }

    public Tag createTag(Object name, int c, int cc, float r, boolean deletable, float size) {
        Tag t = new Tag(name.toString());

        t.radius = r;
        t.layoutColor = c;
        t.layoutColorPress = cc;
        t.tagTextSize = size;
        t.isDeletable = deletable;
        t.deleteIndicatorSize = 14;

        return t;
    }

    private void showInput(String title, boolean entry, String btName, BiConsumer<String, String> onClick) {
        showInput(title, entry, btName, onClick, null);
    }

    private void showInput(String title, boolean entry, String btName, BiConsumer<String, String> onClick, Tag base) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);

        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(60, 30, 60, 30);

        String kBase = "", vBase = "";

        if (base != null) {
            String text = base.text;
            if (entry) {
                String[] entries = text.split(": ");
                kBase = entries[0];
                vBase = entries[1];
            } else {
                kBase = text;
            }
        }


        EditText keyInput = new EditText(context);
        keyInput.setInputType(InputType.TYPE_CLASS_TEXT);
        keyInput.setHint(R.string.name_input_hint);
        keyInput.setText(kBase);
        ll.addView(keyInput);

        EditText valueInput = new EditText(context);

        if (entry) {
            valueInput.setInputType(InputType.TYPE_CLASS_TEXT);
            valueInput.setHint(R.string.value_input_hint);
            valueInput.setText(vBase);
            ll.addView(valueInput);
        }

        builder.setView(ll);

        builder.setPositiveButton(btName, (dialog, which) -> {
            String key = keyInput.getText().toString().trim();
            if (key.isEmpty())
                return;

            String value = valueInput.getText().toString().trim();
            if (value.isEmpty() && entry)
                return;

            onClick.accept(key, value);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showInsertInput(TagView tags, boolean entry) {
        showInput("Insert", entry, "Add", (key, value) -> {
            tags.remove(tags.getTags().size() - 1);

            if (entry) {
                tags.addTag(createETag(key, value));
            } else {
                tags.addTag(createTag(key));
            }

            tags.addTag(createAddTag());
        });
    }

    private void showEditInput(TagView tags, boolean entry, Tag tag, int position) {
        showInput("Edit", entry, "Done", (key, v) -> {
            tags.remove(tags.getTags().size() - 1);
            tags.remove(position);
            if (entry)
                tags.addTag(createETag(key, v));
            else
                tags.addTag(createTag(key));
            tags.addTag(createAddTag());
        }, tag);
    }
}
