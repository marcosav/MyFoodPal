package com.gmail.marcosav2010.myfitnesspal.ui.food;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.gmail.marcosav2010.myfitnesspal.R;
import com.gmail.marcosav2010.myfitnesspal.api.MFPSession;
import com.gmail.marcosav2010.myfitnesspal.api.lister.CustomFoodFormatter;
import com.gmail.marcosav2010.myfitnesspal.api.lister.ListerData;
import com.gmail.marcosav2010.myfitnesspal.common.Utils;
import com.gmail.marcosav2010.myfitnesspal.logic.DataStorer;
import com.gmail.marcosav2010.myfitnesspal.logic.config.PreferenceManager;
import com.gmail.marcosav2010.myfitnesspal.logic.food.DayFoodQueryData;
import com.gmail.marcosav2010.myfitnesspal.logic.food.DayFoodQueryTask;
import com.gmail.marcosav2010.myfitnesspal.logic.food.FoodQueryResult;
import com.gmail.marcosav2010.myfitnesspal.logic.food.MFPSessionRequestResult;
import com.gmail.marcosav2010.myfitnesspal.logic.food.SessionRequestTask;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FoodFragment extends Fragment {

    private static final int DINNER_THRESHOLD = 16;

    private FoodFragmentListener listener;

    private FloatingActionButton genBT;

    private EditText foodTextContainer, dateOpt, toDateOpt, mealsOpt;
    private TextView backgroundLB;

    private DataStorer dataStorer;
    private ProgressBar loadFoodPB;
    private DayFoodQueryData queryData;

    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dataStorer = DataStorer.load(getContext());
        queryData = new DayFoodQueryData();

        View root = inflater.inflate(R.layout.fragment_food, container, false);

        context = root.getContext();

        RadioButton buyRB = root.findViewById(R.id.buyRB), prepareRB = root.findViewById(R.id.prepareRB);
        buyRB.setOnClickListener(this::onBuySelect);
        prepareRB.setOnClickListener(this::onPrepareSelect);

        backgroundLB = root.findViewById(R.id.backgroundLB);

        BottomAppBar bottomBar = root.findViewById(R.id.bottomFoodBar);
        genBT = root.findViewById(R.id.genBT);

        bottomBar.setOnMenuItemClickListener(this::onNavigationItemSelected);
        bottomBar.setNavigationOnClickListener(e -> listener.onSettingsOpen());

        genBT.setOnClickListener(this::onGenClick);

        dateOpt = root.findViewById(R.id.genDateOptField);
        toDateOpt = root.findViewById(R.id.genToDateOptField);
        mealsOpt = root.findViewById(R.id.genMealsOptField);

        loadFoodPB = root.findViewById(R.id.loadFoodPB);

        mealsOpt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                queryData.setMeals(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        dateOpt.setOnClickListener(v -> pickDate(false));
        toDateOpt.setOnClickListener(v -> pickDate(true));

        foodTextContainer = root.findViewById(R.id.foodTextContainer);

        buyRB.callOnClick();

        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (FoodFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnArticleSelectedListener");
        }
    }

    private void onBuySelect(View v) {
        setDate(getTomorrow());
        setMeals(getString(R.string.def_meals_opt));
        queryData.setBuy(true);
        toDateOpt.setEnabled(true);
    }

    private void onPrepareSelect(View v) {
        Calendar now = Calendar.getInstance();
        setDate(now);
        setMeals(now.get(Calendar.HOUR_OF_DAY) >= DINNER_THRESHOLD ? "2" : "1");
        queryData.setBuy(false);
        toDateOpt.setEnabled(false);
        toDateOpt.setText("-");
    }

    private Calendar getTomorrow() {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        return tomorrow;
    }

    private void setDate(Calendar c) {
        setDate(c, null);
    }

    private void setDate(Calendar c, Boolean to) {
        Date d = c.getTime();

        if (to == null || to) {
            queryData.setToDate(d);
            toDateOpt.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(d));
        }

        if (to == null || !to) {
            queryData.setDate(d);
            dateOpt.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(d));
        }

        int diff = queryData.getToDate().compareTo(queryData.getDate());
        if (toDateOpt.isEnabled() && diff < 0)
            setDate(c);
    }

    private void setMeals(String meals) {
        queryData.setMeals(meals);
        mealsOpt.setText(meals);
    }

    private void pickDate(boolean to) {
        Calendar c = Calendar.getInstance();
        c.setTime(to ? queryData.getToDate() : queryData.getDate());

        new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, dayOfMonth);
            setDate(cal, to);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private String getFoodContent() {
        return foodTextContainer.getText().toString();
    }

    private void setContentEditable(boolean b) {
        foodTextContainer.setClickable(b);
        foodTextContainer.setCursorVisible(b);
        foodTextContainer.setFocusable(b);
        foodTextContainer.setFocusableInTouchMode(b);
    }

    private void showEmpty() {
        backgroundLB.setVisibility(View.VISIBLE);
        backgroundLB.setText(R.string.empty_list);
        loadFoodPB.setVisibility(View.INVISIBLE);
        genBT.setEnabled(true);
    }

    private void showLoading() {
        setContentEditable(false);
        backgroundLB.setVisibility(View.VISIBLE);
        backgroundLB.setText(R.string.generating);
        loadFoodPB.setVisibility(View.VISIBLE);
        genBT.setEnabled(false);
        foodTextContainer.setText("");
    }

    private void showLoaded() {
        setContentEditable(true);
        backgroundLB.setVisibility(View.INVISIBLE);
        loadFoodPB.setVisibility(View.INVISIBLE);
        genBT.setEnabled(true);
    }

    private void onGenClick(View v) {
        showLoading();

        PreferenceManager preferenceManager = dataStorer.getPreferenceManager();
        ListerData lc = preferenceManager.getListerData();
        MFPSession session = dataStorer.getSession();

        if (session == null) {
            String username = preferenceManager.getMFPUsername();
            String password = preferenceManager.getMFPPassword();

            if (username == null || password == null) {
                onResultError(FoodQueryResult.Type.NO_SESSION);
            } else {
                new SessionRequestTask(context, r -> {
                    if (r.getType() == MFPSessionRequestResult.Type.SUCCESS)
                        sendFoodQuery(dataStorer.getSession(), lc);
                    else
                        onResultError(FoodQueryResult.Type.NO_SESSION);
                }).execute(username, password);
            }

            return;
        }

        sendFoodQuery(session, lc);
    }

    private void sendFoodQuery(MFPSession session, ListerData lc) {
        if (!Utils.hasInternetConnection(context)) {
            onResultError(FoodQueryResult.Type.NO_INTERNET_ERROR);
            return;
        }

        new DayFoodQueryTask(
                session,
                lc,
                new CustomFoodFormatter(lc),
                queryData,
                this::handleResult
        ).execute();
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        Integer msg = null;
        String content = getFoodContent();

        if (content.trim().isEmpty())
            msg = R.string.no_content;
        else
            switch (item.getItemId()) {
                case R.id.bfbm_copy:
                    msg = Utils.copyToClipboard(getActivity(), content);
                    break;
                case R.id.bfbm_wp:
                    msg = Utils.shareWhatsApp(getActivity(), content);
            }

        if (msg != null)
            Toast.makeText(getContext(), getString(msg), Toast.LENGTH_SHORT).show();

        return true;
    }

    private void handleResult(FoodQueryResult result) {
        if (result.getType() == FoodQueryResult.Type.SUCCESS)
            onResultGot(result.getResult());
        else
            onResultError(result.getType());
    }

    private void onResultError(FoodQueryResult.Type type) {
        showEmpty();
        String msg = getString(R.string.food_generation_error_base) + getString(type.getMsg());
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }

    private void onResultGot(List<String> got) {
        if (got.isEmpty()) {
            showEmpty();
            return;
        }

        showLoaded();

        foodTextContainer.append(getString(queryData.isBuy() ? R.string.buy_header : R.string.prepare_header));
        got.forEach(f -> foodTextContainer.append("\n - " + f));
    }
}