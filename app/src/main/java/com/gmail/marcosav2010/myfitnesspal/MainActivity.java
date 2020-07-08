package com.gmail.marcosav2010.myfitnesspal;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.gmail.marcosav2010.myfitnesspal.ui.food.FoodFragment;
import com.gmail.marcosav2010.myfitnesspal.ui.food.FoodFragmentListener;
import com.gmail.marcosav2010.myfitnesspal.ui.settings.SettingsFragment;

public class MainActivity extends AppCompatActivity implements FoodFragmentListener {

    private FoodFragment foodFragment;
    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            foodFragment = (FoodFragment) getSupportFragmentManager().getFragment(savedInstanceState, "foodFragment");
            settingsFragment = (SettingsFragment) getSupportFragmentManager().getFragment(savedInstanceState, "settingsFragment");

        } else {
            foodFragment = new FoodFragment();
            settingsFragment = new SettingsFragment();

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragmentHost, foodFragment);
            ft.show(foodFragment);
            ft.commit();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (foodFragment.isAdded())
            getSupportFragmentManager().putFragment(outState, "foodFragment", foodFragment);
        if (settingsFragment.isAdded())
            getSupportFragmentManager().putFragment(outState, "settingsFragment", settingsFragment);
    }

    @Override
    public void onSettingsOpen() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                R.anim.enter_from_left, R.anim.exit_to_right);
        ft.replace(R.id.fragmentHost, settingsFragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}
