package com.gmail.marcosav2010.mav.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.gmail.marcosav2010.mav.R;
import com.gmail.marcosav2010.mav.ui.food.FoodFragment;

public class SettingsFragment extends Fragment {

    private View root;
    private EditText configText;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_settings, container, false);

        configText = root.findViewById(R.id.configText);
        configText.setText(FoodFragment.CONFIG_DATA);

        return root;
    }
}