package com.silva.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import com.chachat.android.R;
import com.google.gson.Gson;
import com.silva.objects.User;

/**
 * Created by ceosi_000 on 5/19/2014.
 */

public class UserManager {

    private static final String TAG = "USER MANAGER LOG";

    public static User getUser(Context context) {

        Resources res = context.getResources();
        Gson gson = new Gson();
        SharedPreferences myPrefs = context.getSharedPreferences(
                res.getString(R.string.app_package), 0);
        String json = myPrefs.getString("User", "");
        User user = gson.fromJson(json, User.class);

        Log.i(TAG, "" + json);
        return user;

    }

    public static void saveUser(User user, Context context) {

        Resources res = context.getResources();
        SharedPreferences myPrefs = context.getSharedPreferences(
                res.getString(R.string.app_package), 0);
        SharedPreferences.Editor e = myPrefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(user);
        e.putString("User", json);
        e.commit();

        Log.i("User SUCCESSFULLY SAVED", "" + json);

    }

    public static void deleteUser(Context context) {
        Resources res = context.getResources();
        SharedPreferences myPrefs = context.getSharedPreferences(
                res.getString(R.string.app_package), 0);
        SharedPreferences.Editor e = myPrefs.edit();
        e.remove("User");
        e.commit();
    }

}
