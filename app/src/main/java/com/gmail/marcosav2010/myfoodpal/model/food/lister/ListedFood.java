package com.gmail.marcosav2010.myfoodpal.model.food.lister;

import com.gmail.marcosav2010.myfitnesspal.api.diary.food.DiaryFood;

import java.util.HashMap;

public class ListedFood extends DiaryFood {

    private float amount;

    public ListedFood(DiaryFood food) {
        this(food.getName(), food.getBrand(), food.getUnit(), food.getAmount());
    }

    public ListedFood(String name, String brand, String unit, float amount) {
        super("0", name, brand, unit, amount, 0, new HashMap<>());
        this.amount = amount;
    }

    public void add(float amount) {
        this.amount += amount;
    }

    @Override
    public float getAmount() {
        return Math.round(amount * 100.0f) / 100.0f;
    }
}
