package com.gmail.marcosav2010.myfoodpal.model.food.lister;

import com.gmail.marcosav2010.json.JSONArray;
import com.gmail.marcosav2010.myfitnesspal.api.food.diary.DiaryFood;
import com.gmail.marcosav2010.myfitnesspal.api.food.diary.DiaryMeal;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FoodList {

    private final FoodFormatter formatter;
    private final ListerData data;
    private final Collection<DiaryMeal> inputList;

    public FoodList(ListerData data, Collection<DiaryMeal> inputList) {
        this.data = data;
        this.inputList = inputList;

        formatter = new FoodFormatter(data);
    }

    private String removeEnd(Number n) {
        return String.valueOf(n).replaceFirst("\\.0$", "");
    }

    private String getAmount(DiaryFood f) {
        Double conversion;
        try {
            conversion = data.getConversion(f.getName());
        } catch (NumberFormatException ex) {
            return "[Unit error] ";
        }

        if (conversion == null)
            return removeEnd(f.getAmount()) + "" + f.getUnit() + " ";

        if (conversion > 0)
            return removeEnd(f.getAmount() / conversion) + " ";

        return "";
    }

    public Collection<ListedFood> toFood(boolean buy) {
        if (buy) {
            Map<String, ListedFood> out = new HashMap<>();
            inputList.forEach(m -> m.getFood().stream().map(formatter.mapper()).forEach(f -> {
                if (out.containsKey(f.getName())) {
                    out.get(f.getName()).add(f.getAmount());
                } else
                    out.put(f.getName(), f);
            }));

            return out.values();

        } else {
            List<ListedFood> out = new LinkedList<>();
            inputList.forEach(m -> out.addAll(m.getFood()
                    .stream().map(formatter.mapper()).collect(Collectors.toList())));
            return out;
        }
    }

    public List<String> toList(boolean buy) {
        return toList(toFood(buy), buy);
    }

    public List<String> toList(Collection<ListedFood> foodList, boolean buy) {
        List<String> out = new LinkedList<>();

        foodList.forEach(f -> {
            String alias = f.getName();
            String q = "";

            if (buy) {
                if (!data.isException(alias)) {
                    q = data.isMeasured(alias) ? getAmount(f) : "";
                    out.add(q + alias);
                }
            } else {
                String o = "";

                if (data.isTake(alias))
                    o = "Sacar ";
                else if (data.isWeight(alias))
                    o = "";
                else if (data.isChop(alias))
                    o = "Picar ";
                else
                    return;

                q = getAmount(f);

                out.add(o + q + alias);
            }
        });

        return out;
    }

    public String toJSON(boolean buy) {
        return new JSONArray(toList(buy)).toString();
    }
}
