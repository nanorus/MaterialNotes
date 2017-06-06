package com.example.nanorus.todo.view.MainActivity;

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
        MainActivity getActivity();

        void updateNotesList(int sortBy);
    }
}
