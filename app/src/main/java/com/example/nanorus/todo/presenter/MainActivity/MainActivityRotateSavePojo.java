package com.example.nanorus.todo.presenter.MainActivity;

import com.example.nanorus.todo.model.pojo.NoteRecyclerPojo;

import java.util.ArrayList;

public class MainActivityRotateSavePojo {

    ArrayList<NoteRecyclerPojo> noteRecyclerPojos;


    public MainActivityRotateSavePojo(ArrayList<NoteRecyclerPojo> noteRecyclerPojos) {
        this.noteRecyclerPojos = noteRecyclerPojos;
    }


    public ArrayList<NoteRecyclerPojo> getNoteRecyclerPojos() {
        return noteRecyclerPojos;
    }

    public void setNoteRecyclerPojos(ArrayList<NoteRecyclerPojo> noteRecyclerPojos) {
        this.noteRecyclerPojos = noteRecyclerPojos;
    }
}
