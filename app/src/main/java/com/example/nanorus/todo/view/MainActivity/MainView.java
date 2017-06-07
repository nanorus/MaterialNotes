package com.example.nanorus.todo.view.MainActivity;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.example.nanorus.todo.model.pojo.NoteRecyclerPojo;

import java.util.List;

public interface MainView {
    interface Action {
        List<NoteRecyclerPojo> getAllNotesRecyclerPojo(int sortBy);

        void deleteNote(int position);

        void releasePresenter();

        void onTouchClearNotes();

    }

    interface View {
        void showAlert(Context context, String title, String message,
                       String buttonPositiveTitle, String buttonNegativeTitle,
                       AlertDialog.OnClickListener positiveOnClickListener, AlertDialog.OnClickListener negativeOnClickListener);

        MainActivity getActivity();

        void updateNotesList(int sortBy);
    }
}
