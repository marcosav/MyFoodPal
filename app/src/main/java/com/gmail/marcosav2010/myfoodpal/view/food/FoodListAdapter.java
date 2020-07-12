package com.gmail.marcosav2010.myfoodpal.view.food;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.gmail.marcosav2010.myfoodpal.databinding.FoodHolderBinding;
import com.gmail.marcosav2010.myfoodpal.model.food.ListElement;

import lombok.Getter;

public class FoodListAdapter extends ListAdapter<ListElement, FoodListAdapter.FoodHolder> {

    public FoodListAdapter() {
        super(new ListElementDiff());
    }

    @NonNull
    @Override
    public FoodHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        FoodHolderBinding binding = FoodHolderBinding.inflate(inflater,
                parent,
                false);

        return new FoodHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodHolder holder, int position) {
        ListElement element = getItem(position);
        FoodHolderBinding itemBinding = holder.getBinding();
        itemBinding.setElement(element);
        itemBinding.executePendingBindings();
    }

    static class FoodHolder extends RecyclerView.ViewHolder {

        @Getter
        private FoodHolderBinding binding;

        private FoodHolder(FoodHolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    static class ListElementDiff extends DiffUtil.ItemCallback<ListElement> {

        @Override
        public boolean areItemsTheSame(@NonNull ListElement oldItem, @NonNull ListElement newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ListElement oldItem, @NonNull ListElement newItem) {
            return oldItem.isChecked() == newItem.isChecked() &&
                    oldItem.getName().equals(newItem.getName());
        }
    }
}
