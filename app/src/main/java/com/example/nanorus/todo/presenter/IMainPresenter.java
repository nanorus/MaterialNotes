package com.example.nanorus.todo.presenter;

public interface IMainPresenter {

    void setNotesList();

    void continueSettingNotesList();

    void deleteNote();

    void releasePresenter();

    void onTouchClearNotes();

    void saveRotateData();

}
