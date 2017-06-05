package com.example.nanorus.todo.view.NoteEditorActivity;

public interface NoteEditorView {
    interface Action {
        void onFabClicked(int type, int position, String name, String description, int priority, String date);

        void setFields(int position);

        void releasePresenter();
    }

    interface View {

        void setName(String name);

        void setDescription(String description);

        NoteEditorActivity getActivity();
    }
}
