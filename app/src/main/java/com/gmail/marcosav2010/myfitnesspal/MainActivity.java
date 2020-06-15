package com.gmail.marcosav2010.myfitnesspal;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.gmail.marcosav2010.myfitnesspal.logic.DataStorer;
import com.gmail.marcosav2010.myfitnesspal.ui.food.FoodFragment;
import com.gmail.marcosav2010.myfitnesspal.ui.settings.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private FoodFragment foodFragment;
    private SettingsFragment settingsFragment;

    private SparseArray<Fragment> fragments = new SparseArray<>();
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataStorer dataStorer = DataStorer.load(getApplicationContext());
        fragments.put(R.id.navigation_food, foodFragment = new FoodFragment(dataStorer));
        fragments.put(R.id.navigation_settings, settingsFragment = new SettingsFragment(dataStorer));

        fragmentManager = getSupportFragmentManager();

        bottomNavigationView = findViewById(R.id.nav_view);

        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_SELECTED);
        bottomNavigationView.setSelectedItemId(R.id.navigation_food);
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment, newF, old = fragments.get(bottomNavigationView.getSelectedItemId());
        int enterAnim = 0, exitAnim = 0;
        CharSequence title;

        switch (item.getItemId()) {
            case R.id.navigation_food:
                fragment = foodFragment;
                title = item.getTitle();
                if (fragmentManager.findFragmentById(fragment.getId()) == null)
                    break;
                enterAnim = R.anim.enter_from_left;
                exitAnim = R.anim.exit_to_right;
                break;
            case R.id.navigation_settings:
                fragment = settingsFragment;
                title = item.getTitle();
                enterAnim = R.anim.enter_from_right;
                exitAnim = R.anim.exit_to_left;
                break;
            default:
                return false;
        }

        newF = fragmentManager.findFragmentById(fragment.getId());

        if (newF != null && old == fragment)
            return false;

        setTitle(title);

        FragmentTransaction ft = fragmentManager.beginTransaction();

        if (newF == null)
            ft.add(R.id.fragmentHost, fragment);

        ft.setCustomAnimations(enterAnim, exitAnim);

        ft.hide(old);
        ft.show(fragment);
        ft.commit();

        return true;
    }
}
