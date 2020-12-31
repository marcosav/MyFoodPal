package com.gmail.marcosav2010.myfoodpal.model.food.lister;

import com.gmail.marcosav2010.myfitnesspal.api.diary.food.DiaryFood;

import java.util.function.Function;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FoodFormatter {

    private final ListerData data;

    private String getAmountUnit(String u) {
        if (u.startsWith("g"))
            return "g";

        if (data.isUnitAlias(u))
            return "";

        return u;
    }

    public Function<DiaryFood, ListedFood> mapper() {
        return (food) -> new ListedFood(
                data.getAlias(food.getName()), food.getBrand(), getAmountUnit(food.getUnit()), food.getAmount()
        );
    }
}
