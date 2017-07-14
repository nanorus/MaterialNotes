package com.example.nanorus.todo.presenter;

public interface INoteEditorPresenter {
    void onFabClicked();

    void deleteNote();

    void setFields();

    void setDescriptionSymbolsLengthText();

    void onDescriptionTextChanged();

    void releasePresenter();

    void setDateTime();

}
