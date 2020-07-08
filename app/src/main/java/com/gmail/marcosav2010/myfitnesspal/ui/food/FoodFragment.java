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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gmail.marcosav2010.myfitnesspal.R;
import com.gmail.marcosav2010.myfitnesspal.api.MFPSession;
import com.gmail.marcosav2010.myfitnesspal.api.lister.CustomFoodFormatter;
import com.gmail.marcosav2010.myfitnesspal.api.lister.ListerData;
import com.gmail.marcosav2010.myfitnesspal.common.Utils;
import com.gmail.marcosav2010.myfitnesspal.logic.DataStorer;
import com.gmail.marcosav2010.myfitnesspal.logic.config.PreferenceManager;
import com.gmail.marcosav2010.myfitnesspal.logic.food.FoodQueryData;
import com.gmail.marcosav2010.myfitnesspal.logic.food.FoodQueryResult;
import com.gmail.marcosav2010.myfitnesspal.logic.food.FoodQueryTask;
import com.gmail.marcosav2010.myfitnesspal.logic.food.ListElement;
import com.gmail.marcosav2010.myfitnesspal.logic.food.MFPSessionRequestResult;
import com.gmail.marcosav2010.myfitnesspal.logic.food.SessionRequestTask;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class FoodFragment extends Fragment {

    private static final int DINNER_THRESHOLD = 16;

    private static final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);

    private FoodFragmentListener listener;

    private FloatingActionButton genBT;

    private FoodListAdapter foodListAdapter;

    private EditText dateOpt, toDateOpt, mealsOpt;
    private TextView backgroundLB;
    private ProgressBar loadFoodPB;

    private DataStorer dataStorer;
    private FoodQueryData queryData;

    private Context context;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            queryData = savedInstanceState.getParcelable("queryData");

            mealsOpt.setText(queryData.getMeals());
            dateOpt.setText(dateFormat.format(queryData.getDate()));
            toDateOpt.setText(dateFormat.format(queryData.getToDate()));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("queryData", queryData);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dataStorer = DataStorer.load(getContext());
        queryData = new FoodQueryData();

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

        RecyclerView foodRecycler = root.findViewById(R.id.foodRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        foodRecycler.setLayoutManager(layoutManager);

        foodListAdapter = new FoodListAdapter();
        foodRecycler.setAdapter(foodListAdapter);

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
            toDateOpt.setText(dateFormat.format(d));
        }

        if (to == null || !to) {
            queryData.setDate(d);
            dateOpt.setText(dateFormat.format(d));
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
        String header = getString(queryData.isBuy() ? R.string.buy_header : R.string.prepare_header);
        String content = foodListAdapter.getCurrentList().stream()
                .filter(e -> e.isChecked() && !e.getName().isEmpty())
                .map(e -> "\n - " + e.getName())
                .collect(Collectors.joining());
        return content.isEmpty() ? "" : header + content;
    }

    private void showEmpty() {
        backgroundLB.setVisibility(View.VISIBLE);
        backgroundLB.setText(R.string.empty_list);
        loadFoodPB.setVisibility(View.INVISIBLE);
        genBT.setEnabled(true);
    }

    private void showLoading() {
        backgroundLB.setVisibility(View.VISIBLE);
        backgroundLB.setText(R.string.generating);
        loadFoodPB.setVisibility(View.VISIBLE);
        genBT.setEnabled(false);

        foodListAdapter.submitList(new ArrayList<>());
    }

    private void showLoaded() {
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

        new FoodQueryTask(
                session,
                lc,
                new CustomFoodFormatter(lc),
                queryData,
                this::handleResult
        ).execute();
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        Integer msg = null;

        switch (item.getItemId()) {
            case R.id.bfbm_copy: {
                String content = getFoodContent();

                if (content.trim().isEmpty())
                    msg = R.string.no_content;
                else
                    msg = Utils.copyToClipboard(getActivity(), content);

                break;
            }
            case R.id.bfbm_wp: {
                String content = getFoodContent();

                if (content.trim().isEmpty())
                    msg = R.string.no_content;
                else
                    msg = Utils.shareWhatsApp(getActivity(), content);

                break;
            }
            case R.id.bfbm_add:
                List<ListElement> list = new ArrayList<>(foodListAdapter.getCurrentList());
                list.add(new ListElement());
                foodListAdapter.submitList(list);
                backgroundLB.setVisibility(View.INVISIBLE);
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

    private void onResultGot(List<ListElement> got) {
        if (got.isEmpty()) {
            showEmpty();
            return;
        }

        showLoaded();

        foodListAdapter.submitList(got);
    }
}