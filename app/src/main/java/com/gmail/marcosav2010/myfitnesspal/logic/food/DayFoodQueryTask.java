package com.gmail.marcosav2010.myfitnesspal.logic.food;

import android.content.Context;
import android.os.AsyncTask;

import com.gmail.marcosav2010.myfitnesspal.api.MFPSession;
import com.gmail.marcosav2010.myfitnesspal.api.lister.FoodFormater;
import com.gmail.marcosav2010.myfitnesspal.api.lister.FoodList;
import com.gmail.marcosav2010.myfitnesspal.api.lister.ListerData;
import com.gmail.marcosav2010.myfitnesspal.common.Utils;

import java.io.IOException;
import java.util.function.Consumer;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DayFoodQueryTask extends AsyncTask<Void, Void, FoodQueryResult> {

    private final Context context;
    private final MFPSession session;
    private final ListerData lc;
    private final FoodFormater fc;
    private final DayFoodQueryData data;
    private final Consumer<FoodQueryResult> handler;

    protected FoodQueryResult doInBackground(Void... login) {
        if (!Utils.hasInternetConnection(context))
            return FoodQueryResult.from(FoodQueryResult.Type.NO_INTERNET_ERROR, null);

        try {
            FoodList fl = new FoodList(lc, session.getDayFood(data.getDate(), data.getMeals(), fc));

            return FoodQueryResult.from(FoodQueryResult.Type.SUCCESS, fl.toList(data.isBuy()));

        } catch (IOException ex) {
            return FoodQueryResult.from(FoodQueryResult.Type.IO_ERROR, null);

        } catch (Exception ex) {
            return FoodQueryResult.from(FoodQueryResult.Type.UNKNOWN_ERROR, null);
        }
    }

    protected void onPostExecute(FoodQueryResult got) {
        handler.accept(got);
    }
}