package com.gmail.marcosav2010.myfoodpal.view.food;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gmail.marcosav2010.myfoodpal.R;
import com.gmail.marcosav2010.myfoodpal.common.Utils;
import com.gmail.marcosav2010.myfoodpal.model.food.ListElement;
import com.gmail.marcosav2010.myfoodpal.storage.SessionStorage;
import com.gmail.marcosav2010.myfoodpal.tasks.FoodQueryResult;
import com.gmail.marcosav2010.myfoodpal.viewmodel.food.FoodViewModel;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class FoodFragment extends Fragment {

    private static final int DINNER_THRESHOLD = 16;

    private static final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);

    private FoodViewModel viewModel;

    private FoodFragmentListener listener;

    private FloatingActionButton genBT;

    private FoodListAdapter foodListAdapter;

    private TextView backgroundLB;
    private ProgressBar loadFoodPB;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (FoodFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement OnArticleSelectedListener");
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_food, container, false);

        BottomAppBar bottomBar = root.findViewById(R.id.bottomFoodBar);
        genBT = root.findViewById(R.id.genBT);

        bottomBar.setOnMenuItemClickListener(this::onNavigationItemSelected);
        bottomBar.setNavigationOnClickListener(e -> listener.onSettingsOpen());

        genBT.setOnClickListener(v -> viewModel.loadFoodList());

        loadFoodPB = root.findViewById(R.id.loadFoodPB);
        backgroundLB = root.findViewById(R.id.backgroundLB);

        RecyclerView foodRecycler = root.findViewById(R.id.foodRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        foodRecycler.setLayoutManager(layoutManager);

        foodListAdapter = new FoodListAdapter();
        foodRecycler.setAdapter(foodListAdapter);

        ImageView rawBT = root.findViewById(R.id.rawBT);
        rawBT.setOnClickListener(v -> {
            FoodQueryResult r = viewModel.getResult().getValue();
            if (r != null && r.getType() == FoodQueryResult.Type.SUCCESS)
                listener.onRawFoodListOpen(r.getRawList());
            else
                Toast.makeText(getContext(), R.string.no_food_loaded, Toast.LENGTH_LONG).show();
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RadioButton buyRB = view.findViewById(R.id.buyRB);
        RadioButton prepareRB = view.findViewById(R.id.prepareRB);
        EditText dateOpt = view.findViewById(R.id.genDateOptField);
        EditText toDateOpt = view.findViewById(R.id.genToDateOptField);
        EditText mealsOpt = view.findViewById(R.id.genMealsOptField);

        viewModel = new ViewModelProvider(requireActivity()).get(FoodViewModel.class);

        viewModel.getFoodList().observe(getViewLifecycleOwner(), listElements -> {
            if (listElements.isEmpty())
                showEmpty();

            foodListAdapter.submitList(listElements);
        });

        viewModel.getSelectedMeals().observe(getViewLifecycleOwner(), mealsOpt::setText);
        viewModel.getDate().observe(getViewLifecycleOwner(), date -> tryDate(dateOpt, date));
        viewModel.getToDate().observe(getViewLifecycleOwner(), toDate -> {
            if (viewModel.isBuying())
                tryDate(toDateOpt, toDate);
        });

        viewModel.getResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) {
                showLoading();
                foodListAdapter.submitList(new ArrayList<>());
            } else {
                showLoaded();
                int msgId = result.getType().getMsg();
                if (msgId != 0) {
                    String msg = getString(R.string.food_generation_error_base) + getString(msgId);
                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                }
            }
        });

        mealsOpt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(SessionStorage.MAX_MEALS)});
        dateOpt.setOnClickListener(v -> pickDate(false));
        toDateOpt.setOnClickListener(v -> pickDate(true));
        mealsOpt.setOnClickListener(v -> pickMeals());

        buyRB.setOnClickListener(v -> {
            toDateOpt.setEnabled(true);

            if (viewModel.isBuying())
                return;

            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DAY_OF_MONTH, 1);

            viewModel.setBuying(true);
            viewModel.selectAllMeals();
            viewModel.setDate(tomorrow);
            viewModel.setToDate(tomorrow);
        });

        prepareRB.setOnClickListener(v -> {
            toDateOpt.setText("-");
            toDateOpt.setEnabled(false);

            if (!viewModel.isBuying())
                return;

            Calendar now = Calendar.getInstance();
            viewModel.setBuying(false);
            viewModel.setDate(now);
            viewModel.setSelectedMeals(now.get(Calendar.HOUR_OF_DAY) >= DINNER_THRESHOLD ? "2" : "1");
        });

        if (viewModel.isBuying())
            buyRB.performClick();
        else
            prepareRB.performClick();
    }

    private void pickMeals() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());

        String selectedMeals = viewModel.getSelectedMeals().getValue();

        List<String> userMeals = viewModel.getUserMeals();
        String[] meals = userMeals.toArray(new String[0]);
        boolean[] checkedItems = new boolean[userMeals.size()];

        for (int i = 0; i < userMeals.size(); i++) {
            if (Objects.requireNonNull(selectedMeals).contains(String.valueOf(i)))
                checkedItems[i] = true;
        }

        builder.setTitle(getString(R.string.choose_meals))
                .setMultiChoiceItems(meals, checkedItems, (d, i, b) -> checkedItems[i] = b)
                .setPositiveButton(getString(android.R.string.ok), (dialog, which) -> {
                    StringBuilder out = new StringBuilder();
                    for (int i = 0; i < checkedItems.length; i++)
                        if (checkedItems[i])
                            out.append(i);

                    viewModel.setSelectedMeals(out.toString());
                }).setNegativeButton(getString(android.R.string.cancel), null)
                .create()
                .show();
    }

    private void tryDate(EditText target, Calendar date) {
        target.setText(dateFormat.format(date.getTime()));
    }

    private void pickDate(boolean to) {
        Calendar c = to ? viewModel.getToDate().getValue() : viewModel.getDate().getValue();

        new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar cal = Calendar.getInstance();
                    cal.set(year, month, dayOfMonth);
                    if (to)
                        viewModel.setToDate(cal);
                    else
                        viewModel.setDate(cal);
                },
                Objects.requireNonNull(c).get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        ).show();
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
    }

    private void showLoaded() {
        backgroundLB.setVisibility(View.INVISIBLE);
        loadFoodPB.setVisibility(View.INVISIBLE);
        genBT.setEnabled(true);
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        Integer msg = null;

        switch (item.getItemId()) {
            case R.id.bfbm_copy: {
                String content = viewModel.getFoodListOutput();

                if (content.trim().isEmpty())
                    msg = R.string.no_content;
                else
                    msg = Utils.copyToClipboard(getActivity(), content);

                break;
            }
            case R.id.bfbm_wp: {
                String content = viewModel.getFoodListOutput();

                if (content.trim().isEmpty())
                    msg = R.string.no_content;
                else
                    msg = Utils.shareWhatsApp(getActivity(), content);

                break;
            }
            case R.id.bfbm_add:
                viewModel.addElement(new ListElement());
        }

        if (msg != null)
            Toast.makeText(getContext(), getString(msg), Toast.LENGTH_SHORT).show();

        return true;
    }
}