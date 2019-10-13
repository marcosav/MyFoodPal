package com.gmail.marcosav2010.myfitnesspal.ui.settings;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.gmail.marcosav2010.myfitnesspal.R;
import com.gmail.marcosav2010.myfitnesspal.logic.config.PreferenceManager;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Map;

public class SettingsFragment extends Fragment {

    private static final String PREFERENCES_NAME = "global_preferences";

    private static final String KEY_COLOR = "#ffc570";
    private static final String KEY_CLICK_COLOR = "#f2b457";

    private static final String VALUE_COLOR = "#77b6ff";
    private static final String VALUE_CLICK_COLOR = "#559ced";

    private static final String ADD_COLOR = "#ff7590";
    private static final String ADD_CLICK_COLOR = "#ef4f6f";

    private static final String SEPARATOR_COLOR = "#cccccc";

    private static final String TITLE_COLOR = "#c9c9c9";

    private static final int KEY_RADIUS = 20;
    private static final int VALUE_RADIUS = 40;

    private View root;

    private PreferenceManager preferenceManager;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_settings, container, false);

        getPreferenceManager();

        setContent();

        return root;
    }

    private void setContent() {
        LinearLayout scrollLL = root.findViewById(R.id.scrollLL);

        preferenceManager.getListerData().getAllConfig().forEach((k, v) -> {

            TextView title = createTitle(k);
            TagView tags = createTagView();

            scrollLL.addView(title);
            scrollLL.addView(tags);

            if (v instanceof Collection) {
                Collection<String> c = (Collection<String>) v;
                for (String s : c) {
                    Tag t = createTag(s, true);

                    tags.addTag(t);
                }

            } else if (v instanceof Map) {
                Map<String, ?> m = (Map<String, ?>) v;
                for (Map.Entry<String, ?> e : m.entrySet()) {
                    Tag tk = createTag(e.getKey(), true), tv = createTag(e.getValue(), false);

                    tags.addTag(tk);
                    tags.addTag(createSeparatorTag());
                    tags.addTag(tv);
                }

            } else if (v instanceof Multimap) {
                Multimap<String, Integer> m = (Multimap<String, Integer>) v;
                for (Map.Entry<String, Collection<Integer>> e : m.asMap().entrySet()) {
                    Tag tk = createTag(e.getKey(), true), tv;
                    tags.addTag(tk);
                    tags.addTag(createSeparatorTag());

                    for (Object ev : e.getValue()) {
                        tv = createTag(ev, false);
                        tags.addTag(tv);
                    }
                }
            }

            tags.addTag(createAddTag());
        });
    }

    private PreferenceManager getPreferenceManager() {
        if (preferenceManager == null)
            preferenceManager = new PreferenceManager(root.getContext(), PREFERENCES_NAME);

        return preferenceManager;
    }

    private TextView createTitle(String name) {
        name = name.replaceFirst("p_", "").replaceAll("_", " ");
        name = name.substring(0, 1).toUpperCase() + name.substring(1);

        TextView title = new TextView(getContext());
        title.setText(name);
        title.setTextSize(20);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(Color.parseColor(TITLE_COLOR));

        return title;
    }

    private TagView createTagView() {
        TagView tags = new TagView(root.getContext());

        TagView.LayoutParams layout = new TagView.LayoutParams(TagView.LayoutParams.MATCH_PARENT, TagView.LayoutParams.WRAP_CONTENT);
        layout.setMargins(16, 16, 16, 16);
        tags.setLayoutParams(layout);

        tags.setLineMargin(8);
        tags.setTagMargin(8);
        tags.settextPaddingBottom(5);
        tags.setTextPaddingLeft(9);
        tags.setTextPaddingRight(9);
        tags.setTextPaddingTop(5);

        tags.setOnTagDeleteListener((view, tag, position) -> tags.remove(position));

        tags.setOnTagClickListener((tag, position) -> {
            if (position == tags.getTags().size() - 1) {
                // add
            } else {
                // edit
            }
        });

        return tags;
    }

    private Tag createAddTag() {
        return createTag("+", ADD_COLOR, ADD_CLICK_COLOR, 100, false, 16);
    }

    private Tag createSeparatorTag() {
        return createTag(":", SEPARATOR_COLOR, SEPARATOR_COLOR, 30, false);
    }

    private Tag createTag(Object name, boolean key) {
        if (key)
            return createTag(name, KEY_COLOR, KEY_CLICK_COLOR, KEY_RADIUS, true);
        else
            return createTag(name, VALUE_COLOR, VALUE_CLICK_COLOR, VALUE_RADIUS, false);
    }

    private Tag createTag(Object name, String c, String cc, float r, boolean deletable) {
        return createTag(name, c, cc, r, deletable, 14);
    }

    private Tag createTag(Object name, String c, String cc, float r, boolean deletable, float size) {
        Tag t = new Tag(name.toString());

        t.radius = r;
        t.layoutColor = Color.parseColor(c);
        t.layoutColorPress = Color.parseColor(cc);
        t.tagTextSize = size;
        t.isDeletable = deletable;
        t.deleteIndicatorSize = 14;

        return t;
    }

    private void savePreferences() {

    }
}