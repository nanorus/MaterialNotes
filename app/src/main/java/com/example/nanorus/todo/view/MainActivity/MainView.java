package com.example.nanorus.todo.view.MainActivity;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.example.nanorus.todo.model.pojo.NoteRecyclerPojo;
import com.example.nanorus.todo.presenter.MainActivity.MainActivityRotateSavePojo;

import java.util.List;

public interface MainView {
    interface Action {
        void setNotesList(int sortBy);

        void continueSettingNotesList();

        void deleteNote(int position);

        void releasePresenter();

        void onTouchClearNotes();

        void saveRotateData();

    }

    interface View {


        void setAdapter(List<NoteRecyclerPojo> data);

        void updateAdapter(List<NoteRecyclerPojo> data);

        void showAlert(Context context, String title, String message,
                       String buttonPositiveTitle, String buttonNegativeTitle,
                       AlertDialog.OnClickListener positiveOnClickListener, AlertDialog.OnClickListener negativeOnClickListener);

        MainActivity getActivity();

        void updateNotesList(int sortBy);

        void saveRotateData(MainActivityRotateSavePojo data);

        MainActivityRotateSavePojo loadRotateData();

        boolean isSwipeRefreshing();

        void setSwipeRefreshing(boolean isRefreshing);

        boolean isRotated();
    }
}
