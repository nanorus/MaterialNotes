package com.example.nanorus.todo.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.nanorus.todo.model.DatabaseManager;

public class PreferenceUse {

    private Context mContext;
    private SharedPreferences mPreferences;



    public PreferenceUse(Context context) {
        mContext = context;
        mPreferences = mContext.getSharedPreferences("name", Context.MODE_PRIVATE);
    }

    public void saveSortType(int sortType) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt("notesSortType", sortType);
        editor.apply();
    }

    public int loadSortType() {
        return mPreferences.getInt("notesSortType", DatabaseManager.SORT_BY_DATE_CREATING);
    }

}
