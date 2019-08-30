package com.qp.dev.parentalcontrol.Preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.qp.dev.parentalcontrol.Utils.AppLockConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SharedPreference {
    // fungsi menyimpan data di SharedPreferences
    private static final String LOCKED_APP = "locked_app";

    public SharedPreference() {
        super();
    }

    // menyimpan aplikasi locked
    private void saveLocked(Context context, List<String> lockedApp) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences( AppLockConstants.MyPREFERENCES,
                Context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();
        String jsonLockedApp = gson.toJson(lockedApp);
        editor.putString(LOCKED_APP, jsonLockedApp);
        editor.apply();
    }

    public void addLocked(Context context, String app) {
        List<String> lockedApp = getLocked(context);
        if (lockedApp == null)
            lockedApp = new ArrayList<String>();
        lockedApp.add(app);
        saveLocked(context, lockedApp);
    }

    public void removeLocked(Context context, String app) {
        ArrayList<String> locked = getLocked(context);
        if (locked != null) {
            locked.remove(app);
            saveLocked(context, locked);
        }
    }

    public ArrayList<String> getLocked(Context context) {
        SharedPreferences settings;
        List<String> locked;

        settings = context.getSharedPreferences( AppLockConstants.MyPREFERENCES,
                Context.MODE_PRIVATE);

        if (settings.contains(LOCKED_APP)) {
            String jsonLocked = settings.getString(LOCKED_APP, null);
            Gson gson = new Gson();
            String[] lockedItems = gson.fromJson(jsonLocked,
                    String[].class);

            locked = Arrays.asList(lockedItems);
            locked = new ArrayList<String>(locked);
        } else
            return null;
        return (ArrayList<String>) locked;
    }

    public String getPassword(Context context) {
        SharedPreferences passwordPref;
        passwordPref = context.getSharedPreferences(AppLockConstants.MyPREFERENCES, Context.MODE_PRIVATE);
        if (passwordPref.contains(AppLockConstants.PASSWORD)) {
            return passwordPref.getString(AppLockConstants.PASSWORD, "");
        }
        return "";
    }
}