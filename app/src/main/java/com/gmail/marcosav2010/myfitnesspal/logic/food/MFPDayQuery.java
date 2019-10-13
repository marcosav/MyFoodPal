package com.gmail.marcosav2010.myfitnesspal.logic.food;

import android.os.AsyncTask;

import com.gmail.marcosav2010.myfitnesspal.api.MFPSession;
import com.gmail.marcosav2010.myfitnesspal.api.lister.CustomFoodFormater;
import com.gmail.marcosav2010.myfitnesspal.api.lister.FoodList;
import com.gmail.marcosav2010.myfitnesspal.api.lister.ListerData;
import com.gmail.marcosav2010.myfitnesspal.logic.config.PreferenceManager;

import java.io.IOException;
import java.util.function.Consumer;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MFPDayQuery extends AsyncTask<String, Void, FoodQueryResult> {

    private final MFPDayQueryData data;
    private final Consumer<FoodQueryResult> handler;

    protected FoodQueryResult doInBackground(String... login) {
        try {
            ListerData lc = new ListerData(PreferenceManager.CONFIG_DATA);
            lc.load();
            FoodList fl = new FoodList(lc, MFPSession.create(login[0], login[1]).getDayFood(data.getDate(), data.getMeals(), new CustomFoodFormater(lc)));

            return FoodQueryResult.from(FoodQueryResult.Type.SUCCESS, fl.toList(data.isBuy()));

        } catch (IOException ex) {
            return FoodQueryResult.from(FoodQueryResult.Type.IO_ERROR, null);

        } catch (Exception ex) {
            return FoodQueryResult.from(FoodQueryResult.Type.LOGIN_ERROR, null);
        }
    }

    protected void onPostExecute(FoodQueryResult got) {
        handler.accept(got);
    }
}