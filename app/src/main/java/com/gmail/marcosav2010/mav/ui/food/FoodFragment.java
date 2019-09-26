package com.gmail.marcosav2010.mav.ui.food;

import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.gmail.marcosav2010.mav.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.util.Calendar;

public class FoodFragment extends Fragment {

    private RadioButton buyRB, prepareRB;
    private Button copyBT, wpBT;
    private FloatingActionButton genBT;
    private EditText foodTextContainer;
    private EditText dateOpt, mealsOpt;
    private TextView emptyLB;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_food, container, false);

        buyRB = root.findViewById(R.id.buyRB);
        prepareRB = root.findViewById(R.id.prepareRB);
        buyRB.setOnClickListener(this::onBuySelect);
        prepareRB.setOnClickListener(this::onPrepareSelect);

        emptyLB = root.findViewById(R.id.emptyLB);

        copyBT = root.findViewById(R.id.copyBT);
        wpBT = root.findViewById(R.id.wpBT);
        genBT = root.findViewById(R.id.genBT);

        copyBT.setOnClickListener(this::onShareClick);
        wpBT.setOnClickListener(this::onShareClick);

        genBT.setOnClickListener(this::onGenClick);

        dateOpt = root.findViewById(R.id.genDateOptField);
        mealsOpt = root.findViewById(R.id.genMealsOptField);

        dateOpt.setOnClickListener(v -> pickDate());

        foodTextContainer = root.findViewById(R.id.foodTextContainer);

        buyRB.callOnClick();

        return root;
    }

    private void onBuySelect(View v) {
        setDate(getTomorrow());
        setMeals(getString(R.string.def_meals_opt));
    }

    private void onPrepareSelect(View v) {
        Calendar now = Calendar.getInstance();
        setDate(now);
        setMeals(now.get(Calendar.HOUR_OF_DAY) >= 16 ? "2" : "1");
    }

    private Calendar getTomorrow() {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        return tomorrow;
    }

    private void setDate(Calendar c) {
        dateOpt.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(c.getTime()));
    }

    private void setMeals(String meals) {
        mealsOpt.setText(meals);
    }

    private void pickDate() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, dayOfMonth);
            setDate(cal);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private String getFoodContent() {
        return foodTextContainer.getText().toString();
    }

    private void onGenClick(View v) {
        Toast.makeText(getContext(), R.string.generating, Toast.LENGTH_SHORT).show();

        emptyLB.setVisibility(View.INVISIBLE);

        foodTextContainer.setClickable(true);
        foodTextContainer.setCursorVisible(true);
        foodTextContainer.setFocusable(true);
        foodTextContainer.setFocusableInTouchMode(true);

        Toast.makeText(getContext(), R.string.generation_done, Toast.LENGTH_SHORT).show();
    }

    private void onShareClick(View v) {
        String content = getFoodContent();
        if (content.trim().isEmpty()) {
            Toast.makeText(getContext(), R.string.no_content, Toast.LENGTH_SHORT).show();
            return;
        }

        if (v.getId() == copyBT.getId()) {
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied List", content);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(getContext(), R.string.successfully_copied, Toast.LENGTH_SHORT).show();

        } else if (v.getId() == wpBT.getId()) {
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);

            whatsappIntent.setType("text/plain");
            whatsappIntent.setPackage("com.whatsapp");
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, content);

            try {
                getActivity().startActivity(whatsappIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getContext(), R.string.error_whatsapp, Toast.LENGTH_SHORT).show();
            }
        }
    }
}