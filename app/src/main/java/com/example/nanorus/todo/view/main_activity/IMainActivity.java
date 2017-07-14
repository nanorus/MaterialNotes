package com.example.nanorus.todo.view.main_activity;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.example.nanorus.todo.model.pojo.MainActivityRotateSavePojo;
import com.example.nanorus.todo.model.pojo.NoteRecyclerPojo;

import java.util.List;

public interface IMainActivity {
    int getPosition();

    void setPosition(int position);

    int getSortType();

    void setSortType(int sortType);

    void setAdapter(List<NoteRecyclerPojo> data);

    void updateAdapter(List<NoteRecyclerPojo> data);

    void showAlert(Context context, String title, String message,
                   String buttonPositiveTitle, String buttonNegativeTitle,
                   AlertDialog.OnClickListener positiveOnClickListener, AlertDialog.OnClickListener negativeOnClickListener);


    void updateNotesList(int sortBy);

    void saveRotateData(MainActivityRotateSavePojo data);

    MainActivityRotateSavePojo loadRotateData();

    boolean isSwipeRefreshing();

    void setSwipeRefreshing(boolean isRefreshing);

    boolean isRotated();

    IMainActivity getView();

    Activity getContext();

}
