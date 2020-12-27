package com.gmail.marcosav2010.myfoodpal.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.gmail.marcosav2010.myfitnesspal.api.IMFPSession;
import com.gmail.marcosav2010.myfitnesspal.api.food.diary.FoodDay;
import com.gmail.marcosav2010.myfoodpal.model.food.FoodQueryData;
import com.gmail.marcosav2010.myfoodpal.model.food.ListElement;
import com.gmail.marcosav2010.myfoodpal.model.food.lister.FoodList;
import com.gmail.marcosav2010.myfoodpal.model.food.lister.ListedFood;
import com.gmail.marcosav2010.myfoodpal.model.food.lister.ListerData;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FoodQueryTask extends AsyncTask<Void, Void, FoodQueryResult> {

    private final IMFPSession session;
    private final ListerData lc;
    private final FoodQueryData data;
    private final Consumer<FoodQueryResult> handler;

    protected FoodQueryResult doInBackground(Void... login) {
        try {
            List<FoodDay> days = session
                    .toDiary()
                    .getDayRange(data.getDates(), data.getMeals());

            FoodList fl = new FoodList(lc,
                    days
                            .stream()
                            .flatMap(d -> d.getMeals().stream())
                            .collect(Collectors.toList())
            );

            Collection<ListedFood> rawFoodList = fl.toFood(data.isBuy());
            List<ListElement> list = fl.toList(rawFoodList, data.isBuy())
                    .stream()
                    .map(ListElement::new)
                    .collect(Collectors.toList());

            return FoodQueryResult.from(FoodQueryResult.Type.SUCCESS, list, rawFoodList);

        } catch (Exception ex) {
            Log.e("", "There was an error while retrieving food list", ex);
            return FoodQueryResult.from(FoodQueryResult.Type.UNKNOWN_ERROR);
        }
    }

    protected void onPostExecute(FoodQueryResult got) {
        handler.accept(got);
    }
}