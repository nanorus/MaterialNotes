package com.example.nanorus.todo.view.MainActivity;

import com.example.nanorus.todo.model.pojo.NotePojo;

import rx.Observable;
import rx.Subscription;

public class NotesRecyclerListObservableSaver {

    Observable<NotePojo> notePojosObservable;
    Subscription noteRecyclerPojosSubscriber;

    public NotesRecyclerListObservableSaver(Observable<NotePojo> notePojosObservable, Subscription noteRecyclerPojosSubscriber) {
        this.notePojosObservable = notePojosObservable;
        this.noteRecyclerPojosSubscriber = noteRecyclerPojosSubscriber;
    }

    public Observable<NotePojo> getNotePojosObservable() {
        return notePojosObservable;
    }

    public void setNotePojosObservable(Observable<NotePojo> notePojosObservable) {
        this.notePojosObservable = notePojosObservable;
    }

    public Subscription getNoteRecyclerPojosSubscriber() {
        return noteRecyclerPojosSubscriber;
    }

    public void setNoteRecyclerPojosSubscriber(Subscription noteRecyclerPojosSubscriber) {
        this.noteRecyclerPojosSubscriber = noteRecyclerPojosSubscriber;
    }
}
