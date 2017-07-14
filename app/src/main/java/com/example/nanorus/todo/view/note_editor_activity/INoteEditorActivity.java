package com.example.nanorus.todo.view.note_editor_activity;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.example.nanorus.todo.model.pojo.DateTimePojo;

public interface INoteEditorActivity {

    int getType();

    int getPosition();

    String getName();

    String getDescription();

    String getPriority();

    DateTimePojo getDateTimePojo();

    int getYear();

    int getMonth();

    int getDay();

    int getHour();

    int getMinute();

    void setDateTime(String dateTime);

    void setName(String name);

    void setPriority(String priority);

    void setDescription(String description);

    void setDateTimeVariables(int year, int month, int day, int hour, int minute);

    int getCurrentDescriptionLength();

    int getMaxDescriptionLength();


    void setTitle(String text);

    void showAlert(Context context, String title, String message,
                   String buttonPositiveTitle, String buttonNegativeTitle,
                   AlertDialog.OnClickListener positiveOnClickListener, AlertDialog.OnClickListener negativeOnClickListener);

    void setDescriptionSymbolsLengthText(String text);

    INoteEditorActivity getView();

    Activity getContext();

    void onBackPressedView();

    void showToastShot(String text);
}
