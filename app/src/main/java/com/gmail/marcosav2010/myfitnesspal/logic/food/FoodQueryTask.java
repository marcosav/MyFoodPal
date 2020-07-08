package com.gmail.marcosav2010.myfitnesspal.logic.food;

import android.os.AsyncTask;

import com.gmail.marcosav2010.myfitnesspal.api.MFPSession;
import com.gmail.marcosav2010.myfitnesspal.api.lister.FoodFormatter;
import com.gmail.marcosav2010.myfitnesspal.api.lister.FoodList;
import com.gmail.marcosav2010.myfitnesspal.api.lister.ListerData;

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

            List<ListElement> list = fl.toList(data.isBuy())
                    .stream()
                    .map(ListElement::new)
                    .collect(Collectors.toList());

            return FoodQueryResult.from(FoodQueryResult.Type.SUCCESS, list);

        /*} catch (IOException ex) {
            return FoodQueryResult.from(FoodQueryResult.Type.IO_ERROR, null);*/

        } catch (Exception ex) {
            return FoodQueryResult.from(FoodQueryResult.Type.UNKNOWN_ERROR, null);
        }
    }

    protected void onPostExecute(FoodQueryResult got) {
        handler.accept(got);
    }
}