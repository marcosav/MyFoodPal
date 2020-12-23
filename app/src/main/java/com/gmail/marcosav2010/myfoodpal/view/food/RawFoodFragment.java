package com.gmail.marcosav2010.myfoodpal.view.food;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gmail.marcosav2010.myfoodpal.R;
import com.gmail.marcosav2010.myfoodpal.model.food.lister.ListedFood;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RawFoodFragment extends Fragment {

    private static final String FOOD_DATA_SIZE_ARG = "raw_food_data_size";
    private static final String FOOD_DATA_NAME_ARG = "raw_food_data_name_";
    private static final String FOOD_DATA_BRAND_ARG = "raw_food_data_brand_";
    private static final String FOOD_DATA_UNIT_ARG = "raw_food_data_unit_";
    private static final String FOOD_DATA_AMOUNT_ARG = "raw_food_data_amount_";

    private List<ListedFood> foodList;

    public RawFoodFragment() {
    }

    @SuppressWarnings("unused")
    public static RawFoodFragment newInstance(Collection<ListedFood> foodList) {
        RawFoodFragment fragment = new RawFoodFragment();
        Bundle args = new Bundle();

        int i = 0;
        args.putInt(FOOD_DATA_SIZE_ARG, foodList.size());
        for (ListedFood f : foodList) {
            args.putString(FOOD_DATA_NAME_ARG + i, f.getName());
            args.putString(FOOD_DATA_BRAND_ARG + i, f.getBrand());
            args.putString(FOOD_DATA_UNIT_ARG + i, f.getUnit());
            args.putFloat(FOOD_DATA_AMOUNT_ARG + i, f.getAmount());
            i++;
        }

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            int size = getArguments().getInt(FOOD_DATA_SIZE_ARG);

            foodList = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                foodList.add(new ListedFood(
                        getArguments().getString(FOOD_DATA_NAME_ARG + i),
                        getArguments().getString(FOOD_DATA_BRAND_ARG + i),
                        getArguments().getString(FOOD_DATA_UNIT_ARG + i),
                        getArguments().getFloat(FOOD_DATA_AMOUNT_ARG + i)
                ));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_raw_food, container, false);

        Context context = view.getContext();

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(e -> getParentFragmentManager().popBackStack());

        RecyclerView recyclerView = view.findViewById(R.id.rawFoodRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new RawFoodListAdapter(foodList));

        return view;
    }
}