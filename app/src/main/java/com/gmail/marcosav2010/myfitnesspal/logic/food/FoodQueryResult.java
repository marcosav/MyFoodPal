package com.gmail.marcosav2010.myfitnesspal.logic.food;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(staticName = "from")
public class FoodQueryResult {

    @Getter
    private Type type;
    @Getter
    private List<String> result;

    public enum Type {

        LOGIN_ERROR, IO_ERROR, SUCCESS
    }
}