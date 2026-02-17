package com.example.parqlink;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.parqlink.DTO.ParkingResponse;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavoritesManager {

    private static final String PREFS_NAME = "ParqLinkPrefs";
    private static final String FAVORITES_KEY = "favorites";
    private static final List<ParkingResponse> favorites = new ArrayList<>();
    private static String userEmail;

    public static void init(Context context, String email) {
         userEmail = email;
        SharedPreferences prefs = context.getSharedPreferences("ParqLinkPrefs", Context.MODE_PRIVATE);
        String json = prefs.getString("favorites_" + userEmail, null);
        favorites.clear();
        if (json != null) {
            Type type = new TypeToken<List<ParkingResponse>>() {}.getType();
            List<ParkingResponse> savedFavorites = new Gson().fromJson(json, type);
            if (savedFavorites != null) {
                favorites.addAll(savedFavorites);
            }
        }
    }


    private static void save(Context context) {
        if (userEmail == null) return;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String json = new Gson().toJson(favorites);
        editor.putString("favorites_" + userEmail, json);
        editor.apply();
    }

    public static void addFavorite(ParkingResponse parking, Context context) {
        if (!favorites.contains(parking)) {
            favorites.add(parking);
            save(context);
        }
    }

    public static void removeFavorite(ParkingResponse parking, Context context) {
        favorites.remove(parking);
        save(context);
    }

    public static List<ParkingResponse> getFavorites() {
        return new ArrayList<>(favorites);
    }
}
