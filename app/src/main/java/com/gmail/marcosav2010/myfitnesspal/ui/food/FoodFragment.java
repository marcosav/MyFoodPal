package com.gmail.marcosav2010.myfitnesspal.ui.food;

import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.gmail.marcosav2010.myfitnesspal.R;
import com.gmail.marcosav2010.myfitnesspal.logic.food.FoodQueryResult;
import com.gmail.marcosav2010.myfitnesspal.logic.food.MFPDayQuery;
import com.gmail.marcosav2010.myfitnesspal.logic.food.MFPDayQueryData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FoodFragment extends Fragment {

    private static final int DINNER_THRESHOLD = 16;

    private RadioButton buyRB, prepareRB;
    private Button copyBT, wpBT;
    private FloatingActionButton genBT;
    private EditText foodTextContainer, dateOpt, mealsOpt;
    private TextView backgroundLB;

    private View root;

    private MFPDayQueryData queryData;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        queryData = new MFPDayQueryData();

        root = inflater.inflate(R.layout.fragment_food, container, false);

        buyRB = root.findViewById(R.id.buyRB);
        prepareRB = root.findViewById(R.id.prepareRB);
        buyRB.setOnClickListener(this::onBuySelect);
        prepareRB.setOnClickListener(this::onPrepareSelect);

        backgroundLB = root.findViewById(R.id.backgroundLB);

        copyBT = root.findViewById(R.id.copyBT);
        wpBT = root.findViewById(R.id.wpBT);
        genBT = root.findViewById(R.id.genBT);

        copyBT.setOnClickListener(this::onShareClick);
        wpBT.setOnClickListener(this::onShareClick);

        genBT.setOnClickListener(this::onGenClick);

        dateOpt = root.findViewById(R.id.genDateOptField);
        mealsOpt = root.findViewById(R.id.genMealsOptField);

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

        dateOpt.setOnClickListener(v -> pickDate());

        foodTextContainer = root.findViewById(R.id.foodTextContainer);

        buyRB.callOnClick();

        return root;
    }

    private void onBuySelect(View v) {
        setDate(getTomorrow());
        setMeals(getString(R.string.def_meals_opt));
        queryData.setBuy(true);
    }

    private void onPrepareSelect(View v) {
        Calendar now = Calendar.getInstance();
        setDate(now);
        setMeals(now.get(Calendar.HOUR_OF_DAY) >= DINNER_THRESHOLD ? "2" : "1");
        queryData.setBuy(false);
    }

    private Calendar getTomorrow() {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        return tomorrow;
    }

    private void setDate(Calendar c) {
        Date d = c.getTime();
        queryData.setDate(d);
        dateOpt.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(d));
    }

    private void setMeals(String meals) {
        queryData.setMeals(meals);
        mealsOpt.setText(meals);
    }

    private void pickDate() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(root.getContext(), (view, year, month, dayOfMonth) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, dayOfMonth);
            setDate(cal);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private String getFoodContent() {
        return foodTextContainer.getText().toString();
    }

    private void clearContent() {
        foodTextContainer.setText("");
    }

    private void showEmpty() {
        backgroundLB.setVisibility(View.VISIBLE);
        backgroundLB.setText(R.string.empty_list);
    }

    private void showLoading() {
        backgroundLB.setVisibility(View.VISIBLE);
        backgroundLB.setText(R.string.generating);
    }

    private void onGenClick(View v) {
        setContentEditable(false);
        clearContent();
        showLoading();

        new MFPDayQuery(queryData, this::handleResult).execute("", "");
    }

    private void setContentEditable(boolean b) {
        foodTextContainer.setClickable(b);
        foodTextContainer.setCursorVisible(b);
        foodTextContainer.setFocusable(b);
        foodTextContainer.setFocusableInTouchMode(b);
    }

    private void onShareClick(View v) {
        Integer msg = tryShare(v);
        if (msg == null)
            return;

        Toast.makeText(getContext(), getString(msg), Toast.LENGTH_SHORT).show();
    }

    private void handleResult(FoodQueryResult result) {
        if (result.getType() == FoodQueryResult.Type.SUCCESS)
            onResultGot(result.getResult());
        else
            onResultError(result.getType());
    }

    private void onResultError(FoodQueryResult.Type type) {
        showEmpty();

        String msg = getString(R.string.generation_error) + (type == FoodQueryResult.Type.LOGIN_ERROR ? getString(R.string.generation_error_login) : "");

        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }

    private void onResultGot(List<String> got) {
        if (got.isEmpty()) {
            showEmpty();
            return;
        }

        backgroundLB.setVisibility(View.INVISIBLE);

        foodTextContainer.append(getString(queryData.isBuy() ? R.string.buy_header : R.string.prepare_header));
        got.forEach(f -> foodTextContainer.append("\n - " + f));

        setContentEditable(true);
    }

    private Integer tryShare(View v) {
        String content = getFoodContent();
        if (content.trim().isEmpty())
            return R.string.no_content;

        if (v.getId() == copyBT.getId()) {
            return copyToClipboard(content);

        } else if (v.getId() == wpBT.getId()) {
            return shareWhatsApp(content);
        }

        return null;
    }

    private Integer copyToClipboard(String content) {
        try {
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied List", content);
            clipboard.setPrimaryClip(clip);

            return R.string.successfully_copied;
        } catch (Exception ex) {
            return R.string.error_copy;
        }
    }

    private Integer shareWhatsApp(String content) {
        Intent wIntent = new Intent(Intent.ACTION_SEND);

        wIntent.setType("text/plain");
        wIntent.setPackage("com.whatsapp");
        wIntent.putExtra(Intent.EXTRA_TEXT, content);

        try {
            getActivity().startActivity(wIntent);
        } catch (Exception ex) {
            return R.string.error_whatsapp;
        }

        return null;
    }
}