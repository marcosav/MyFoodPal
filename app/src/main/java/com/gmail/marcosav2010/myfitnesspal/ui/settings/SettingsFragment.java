package com.gmail.marcosav2010.myfitnesspal.ui.settings;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.cunoraz.tagview.Tag;
import com.gmail.marcosav2010.json.JSONObject;
import com.gmail.marcosav2010.myfitnesspal.R;
import com.gmail.marcosav2010.myfitnesspal.logic.DataStorer;
import com.gmail.marcosav2010.myfitnesspal.logic.config.PreferenceManager;
import com.gmail.marcosav2010.myfitnesspal.logic.food.MFPSessionRequestResult;
import com.gmail.marcosav2010.myfitnesspal.logic.food.SessionRequestTask;
import com.gmail.marcosav2010.myfitnesspal.ui.settings.tag.ETag;
import com.gmail.marcosav2010.myfitnesspal.ui.settings.tag.TagFactory;
import com.gmail.marcosav2010.myfitnesspal.ui.settings.tag.TagGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SettingsFragment extends Fragment {

    private View root;

    private LinearLayout settingsLinearLayout, tagGroupsLinearLayout;
    private ProgressBar progressBar;

    private DataStorer dataStorer;
    private PreferenceManager preferenceManager;

    private TextInputEditText usernameET, passwordET;
    private TextView credentialsHeader, loginDateLB;

    private Map<String, TagGroup> tagGroups;

    private TagFactory tf;

    private Set<String> viewStack;

    private ProgressBar loginPB;

    private Context context;

    public SettingsFragment(DataStorer dataStorer) {
        viewStack = Collections.synchronizedSet(new HashSet<>());
        tagGroups = new HashMap<>();
        this.dataStorer = dataStorer;
        preferenceManager = dataStorer.getPreferenceManager();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_settings, container, false);

        context = root.getContext();

        tf = new TagFactory(context);

        settingsLinearLayout = root.findViewById(R.id.scrollLL);
        tagGroupsLinearLayout = root.findViewById(R.id.tagGroupsLL);
        progressBar = root.findViewById(R.id.settingsProgressBar);

        loginDateLB = root.findViewById(R.id.loginDateLB);
        loginDateLB.setOnClickListener(e -> loginDateLB.setText(String.format("%s %s", new Date(dataStorer.getLoginDate()), dataStorer.getLoginResult())));

        usernameET = root.findViewById(R.id.usernameET);
        passwordET = root.findViewById(R.id.passwordET);

        loginPB = root.findViewById(R.id.loginPB);

        settingsLinearLayout.addView(credentialsHeader = createSectionTitle(getString(R.string.s_credentials_title)), 0);

        load();
        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.settings_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sm_reload:
                load();
                break;

            case R.id.sm_save:
                savePreferences();
        }
        return super.onOptionsItemSelected(item);
    }

    private void load() {
        settingsLinearLayout.animate()
                .alpha(0f)
                .setDuration(100)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        settingsLinearLayout.setVisibility(View.GONE);
                        new LoadAsyncTask().execute();
                    }
                });

        progressBar.setVisibility(View.VISIBLE);
    }

    private void clearMFPConfigSections() {
        tagGroups.clear();
        viewStack.clear();
        tagGroupsLinearLayout.removeAllViews();
    }

    private void addContent() {
        clearMFPConfigSections();

        preferenceManager.getListerData().getAllConfig().forEach((k, v) -> {
            TagGroup tags;

            String name = k.replaceFirst("p_", "").replaceAll("_", " ");
            name = name.substring(0, 1).toUpperCase() + name.substring(1);

            if (v instanceof Collection) {
                tags = tf.createTagView(name, false);

                Collection<String> c = (Collection<String>) v;
                for (String s : c)
                    tags.addTag(tf.createTag(s));

            } else if (v instanceof Map) {
                tags = tf.createTagView(name, true);

                Map<String, ?> m = (Map<String, ?>) v;
                for (Map.Entry<String, ?> e : m.entrySet())
                    tags.addTag(tf.createETag(e.getKey(), e.getValue()));

            } else return;/* else if (v instanceof Multimap) {
                Multimap<String, Integer> m = (Multimap<String, Integer>) v;
                for (Map.Entry<String, Collection<Integer>> e : m.asMap().entrySet()) {
                    Tag tk = createTag(e.getKey(), true), tv;
                    tags.addTag(tk);
                    tags.addTag(createSeparatorTag());

                    for (Object ev : e.getValue()) {
                        tv = createTag(ev, false);
                        tags.addTag(tv);
                    }
                }
            }*/

            tags.addTag(tf.createAddTag());

            createMFPConfigSection(name, k, tags);
        });

        settingsLinearLayout.setVisibility(View.VISIBLE);
    }

    private void createMFPConfigSection(String name, String k, TagGroup tags) {
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(20, 20, 20, 20);
        ll.setLayoutParams(lp);

        tagGroupsLinearLayout.addView(createSectionTitle(name));
        ll.addView(tags);
        tagGroupsLinearLayout.addView(ll);

        tagGroups.put(k, tags);
        viewStack.add(k);

        tags.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tags.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                viewStack.remove(k);

                if (viewStack.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    settingsLinearLayout.animate().alpha(1f).setDuration(150).setListener(new AnimatorListenerAdapter() {
                    });
                }
            }
        });
    }

    private TextView createSectionTitle(String name) {
        TextView title = new TextView(context);
        title.setText(name);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(ContextCompat.getColor(context, R.color.white));
        title.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLightPrimary));
        title.setPadding(0, 15, 0, 15);
        title.setElevation(8);

        return title;
    }

    private void onCredentialsCheck(MFPSessionRequestResult r, Editable username, Editable password) {
        MFPSessionRequestResult.Type type = r.getType();
        loginPB.setVisibility(View.INVISIBLE);

        if (type == MFPSessionRequestResult.Type.SUCCESS) {
            preferenceManager.saveCredentials(username.toString(), password.toString());

            username.clear();
            password.clear();

            usernameET.clearFocus();
            passwordET.clearFocus();

            credentialsHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSuccess));

            Toast.makeText(context, R.string.settings_credentials_saved, Toast.LENGTH_LONG).show();

        } else {
            String msg = getString(R.string.settings_error_logging) + getString(type.getMsg());

            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

            credentialsHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.colorError));
        }
    }

    private void savePreferences() {
        Toast.makeText(context, R.string.settings_saving, Toast.LENGTH_LONG).show();

        saveMFPPreferences();

        Editable username = usernameET.getText(), password = passwordET.getText();

        if (username.length() != 0 && password.length() != 0) {
            Toast.makeText(context, R.string.settings_saving_and_checking_credentials, Toast.LENGTH_LONG).show();
            loginPB.setVisibility(View.VISIBLE);
            new SessionRequestTask(context, r -> onCredentialsCheck(r, username, password)).execute(username.toString(), password.toString());
        } else
            Toast.makeText(context, R.string.settings_saved, Toast.LENGTH_LONG).show();
    }

    private void saveMFPPreferences() {
        JSONObject json = new JSONObject();
        tagGroups.forEach((n, g) -> {
            List<Tag> tags = g.getTags();
            if (!g.isEntry())
                json.put(n, tags.stream().limit(tags.size() - 1).map(t -> t.text).collect(Collectors.toSet()));
            else
                json.put(n, tags.stream().limit(tags.size() - 1).map(tag -> (ETag) tag).collect(Collectors.toMap(t -> t.key, t -> t.value)));
        });

        preferenceManager.saveMFPConfig(json.toString());
        preferenceManager.reloadListerData();
    }

    private final class LoadAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(500l);
            } catch (Exception ex) {
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            addContent();
        }
    }
}