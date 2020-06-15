package com.gmail.marcosav2010.myfitnesspal.ui.settings.tag;

import android.content.Context;

import com.cunoraz.tagview.TagView;

import lombok.Getter;

public class TagGroup extends TagView {

    @Getter
    private boolean entry;

    public TagGroup(Context ctx, boolean entry) {
        super(ctx);
        this.entry = entry;
    }
}
