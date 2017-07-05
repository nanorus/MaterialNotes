package com.example.nanorus.todo.model.pojo;

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
