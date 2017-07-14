package com.example.nanorus.todo.presenter;

import com.example.nanorus.todo.bus.EventBus;
import com.example.nanorus.todo.bus.event.UpdateNotesListEvent;
import com.example.nanorus.todo.model.DatabaseManager;
import com.example.nanorus.todo.model.pojo.MainActivityRotateSavePojo;
import com.example.nanorus.todo.model.pojo.NotePojo;
import com.example.nanorus.todo.model.pojo.NoteRecyclerPojo;
import com.example.nanorus.todo.utils.PreferenceUse;
import com.example.nanorus.todo.view.main_activity.IMainActivity;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainPresenter implements IMainPresenter {
    private IMainActivity mView;
    private PreferenceUse mPreferences;
    private Subscription noteRecyclerPojosSubscriber;
    private Observable<List<NoteRecyclerPojo>> mNoteRecyclerPojosObservable;
    private List<NoteRecyclerPojo> mNoteRecyclerPojos;

    public MainPresenter(IMainActivity view) {
        mView = view;
        mPreferences = new PreferenceUse(view.getContext());

        if (mView.isRotated()) {
            continueSettingNotesList();
        } else {
            mNoteRecyclerPojos = new ArrayList<>();
            mView.setAdapter(mNoteRecyclerPojos);
            setNotesList();
        }
    }

    @Override
    public void setNotesList() {
        mView.setSwipeRefreshing(true);

        if (mNoteRecyclerPojos != null)
            mNoteRecyclerPojos.clear();
        mView.updateAdapter(mNoteRecyclerPojos);

        if (noteRecyclerPojosSubscriber != null) {
            noteRecyclerPojosSubscriber.unsubscribe();
        }

        // getting observable
        Observable<List<NotePojo>> notePojosObservable = DatabaseManager.getAllNotesObservable(mView.getContext(),
                mView.getSortType(), 0);
        mNoteRecyclerPojosObservable = notePojosObservable.map(notePojos -> {
            ArrayList<NoteRecyclerPojo> noteRecyclerPojos = new ArrayList<>();

            for (int i = 0; i < notePojos.size(); i++) {
                NotePojo notePojo = notePojos.get(i);
                noteRecyclerPojos.add(new NoteRecyclerPojo(
                        notePojo.getName(),
                        notePojo.getDateTimePojo(),
                        notePojo.getPriority()
                ));
            }
            return noteRecyclerPojos;
        });

        // subscribing
        noteRecyclerPojosSubscriber =
                mNoteRecyclerPojosObservable
                        // MultiThreading
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<List<NoteRecyclerPojo>>() {
                                       @Override
                                       public void onCompleted() {
                                           noteRecyclerPojosSubscriber.unsubscribe();
                                       }

                                       @Override
                                       public void onError(Throwable e) {

                                       }

                                       @Override
                                       public void onNext(List<NoteRecyclerPojo> noteRecyclerPojos) {
                                           mNoteRecyclerPojos.addAll(noteRecyclerPojos);
                                           mView.updateAdapter(mNoteRecyclerPojos);
                                       }
                                   }
                        );
        mView.setSwipeRefreshing(false);

    }

    @Override
    public void continueSettingNotesList() {
        MainActivityRotateSavePojo savePojo = mView.loadRotateData();
        if (savePojo != null)
            mNoteRecyclerPojos = savePojo.getNoteRecyclerPojos();
        mView.setAdapter(mNoteRecyclerPojos);


        // getting observable
        Observable<List<NotePojo>> notePojosObservable = DatabaseManager.getAllNotesObservable(mView.getContext(),
                mPreferences.loadSortType(), mNoteRecyclerPojos.size());


        mNoteRecyclerPojosObservable = notePojosObservable.map(notePojos -> {
            ArrayList<NoteRecyclerPojo> noteRecyclerPojos = new ArrayList<>();
            for (int i = 0; i < notePojos.size(); i++) {
                NotePojo notePojo = notePojos.get(i);
                noteRecyclerPojos.add(new NoteRecyclerPojo(
                        notePojo.getName(),
                        notePojo.getDateTimePojo(),
                        notePojo.getPriority()
                ));
            }
            return noteRecyclerPojos;
        });
        // subscribing
        noteRecyclerPojosSubscriber =
                mNoteRecyclerPojosObservable
                        // MultiThreading
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<List<NoteRecyclerPojo>>() {
                                       @Override
                                       public void onCompleted() {
                                           noteRecyclerPojosSubscriber.unsubscribe();
                                       }

                                       @Override
                                       public void onError(Throwable e) {

                                       }

                                       @Override
                                       public void onNext(List<NoteRecyclerPojo> noteRecyclerPojos) {
                                           mNoteRecyclerPojos.addAll(noteRecyclerPojos);
                                           mView.updateAdapter(mNoteRecyclerPojos);
                                       }
                                   }
                        );
    }


    @Override
    public void deleteNote() {
        int id = DatabaseManager.getNoteDbIdByPosition(mView.getContext(), mView.getPosition(),
                mPreferences.loadSortType());
        DatabaseManager.deleteNote(id, mView.getContext());
        EventBus.getBus().post(new UpdateNotesListEvent(mPreferences.loadSortType()));
    }


    @Override
    public void releasePresenter() {
        if (!noteRecyclerPojosSubscriber.isUnsubscribed())
            noteRecyclerPojosSubscriber.unsubscribe();
        noteRecyclerPojosSubscriber = null;
        mNoteRecyclerPojosObservable = null;
        mPreferences = null;
        mNoteRecyclerPojos = null;
        mView = null;
    }

    @Override
    public void onTouchClearNotes() {
        DatabaseManager.clearNotes(mView.getContext());
        EventBus.getBus().post(new UpdateNotesListEvent(DatabaseManager.SORT_BY_DATE_CREATING));
    }

    @Override
    public void saveRotateData() {
        mView.saveRotateData(new MainActivityRotateSavePojo(mNoteRecyclerPojos));
    }


}
