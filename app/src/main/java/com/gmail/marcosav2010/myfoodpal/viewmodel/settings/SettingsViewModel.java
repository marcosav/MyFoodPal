package com.gmail.marcosav2010.myfoodpal.viewmodel.settings;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gmail.marcosav2010.json.JSONObject;
import com.gmail.marcosav2010.myfoodpal.model.settings.FoodSetting;
import com.gmail.marcosav2010.myfoodpal.model.settings.FoodSettingCategory;
import com.gmail.marcosav2010.myfoodpal.storage.SessionStorage;
import com.gmail.marcosav2010.myfoodpal.storage.PreferenceManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class SettingsViewModel extends AndroidViewModel {

    private final SessionStorage sessionStorage;
    private final PreferenceManager preferenceManager;

    private final MutableLiveData<Map<String, FoodSettingCategory>> loadedConfig = new MutableLiveData<>();

    private Map<String, FoodSettingCategory> config;

    public SettingsViewModel(@NonNull Application application) {
        super(application);

        sessionStorage = SessionStorage.load(application.getApplicationContext());
        preferenceManager = PreferenceManager.load(application.getApplicationContext());

        new LoadAsyncTask().execute();
    }

    public void reload() {
        loadedConfig.setValue(null);
        new LoadAsyncTask().execute();
    }

    public LiveData<Map<String, FoodSettingCategory>> getLoadedConfig() {
        return loadedConfig;
    }

    public void saveMFPConfig(JSONObject settings) {
        preferenceManager.saveMFPConfig(settings.toString());
        reload();
    }

    public void saveMFPConfig() {
        JSONObject json = new JSONObject();
        config.forEach((k, cat) -> {
            List<FoodSetting> settings = cat.getSettings();
            if (!cat.isEntry())
                json.put(k, settings.stream()
                        .map(FoodSetting::getFirst)
                        .collect(Collectors.toSet())
                        .stream()
                        .sorted()
                        .collect(Collectors.toList()));
            else {
                json.put(k, settings.stream()
                        .collect(Collectors.toMap(
                                FoodSetting::getFirst,
                                FoodSetting::getSecond,
                                (s1, s2) -> s1,
                                () -> new TreeMap<>(String::compareToIgnoreCase))));
            }
        });

        saveMFPConfig(json);
    }

    public void saveMFPCredentials(String user, String password) {
        preferenceManager.saveCredentials(user, password);
    }

    public String getMFPConfig() {
        return preferenceManager.getMFPConfig();
    }

    public long getLoginDate() {
        return sessionStorage.getLoginDate();
    }

    public String getLoginResult() {
        return sessionStorage.getLoginResult();
    }

    public void removeSetting(String category, int position) {
        Objects.requireNonNull(config.get(category)).remove(position);
    }

    public void updateSetting(String category, int position, FoodSetting setting) {
        Objects.requireNonNull(config.get(category)).update(position, setting);
    }

    public void insertSetting(String category, FoodSetting setting) {
        Objects.requireNonNull(config.get(category)).insert(setting);
    }

    @SuppressLint("StaticFieldLeak")
    @SuppressWarnings("unchecked")
    private final class LoadAsyncTask extends AsyncTask<Void, Void, Map<String, FoodSettingCategory>> {

        @Override
        protected Map<String, FoodSettingCategory> doInBackground(Void... voids) {
            try {
                Thread.sleep(200L);
            } catch (InterruptedException ignored) {
            }

            Map<String, FoodSettingCategory> mm = new HashMap<>();

            preferenceManager.getListerData().getAllConfig().forEach((k, v) -> {
                String name = k.replaceFirst("p_", "").replaceAll("_", " ");
                name = name.substring(0, 1).toUpperCase() + name.substring(1);

                boolean entries = false;

                List<FoodSetting> settings;
                if (v instanceof Collection)
                    settings = ((Collection<?>) v)
                            .stream()
                            .map(Object::toString)
                            .sorted(String::compareToIgnoreCase)
                            .map(FoodSetting::new)
                            .collect(Collectors.toList());

                else if (v instanceof Map) {
                    settings = ((Map<String, Object>) v)
                            .entrySet()
                            .stream()
                            .sorted((e1, e2) -> e1.getKey().compareToIgnoreCase(e2.getKey()))
                            .map(e -> new FoodSetting(e.getKey(), e.getValue()))
                            .collect(Collectors.toList());

                    entries = true;
                } else return;

                mm.put(k, new FoodSettingCategory(name, k, entries, settings));
            });

            return mm;
        }

        @Override
        protected void onPostExecute(Map<String, FoodSettingCategory> res) {
            config = res;
            loadedConfig.postValue(config);
        }
    }
}
