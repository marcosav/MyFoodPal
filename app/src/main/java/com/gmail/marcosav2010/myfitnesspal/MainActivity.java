package com.gmail.marcosav2010.myfitnesspal;

import android.os.Bundle;
import android.util.SparseArray;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.gmail.marcosav2010.myfitnesspal.ui.food.FoodFragment;
import com.gmail.marcosav2010.myfitnesspal.ui.settings.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    private FoodFragment foodFragment;
    private SettingsFragment settingsFragment;

    private SparseArray<Fragment> fragments = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragments.put(R.id.navigation_food, foodFragment = new FoodFragment());
        fragments.put(R.id.navigation_settings, settingsFragment = new SettingsFragment());

        FragmentManager fragmentManager = getSupportFragmentManager();

        bottomNavigationView = findViewById(R.id.nav_view);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment, old = fragments.get(bottomNavigationView.getSelectedItemId());
            int enterAnim, exitAnim;

            switch (item.getItemId()) {
                case R.id.navigation_food:
                    fragment = foodFragment;
                    setTitle(item.getTitle());
                    enterAnim = R.anim.enter_from_left;
                    exitAnim = R.anim.exit_to_right;
                    break;
                case R.id.navigation_settings:
                    fragment = settingsFragment;
                    setTitle(item.getTitle());
                    enterAnim = R.anim.enter_from_right;
                    exitAnim = R.anim.exit_to_left;
                    break;
                default:
                    return false;
            }
            FragmentTransaction ft = fragmentManager.beginTransaction();

            if (fragmentManager.findFragmentById(fragment.getId()) == null)
                ft.add(R.id.fragmentHost, fragment);

            ft.setCustomAnimations(enterAnim, exitAnim);

            ft.hide(old);
            ft.show(fragment);
            ft.commit();

            return true;
        });

        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_SELECTED);
        bottomNavigationView.setSelectedItemId(R.id.navigation_food);
    }
}
