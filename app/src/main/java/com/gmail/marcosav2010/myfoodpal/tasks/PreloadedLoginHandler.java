package com.gmail.marcosav2010.myfoodpal.tasks;

import com.gmail.marcosav2010.myfitnesspal.api.LoginException;
import com.gmail.marcosav2010.myfitnesspal.api.LoginHandler;
import com.gmail.marcosav2010.myfoodpal.model.settings.Cookies;

import java.util.Map;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PreloadedLoginHandler implements LoginHandler {

    private final Cookies cookies;

    @Override
    public Map<String, String> login(String url, String username, String password) throws LoginException {
        return cookies.getMap();
    }
}
