package com.gmail.marcosav2010.myfitnesspal.ui.settings.tag;

import com.cunoraz.tagview.Tag;

public class ETag extends Tag {

    public String key, value;

    public ETag(Object key, Object value) {
        super(key + ": " + value);
        this.key = key.toString();
        this.value = value.toString();
    }

    public ETag(Tag t, Object key, Object value) {
        this(key, value);

        radius = t.radius;
        layoutColor = t.layoutColor;
        layoutColorPress = t.layoutColorPress;
        tagTextSize = t.tagTextSize;
        isDeletable = t.isDeletable;
        deleteIndicatorSize = t.deleteIndicatorSize;
        background = t.background;
        deleteIcon = t.deleteIcon;
        deleteIndicatorColor = t.deleteIndicatorColor;
        id = t.id;
        tagTextColor = t.tagTextColor;
        layoutBorderSize = t.layoutBorderSize;
        layoutBorderColor = t.layoutBorderColor;
    }
}
