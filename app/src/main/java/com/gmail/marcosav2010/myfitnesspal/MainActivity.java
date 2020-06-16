package com.gmail.marcosav2010.myfitnesspal;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.gmail.marcosav2010.myfitnesspal.ui.food.FoodFragment;
import com.gmail.marcosav2010.myfitnesspal.ui.food.FoodFragmentListener;
import com.gmail.marcosav2010.myfitnesspal.ui.settings.SettingsFragment;

public class MainActivity extends AppCompatActivity implements FoodFragmentListener {

    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FoodFragment foodFragment = new FoodFragment();
        settingsFragment = new SettingsFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragmentHost, foodFragment);
        ft.show(foodFragment);
        ft.commit();
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
