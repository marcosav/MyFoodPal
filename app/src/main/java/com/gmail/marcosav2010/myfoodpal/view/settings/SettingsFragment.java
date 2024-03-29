package com.gmail.marcosav2010.myfoodpal.view.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gmail.marcosav2010.json.JSONException;
import com.gmail.marcosav2010.json.JSONObject;
import com.gmail.marcosav2010.myfoodpal.R;
import com.gmail.marcosav2010.myfoodpal.model.settings.FoodSetting;
import com.gmail.marcosav2010.myfoodpal.model.settings.FoodSettingCategory;
import com.gmail.marcosav2010.myfoodpal.tasks.SessionRequestResult;
import com.gmail.marcosav2010.myfoodpal.viewmodel.settings.SettingsViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Date;
import java.util.Objects;
import java.util.function.Consumer;

public class SettingsFragment extends Fragment {

    private SettingsViewModel viewModel;

    private LinearLayout categoriesLinearLayout;
    private ProgressBar progressBar;

    private Button loginBT;

    private ProgressBar loginPB;

    private Context context;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        context = getContext();

        MaterialToolbar toolbar = root.findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(this::onTopBarMenuOption);
        toolbar.setNavigationOnClickListener(e -> getParentFragmentManager().popBackStack());

        LinearLayout settingsLinearLayout = root.findViewById(R.id.scrollLL);
        categoriesLinearLayout = root.findViewById(R.id.categoryContainer);
        progressBar = root.findViewById(R.id.settingsProgressBar);

        TextView loginDateLB = root.findViewById(R.id.loginDateLB);
        loginDateLB.setOnClickListener(e -> loginDateLB.setText(String.format("%s %s",
                new Date(viewModel.getLoginDate()), viewModel.getLoginResult())));

        loginBT = root.findViewById(R.id.loginBT);
        loginBT.setOnClickListener(e -> onLoginClick());

        loginPB = root.findViewById(R.id.loginPB);

        settingsLinearLayout.addView(createSectionTitle(getString(R.string.s_credentials_title)), 1);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);

        RecyclerView.RecycledViewPool pool = new RecyclerView.RecycledViewPool();

        viewModel.getLoadedConfig().observe(getViewLifecycleOwner(), c -> {
            if (c != null) {
                categoriesLinearLayout.removeAllViews();

                c.forEach((k, cat) -> createSettingCategory(cat, pool));

                progressBar.setVisibility(View.GONE);
                categoriesLinearLayout.setVisibility(View.VISIBLE);

            } else {
                progressBar.setVisibility(View.VISIBLE);

                categoriesLinearLayout.setVisibility(View.INVISIBLE);
            }
        });

        viewModel.getSessionLoadStatus().observe(getViewLifecycleOwner(), r -> {
            if (r != null) {
                loginPB.setVisibility(View.INVISIBLE);

                SessionRequestResult.Type type = r.getType();

                if (type == SessionRequestResult.Type.SUCCESS) {
                    Toast.makeText(context, R.string.settings_credentials_saved, Toast.LENGTH_LONG).show();

                } else {
                    String msg = getString(R.string.settings_error_logging) + getString(type.getMsg());
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void createSettingCategory(FoodSettingCategory cat, RecyclerView.RecycledViewPool pool) {
        View container = LayoutInflater.from(context)
                .inflate(R.layout.setting_category, categoriesLinearLayout, false);

        RecyclerView recyclerView = container.findViewById(R.id.settingRecycler);
        TextView title = container.findViewById(R.id.settingTitle);
        ImageView addBT = container.findViewById(R.id.addSettingBT);

        title.setText(cat.getName());

        FoodSettingAdapter adapter = new FoodSettingAdapter(
                cat.getSettings(),
                i -> viewModel.removeSetting(cat.getKey(), i),
                i -> showEditInput(cat, recyclerView, i)
        );

        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setRecycledViewPool(pool);
        recyclerView.setLayoutManager(new LinearLayoutManager(context,
                LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setAdapter(adapter);

        addBT.setOnClickListener(v -> showInsertInput(cat, recyclerView));

        categoriesLinearLayout.addView(container);
    }

    private static final int LAUNCH_SECOND_ACTIVITY = 1423;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SECOND_ACTIVITY)
            if (resultCode == Activity.RESULT_OK)
                handleResult(parseSerializedCookies(data));
    }

    private void onLoginClick() {
        Intent intent = new Intent(getContext(), MFPLoginActivity.class);
        intent.putExtra("url", "https://www.myfitnesspal.com/account/login");
        startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY);
    }

    private String parseSerializedCookies(Intent data) {
        return data.getStringExtra("cookies");
    }

    private void handleResult(String serializedCookies) {
        loginPB.setVisibility(View.VISIBLE);
        viewModel.handleCookies(serializedCookies);
    }

    private void showInput(String title,
                           FoodSetting baseSetting,
                           FoodSettingCategory cat,
                           Consumer<FoodSetting> onClick) {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(title);

        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(60, 30, 60, 30);

        FoodSetting setting = baseSetting == null ? new FoodSetting("") : baseSetting;

        EditText keyInput = new EditText(context);
        keyInput.setInputType(InputType.TYPE_CLASS_TEXT);
        keyInput.setHint(R.string.name_input_hint);
        if (!TextUtils.isEmpty(setting.getFirst()))
            keyInput.setText(setting.getFirst());
        ll.addView(keyInput);

        EditText valueInput = new EditText(context);

        if (cat.isEntry()) {
            valueInput.setInputType(InputType.TYPE_CLASS_TEXT);
            valueInput.setHint(R.string.value_input_hint);
            if (setting.getSecond() != null)
                valueInput.setText(setting.getSecond().toString());
            ll.addView(valueInput);
        }

        builder.setView(ll);

        builder.setPositiveButton(getString(android.R.string.ok), (dialog, which) -> {
            String first = keyInput.getText().toString().trim();
            if (first.isEmpty())
                return;

            String second = valueInput.getText().toString().trim();
            if (second.isEmpty() && cat.isEntry())
                return;

            setting.setFirst(first);

            if (cat.isEntry())
                setting.setSecond(second);

            onClick.accept(setting);
        });

        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showInsertInput(FoodSettingCategory cat, RecyclerView recyclerView) {
        showInput("Insert", null, cat, result -> {
            viewModel.insertSetting(cat.getKey(), result);
            Objects.requireNonNull(recyclerView.getAdapter()).notifyItemInserted(0);
            recyclerView.scrollToPosition(0);
        });
    }

    private void showEditInput(FoodSettingCategory cat,
                               RecyclerView recyclerView,
                               int position) {

        showInput("Edit", cat.getSettings().get(position), cat, result ->
                Objects.requireNonNull(recyclerView.getAdapter()).notifyItemChanged(position));
    }

    private TextView createSectionTitle(String name) {
        TextView title = new TextView(context);

        title.setText(name.toUpperCase());
        title.setTextSize(14);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(ContextCompat.getColor(context, R.color.primaryColor));
        title.setPadding(40, 50, 0, 40);

        return title;
    }

    private void savePreferences() {
        viewModel.saveMFPConfig();
        Toast.makeText(context, R.string.settings_saved, Toast.LENGTH_LONG).show();
    }

    private void exportPreferences() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle("Raw Config");

        EditText input = new EditText(requireContext());
        input.setText(viewModel.getMFPConfig());

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            String newConf = input.getText().toString();

            try {
                JSONObject parsed = new JSONObject(newConf);
                viewModel.saveMFPConfig(parsed);

            } catch (JSONException ex) {
                Toast.makeText(context, R.string.settings_bad_format, Toast.LENGTH_LONG).show();
                return;
            }

            dialog.dismiss();
            Toast.makeText(context, R.string.settings_saved, Toast.LENGTH_LONG).show();
        });

        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private boolean onTopBarMenuOption(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sm_reload -> viewModel.reload();
            case R.id.sm_save -> savePreferences();
            case R.id.sm_export -> exportPreferences();
        }
        return super.onOptionsItemSelected(item);
    }
}