package com.example.nanorus.todo.view.NoteEditorActivity;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.example.nanorus.todo.model.pojo.DateTimePojo;

public interface NoteEditorView {
    interface Action {
        void onFabClicked(int type, int position, String name, String description, String priority,  DateTimePojo dateTimePojo);

        void deleteNote(int position);

        void setFields(int position);

        /*
        void addNotification( parameters );
        void updateNotification( parameters );
        void deleteNotification( parameters );

        */


        void setDescriptionSymbolsLengthText(int currentLength, int maxLength);

        void releasePresenter();

        void setDateTime (int year, int month, int day, int hour, int minute);



    }

    interface View {

        DateTimePojo getDateTimePojo();

        void setDateTime (String dateTime);

        void setName(String name);

        void setPriority(String priority);

        void setDescription(String description);

        void setDateTimeVariables(int year, int month, int day, int hour, int minute);

        void showAlert(Context context, String title, String message,
                       String buttonPositiveTitle, String buttonNegativeTitle,
                       AlertDialog.OnClickListener positiveOnClickListener, AlertDialog.OnClickListener negativeOnClickListener);

        void setDescriptionSymbolsLengthText(String text);

        NoteEditorActivity getActivity();
        void onBackPressedView();

        void showToastShot(String text);
    }
}
