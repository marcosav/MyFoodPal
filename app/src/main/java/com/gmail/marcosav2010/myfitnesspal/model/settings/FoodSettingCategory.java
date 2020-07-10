package com.gmail.marcosav2010.myfitnesspal.model.settings;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class FoodSettingCategory {

    @Getter
    private final String name;

    @Getter
    private final String key;

    @Getter
    private final boolean entry;

    @Getter
    private List<FoodSetting> settings;

    public void remove(int position) {
        settings.remove(position);
    }

    public void update(int position, FoodSetting setting) {
        settings.set(position, setting);
    }

    public void insert(FoodSetting setting) {
        settings.add(0, setting);
    }
}
