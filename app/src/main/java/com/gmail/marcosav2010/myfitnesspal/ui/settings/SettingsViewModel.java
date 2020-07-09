package com.gmail.marcosav2010.myfitnesspal.ui.settings;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.gmail.marcosav2010.myfitnesspal.logic.DataStorer;
import com.gmail.marcosav2010.myfitnesspal.logic.config.PreferenceManager;

import java.util.Map;

public class SettingsViewModel extends AndroidViewModel {

    private DataStorer dataStorer;
    private PreferenceManager preferenceManager;

    public SettingsViewModel(@NonNull Application application) {
        super(application);

        dataStorer = DataStorer.load(application.getApplicationContext());
        preferenceManager = dataStorer.getPreferenceManager();
    }

    public void saveMFPConfig(String data) {
        preferenceManager.saveMFPConfig(data);
    }

    public void saveMFPCredentials(String user, String password) {
        preferenceManager.saveCredentials(user, password);
    }

    public Map<String, Object> getAllConfig() {
        return preferenceManager.getListerData().getAllConfig();
    }

    public String getMFPConfig() {
        return preferenceManager.getMFPConfig();
    }

    public long getLoginDate() {
        return dataStorer.getLoginDate();
    }

    public String getLoginResult() {
        return dataStorer.getLoginResult();
    }
}
