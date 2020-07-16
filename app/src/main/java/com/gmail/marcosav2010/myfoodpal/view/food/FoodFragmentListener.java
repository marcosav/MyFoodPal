package com.gmail.marcosav2010.myfoodpal.view.food;

import com.gmail.marcosav2010.myfitnesspal.api.Food;

import java.util.Collection;

public interface FoodFragmentListener {

    void onSettingsOpen();

    void onRawFoodListOpen(Collection<Food> foodList);
}
