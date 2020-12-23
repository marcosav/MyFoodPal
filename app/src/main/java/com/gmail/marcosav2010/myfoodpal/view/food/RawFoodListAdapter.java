package com.gmail.marcosav2010.myfoodpal.view.food;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gmail.marcosav2010.myfoodpal.R;
import com.gmail.marcosav2010.myfoodpal.model.food.lister.ListedFood;

import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RawFoodListAdapter extends RecyclerView.Adapter<RawFoodListAdapter.ViewHolder> {

    private final List<ListedFood> mValues;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.raw_food_holder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.bind(mValues.get(position));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView nameTV;
        public final TextView amountTV;
        public final TextView unitTV;

        public ViewHolder(View view) {
            super(view);
            nameTV = view.findViewById(R.id.food_name);
            amountTV = view.findViewById(R.id.food_amount);
            unitTV = view.findViewById(R.id.food_unit);
        }

        public void bind(ListedFood food) {
            nameTV.setText(food.getName());
            amountTV.setText(String.valueOf(food.getAmount()));
            unitTV.setText(food.getUnit());
        }
    }
}