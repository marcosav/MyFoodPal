package com.gmail.marcosav2010.myfitnesspal.model.settings;

import androidx.annotation.NonNull;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FoodSetting {

    @NonNull
    private String first;

    private Object second;

    public FoodSetting(String name) {
        this(name, null);
    }
}