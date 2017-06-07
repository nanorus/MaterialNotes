package com.example.nanorus.todo.view.NoteEditorActivity;

import android.content.Context;
import android.support.v7.app.AlertDialog;

public interface NoteEditorView {
    interface Action {
        void onFabClicked(int type, int position, String name, String description, String priority, String date);

        void deleteNote(int position);

        void setFields(int position);

        void releasePresenter();
    }

    interface View {

        void setName(String name);

        void setPriority(String priority);

        void setDescription(String description);

        void showAlert(Context context, String title, String message,
                       String buttonPositiveTitle, String buttonNegativeTitle,
                       AlertDialog.OnClickListener positiveOnClickListener, AlertDialog.OnClickListener negativeOnClickListener);

        NoteEditorActivity getActivity();
    }
}
