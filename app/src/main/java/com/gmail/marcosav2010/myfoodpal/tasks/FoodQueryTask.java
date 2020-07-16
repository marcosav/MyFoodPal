package com.gmail.marcosav2010.myfoodpal.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.gmail.marcosav2010.myfitnesspal.api.Food;
import com.gmail.marcosav2010.myfitnesspal.api.MFPSession;
import com.gmail.marcosav2010.myfitnesspal.api.lister.FoodFormatter;
import com.gmail.marcosav2010.myfitnesspal.api.lister.FoodList;
import com.gmail.marcosav2010.myfitnesspal.api.lister.ListerData;
import com.gmail.marcosav2010.myfoodpal.model.food.FoodQueryData;
import com.gmail.marcosav2010.myfoodpal.model.food.ListElement;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FoodQueryTask extends AsyncTask<Void, Void, FoodQueryResult> {

    private final MFPSession session;
    private final ListerData lc;
    private final FoodFormatter fc;
    private final FoodQueryData data;
    private final Consumer<FoodQueryResult> handler;

    protected FoodQueryResult doInBackground(Void... login) {
        try {
            FoodList fl = new FoodList(lc, session.getDayRangeFood(data.getDates(), data.getMeals(), fc));

            Collection<Food> foodList = fl.toFood(data.isBuy());
            List<ListElement> list = fl.toList(foodList, data.isBuy())
                    .stream()
                    .map(ListElement::new)
                    .collect(Collectors.toList());

            return FoodQueryResult.from(FoodQueryResult.Type.SUCCESS, list, foodList);

        /*} catch (IOException ex) {
            return FoodQueryResult.from(FoodQueryResult.Type.IO_ERROR, null);*/

        } catch (Exception ex) {
            Log.e("", "There was an error while retrieving food list", ex);
            return FoodQueryResult.from(FoodQueryResult.Type.UNKNOWN_ERROR);
        }
    }

    protected void onPostExecute(FoodQueryResult got) {
        handler.accept(got);
    }
}