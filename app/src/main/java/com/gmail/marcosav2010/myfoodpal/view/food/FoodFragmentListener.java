package com.gmail.marcosav2010.myfoodpal.view.food;

import com.gmail.marcosav2010.myfoodpal.model.food.lister.ListedFood;

import java.util.Collection;

public interface FoodFragmentListener {

    void onSettingsOpen();

    void onRawFoodListOpen(Collection<ListedFood> foodList);
}
