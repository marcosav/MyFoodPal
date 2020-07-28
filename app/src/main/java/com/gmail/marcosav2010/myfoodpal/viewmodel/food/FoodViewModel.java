package com.gmail.marcosav2010.myfoodpal.viewmodel.food;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gmail.marcosav2010.myfitnesspal.api.Food;
import com.gmail.marcosav2010.myfitnesspal.api.MFPSession;
import com.gmail.marcosav2010.myfitnesspal.api.lister.CustomFoodFormatter;
import com.gmail.marcosav2010.myfitnesspal.api.lister.ListerData;
import com.gmail.marcosav2010.myfoodpal.R;
import com.gmail.marcosav2010.myfoodpal.common.Utils;
import com.gmail.marcosav2010.myfoodpal.model.food.FoodQueryData;
import com.gmail.marcosav2010.myfoodpal.model.food.ListElement;
import com.gmail.marcosav2010.myfoodpal.storage.DataStorer;
import com.gmail.marcosav2010.myfoodpal.storage.PreferenceManager;
import com.gmail.marcosav2010.myfoodpal.tasks.FoodQueryResult;
import com.gmail.marcosav2010.myfoodpal.tasks.FoodQueryTask;
import com.gmail.marcosav2010.myfoodpal.tasks.SessionRequestResult;
import com.gmail.marcosav2010.myfoodpal.tasks.SessionRequestTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FoodViewModel extends AndroidViewModel {

    private DataStorer dataStorer = DataStorer.load(getApplication().getApplicationContext());

    private boolean buying = true;

    private MutableLiveData<String> meals = new MutableLiveData<>(getApplication().getString(R.string.def_meals_opt));
    private MutableLiveData<Calendar> date;
    private MutableLiveData<Calendar> toDate;

    private MutableLiveData<FoodQueryResult> result = new MutableLiveData<>();
    private MutableLiveData<List<ListElement>> foodList = new MutableLiveData<>(new ArrayList<>());

    public FoodViewModel(@NonNull Application application) {
        super(application);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        date = new MutableLiveData<>(c);
        toDate = new MutableLiveData<>(c);
    }

    public LiveData<List<ListElement>> getFoodList() {
        return foodList;
    }

    public LiveData<FoodQueryResult> getResult() {
        return result;
    }

    private void setResult(FoodQueryResult result) {
        if (result.getType() == FoodQueryResult.Type.SUCCESS)
            this.foodList.setValue(result.getResult());

        this.result.setValue(result);
    }

    public boolean isBuying() {
        return buying;
    }

    public void setBuying(boolean buying) {
        this.buying = buying;
    }

    public LiveData<String> getMeals() {
        return meals;
    }

    public void setMeals(@NonNull String meals) {
        this.meals.setValue(meals);
    }

    public LiveData<Calendar> getDate() {
        return date;
    }

    public void setDate(@NonNull Calendar date) {
        if (buying &&
                Objects.requireNonNull(this.toDate.getValue()).compareTo(date) < 0)
            this.toDate.setValue(date);

        this.date.setValue(date);
    }

    public LiveData<Calendar> getToDate() {
        return toDate;
    }

    public void setToDate(@NonNull Calendar toDate) {
        if (!buying)
            return;

        if (toDate.compareTo(Objects.requireNonNull(this.date.getValue())) < 0)
            this.date.setValue(toDate);

        this.toDate.setValue(toDate);
    }

    private void setErrorResult(@NonNull FoodQueryResult.Type resultType) {
        this.result.setValue(FoodQueryResult.from(resultType));
    }

    public FoodQueryData getQueryData() {
        return new FoodQueryData(
                meals.getValue(),
                buying,
                Objects.requireNonNull(date.getValue()).getTime(),
                Objects.requireNonNull(toDate.getValue()).getTime()
        );
    }

    public void loadFoodList() {
        result.setValue(null);

        PreferenceManager preferenceManager = dataStorer.getPreferenceManager();
        ListerData lc = preferenceManager.getListerData();
        MFPSession session = dataStorer.getSession();

        if (session == null) {
            String username = preferenceManager.getMFPUsername();
            String password = preferenceManager.getMFPPassword();

            if (username == null || password == null) {
                setErrorResult(FoodQueryResult.Type.NO_SESSION);
            } else {
                new SessionRequestTask(getApplication().getApplicationContext(), r -> {
                    if (r.getType() == SessionRequestResult.Type.SUCCESS)
                        sendFoodQuery(dataStorer.getSession(), lc);
                    else
                        setErrorResult(FoodQueryResult.Type.NO_SESSION);
                }).execute(username, password);
            }

            return;
        }

        sendFoodQuery(session, lc);
    }

    private void sendFoodQuery(MFPSession session, ListerData lc) {
        if (!Utils.hasInternetConnection(getApplication().getApplicationContext())) {
            setErrorResult(FoodQueryResult.Type.NO_INTERNET_ERROR);
            return;
        }

        new FoodQueryTask(
                session,
                lc,
                new CustomFoodFormatter(lc),
                getQueryData(),
                this::setResult
        ).execute();
    }

    public String getFoodListOutput() {
        String header = getApplication().getString(buying ?
                R.string.buy_header : R.string.prepare_header);
        String content = Objects.requireNonNull(getFoodList().getValue()).stream()
                .filter(e -> e.isChecked() && !e.getName().isEmpty())
                .map(e -> "\n - " + e.getName())
                .collect(Collectors.joining());
        return content.isEmpty() ? "" : header + content;
    }

    public void addElement(ListElement listElement) {
        List<ListElement> l = new ArrayList<>(Objects.requireNonNull(foodList.getValue()));
        l.add(listElement);

        Collection<Food> currentFoodResult = result.getValue() == null ?
                Collections.emptyList() :
                result.getValue().getRawList();

        result.setValue(FoodQueryResult.from(FoodQueryResult.Type.SUCCESS, l, currentFoodResult));
        foodList.setValue(l);
    }
}
