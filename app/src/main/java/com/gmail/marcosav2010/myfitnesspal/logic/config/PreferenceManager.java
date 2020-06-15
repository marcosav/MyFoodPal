package com.gmail.marcosav2010.myfitnesspal.logic.config;

import android.content.SharedPreferences;

import com.gmail.marcosav2010.myfitnesspal.api.lister.ListerData;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PreferenceManager {

    private static final String BASE_CONFIG = "{\"exceptions\":[\"Oregano\",\"Patatas\"],\"aliases\":{\"Cacahuete tostado sin sal\":\"Cacahuetes\"}}";

    private static final String MFP_CONFIG = "mfp_config";
    private static final String MFP_USERNAME = "mfp_username";
    private static final String MFP_PASSWORD = "mfp_password";

    private final SharedPreferences preferences;

    private ListerData ld;

    public ListerData getListerData() {
        if (ld == null)
            reloadListerData();

        return ld;
    }

    private void reloadListerData() {
        ld = new ListerData(getMFPConfig());
        ld.load();
    }

    public void saveMFPConfig(String config) {
        preferences.edit().putString(MFP_CONFIG, config).apply();
        reloadListerData();
    }

    public void saveCredentials(String username, String password) {
        preferences.edit().putString(MFP_USERNAME, username).putString(MFP_PASSWORD, password).apply();
    }

    public String getMFPConfig() {
        return preferences.getString(MFP_CONFIG, BASE_CONFIG);
    }

    public String getMFPUsername() {
        return preferences.getString(MFP_USERNAME, null);
    }

    public String getMFPPassword() {
        return preferences.getString(MFP_PASSWORD, null);
    }
}
