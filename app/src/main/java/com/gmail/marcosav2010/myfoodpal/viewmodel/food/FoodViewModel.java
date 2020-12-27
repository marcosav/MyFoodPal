package com.gmail.marcosav2010.myfoodpal.viewmodel.food;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gmail.marcosav2010.myfitnesspal.api.IMFPSession;
import com.gmail.marcosav2010.myfoodpal.R;
import com.gmail.marcosav2010.myfoodpal.common.Utils;
import com.gmail.marcosav2010.myfoodpal.model.food.FoodQueryData;
import com.gmail.marcosav2010.myfoodpal.model.food.ListElement;
import com.gmail.marcosav2010.myfoodpal.model.food.lister.ListedFood;
import com.gmail.marcosav2010.myfoodpal.model.food.lister.ListerData;
import com.gmail.marcosav2010.myfoodpal.storage.PreferenceManager;
import com.gmail.marcosav2010.myfoodpal.storage.SessionStorage;
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
import java.util.stream.IntStream;

public class FoodViewModel extends AndroidViewModel {

    private final SessionStorage sessionStorage = SessionStorage.load(getApplication().getApplicationContext());
    private final PreferenceManager preferenceManager = PreferenceManager.load(getApplication().getApplicationContext());

    private boolean buying = true;

    private final MutableLiveData<String> selectedMeals;
    private final MutableLiveData<Calendar> date;
    private final MutableLiveData<Calendar> toDate;

    private final MutableLiveData<FoodQueryResult> result = new MutableLiveData<>();
    private final MutableLiveData<List<ListElement>> foodList = new MutableLiveData<>(new ArrayList<>());

    public FoodViewModel(@NonNull Application application) {
        super(application);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);

        selectedMeals = new MutableLiveData<>(getAllMeals());
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

    public LiveData<String> getSelectedMeals() {
        return selectedMeals;
    }

    public void setSelectedMeals(@NonNull String selectedMeals) {
        this.selectedMeals.setValue(selectedMeals);
    }

    public void selectAllMeals() {
        setSelectedMeals(getAllMeals());
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

    public List<String> getUserMeals() {
        return sessionStorage.getMeals();
    }

    public String getAllMeals() {
        return IntStream.range(0, getUserMeals().size())
                .mapToObj(Integer::toString)
                .collect(Collectors.joining());
    }

    private void setErrorResult(@NonNull FoodQueryResult.Type resultType) {
        this.result.setValue(FoodQueryResult.from(resultType));
    }

    public FoodQueryData getQueryData() {
        return new FoodQueryData(
                selectedMeals.getValue(),
                buying,
                Objects.requireNonNull(date.getValue()).getTime(),
                Objects.requireNonNull(toDate.getValue()).getTime()
        );
    }

    public void loadFoodList() {
        result.setValue(null);

        ListerData lc = preferenceManager.getListerData();
        IMFPSession session = sessionStorage.getSession();

        if (session == null) {
            String username = preferenceManager.getMFPUsername();
            String password = preferenceManager.getMFPPassword();

            if (username == null || password == null) {
                setErrorResult(FoodQueryResult.Type.NO_SESSION);
            } else {
                new SessionRequestTask(getApplication().getApplicationContext(), r -> {
                    if (r.getType() == SessionRequestResult.Type.SUCCESS)
                        sendFoodQuery(sessionStorage.getSession(), lc);
                    else
                        setErrorResult(FoodQueryResult.Type.NO_SESSION);
                }).execute(username, password);
            }

            return;
        }

        sendFoodQuery(session, lc);
    }

    private void sendFoodQuery(IMFPSession session, ListerData lc) {
        if (!Utils.hasInternetConnection(getApplication().getApplicationContext())) {
            setErrorResult(FoodQueryResult.Type.NO_INTERNET_ERROR);
            return;
        }

        new FoodQueryTask(
                session,
                lc,
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

        Collection<ListedFood> currentFoodResult = result.getValue() == null ?
                Collections.emptyList() :
                result.getValue().getRawList();

        result.setValue(FoodQueryResult.from(FoodQueryResult.Type.SUCCESS, l, currentFoodResult));
        foodList.setValue(l);
    }
}
