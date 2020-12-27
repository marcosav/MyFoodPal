package com.gmail.marcosav2010.myfoodpal.storage;

import android.content.Context;
import android.content.SharedPreferences;

import lombok.NonNull;

class ApplicationPreferences {

    private static final String PREFERENCES_NAME = "global_preferences";

    private static SharedPreferences preferences;

    static SharedPreferences load(@NonNull Context c) {
        if (preferences == null)
            preferences = c.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences;
    }
}
