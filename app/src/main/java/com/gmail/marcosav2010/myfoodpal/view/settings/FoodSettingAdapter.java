package com.gmail.marcosav2010.myfoodpal.view.settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gmail.marcosav2010.myfoodpal.R;
import com.gmail.marcosav2010.myfoodpal.model.settings.FoodSetting;

import java.util.List;
import java.util.function.Consumer;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FoodSettingAdapter extends RecyclerView.Adapter<FoodSettingAdapter.ViewHolder> {

    private List<FoodSetting> settings;
    private Consumer<Integer> onRemoveListener;
    private Consumer<Integer> onClickListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chip_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindItem(settings.get(position));
    }

    @Override
    public int getItemCount() {
        return settings.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private View itemView;
        private TextView tvDescription;
        private TextView tvName;
        private ImageButton ibClose;

        ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvName = itemView.findViewById(R.id.tvName);
            ibClose = itemView.findViewById(R.id.ibClose);
        }

        void bindItem(FoodSetting entity) {
            if (entity.getSecond() == null) {
                tvDescription.setVisibility(View.GONE);
            } else {
                tvDescription.setVisibility(View.VISIBLE);
                tvDescription.setText(entity.getSecond().toString());
            }

            tvName.setText(entity.getFirst());

            ibClose.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (onRemoveListener != null && position != -1) {
                    notifyItemRemoved(position);
                    onRemoveListener.accept(position);
                }
            });

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (onClickListener != null && position != -1)
                    onClickListener.accept(position);
            });
        }
    }
}