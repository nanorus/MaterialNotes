package com.example.nanorus.todo.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.nanorus.todo.model.database.DatabaseUse;

public class PreferenceUse {

    private Context mContext;
    private SharedPreferences mPreferences;



    public PreferenceUse(Context context) {
        mContext = context;
        mPreferences = context.getSharedPreferences("name", Context.MODE_PRIVATE);
    }

    public void saveSortType(int sortType) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt("notesSortType", sortType);
        editor.commit();
    }

    public int loadSortType() {
        return mPreferences.getInt("notesSortType", DatabaseUse.SORT_BY_DATE_CREATING);
    }

}
