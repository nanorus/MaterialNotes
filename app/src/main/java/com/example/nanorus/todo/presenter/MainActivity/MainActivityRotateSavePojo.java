package com.example.nanorus.todo.presenter.MainActivity;

import com.example.nanorus.todo.model.pojo.NoteRecyclerPojo;

import java.util.List;

public class MainActivityRotateSavePojo {

    List<NoteRecyclerPojo> noteRecyclerPojos;


    public MainActivityRotateSavePojo(List<NoteRecyclerPojo> noteRecyclerPojos) {
        this.noteRecyclerPojos = noteRecyclerPojos;
    }


    public List<NoteRecyclerPojo> getNoteRecyclerPojos() {
        return noteRecyclerPojos;
    }

    public void setNoteRecyclerPojos(List<NoteRecyclerPojo> noteRecyclerPojos) {
        this.noteRecyclerPojos = noteRecyclerPojos;
    }
}
