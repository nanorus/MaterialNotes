package com.example.nanorus.todo.presenter.MainActivity;

import com.example.nanorus.todo.bus.EventBus;
import com.example.nanorus.todo.bus.event.UpdateNotesListEvent;
import com.example.nanorus.todo.model.DatabaseManager;
import com.example.nanorus.todo.model.pojo.NotePojo;
import com.example.nanorus.todo.model.pojo.NoteRecyclerPojo;
import com.example.nanorus.todo.utils.PreferenceUse;
import com.example.nanorus.todo.view.MainActivity.MainActivity;
import com.example.nanorus.todo.view.MainActivity.MainView;

import java.util.ArrayList;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainPresenter implements MainView.Action {
    MainView.View mActivity;
    private PreferenceUse mPreferences;
    Subscription noteRecyclerPojosSubscriber;
    Observable<NoteRecyclerPojo> noteRecyclerPojosObservable;
    ArrayList<NoteRecyclerPojo> noteRecyclerPojos;

    public MainPresenter(MainActivity activity) {
        mActivity = activity;
        mPreferences = new PreferenceUse(activity);

        if (mActivity.isRotated()) {
            continueSettingNotesList();
        } else {
            noteRecyclerPojos = new ArrayList<>();
            mActivity.setAdapter(noteRecyclerPojos);
            setNotesList(mPreferences.loadSortType());
        }
    }

    @Override
    public void setNotesList(int sortBy) {

        if (noteRecyclerPojos!=null)
        noteRecyclerPojos.clear();

        if (noteRecyclerPojosSubscriber != null) {
            noteRecyclerPojosSubscriber.unsubscribe();
        }

        // getting observable
        Observable<NotePojo> notePojosObservable = DatabaseManager.getAllNotesObservable(mActivity.getActivity(), sortBy, 0);
        noteRecyclerPojosObservable = notePojosObservable.map(new Func1<NotePojo, NoteRecyclerPojo>() {

            @Override
            public NoteRecyclerPojo call(NotePojo notePojo) {
                return new NoteRecyclerPojo(
                        notePojo.getName(),
                        notePojo.getDateTimePojo(),
                        notePojo.getPriority());
            }
            // subscribing
        });
        noteRecyclerPojosSubscriber = noteRecyclerPojosObservable
                // MultiThreading
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NoteRecyclerPojo>() {
                               @Override
                               public void onCompleted() {
                                   noteRecyclerPojosSubscriber.unsubscribe();
                               }

                               @Override
                               public void onError(Throwable e) {

                               }

                               @Override
                               public void onNext(NoteRecyclerPojo noteRecyclerPojo) {
                                   noteRecyclerPojos.add(noteRecyclerPojo);
                                   mActivity.updateAdapter(noteRecyclerPojos);

                               }
                           }


                );


    }

    @Override
    public void continueSettingNotesList() {
        MainActivityRotateSavePojo savePojo = mActivity.loadRotateData();

        noteRecyclerPojos = savePojo.getNoteRecyclerPojos();
        mActivity.setAdapter(noteRecyclerPojos);

        // getting observable
        Observable<NotePojo> notePojosObservable = DatabaseManager.getAllNotesObservable(mActivity.getActivity(),
                mPreferences.loadSortType(), noteRecyclerPojos.size());
        noteRecyclerPojosObservable = notePojosObservable.map(new Func1<NotePojo, NoteRecyclerPojo>() {

            @Override
            public NoteRecyclerPojo call(NotePojo notePojo) {
                return new NoteRecyclerPojo(
                        notePojo.getName(),
                        notePojo.getDateTimePojo(),
                        notePojo.getPriority());
            }
            // subscribing
        });

        // noteRecyclerPojosObservable = savePojo.getNoteRecyclerPojosObservable();
        noteRecyclerPojosSubscriber = noteRecyclerPojosObservable
                // MultiThreading
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NoteRecyclerPojo>() {
                               @Override
                               public void onCompleted() {
                                   noteRecyclerPojosSubscriber.unsubscribe();
                               }

                               @Override
                               public void onError(Throwable e) {

                               }

                               @Override
                               public void onNext(NoteRecyclerPojo noteRecyclerPojo) {
                                   noteRecyclerPojos.add(noteRecyclerPojo);
                                   mActivity.updateAdapter(noteRecyclerPojos);
                               }
                           }


                );
    }


    @Override
    public void deleteNote(int position) {
        int id = DatabaseManager.getNoteDbIdByPosition(mActivity.getActivity(), position, mPreferences.loadSortType());
        DatabaseManager.deleteNote(id, mActivity.getActivity());
        EventBus.getBus().post(new UpdateNotesListEvent(mPreferences.loadSortType()));
    }


    @Override
    public void releasePresenter() {
        if (!noteRecyclerPojosSubscriber.isUnsubscribed())
            noteRecyclerPojosSubscriber.unsubscribe();
        noteRecyclerPojosObservable = null;
        mActivity = null;
    }

    @Override
    public void onTouchClearNotes() {
        DatabaseManager.clearNotes(mActivity.getActivity());
        EventBus.getBus().post(new UpdateNotesListEvent(DatabaseManager.SORT_BY_DATE_CREATING));
    }

    @Override
    public void saveRotateData() {
        mActivity.saveRotateData(new MainActivityRotateSavePojo(noteRecyclerPojos));
    }


}
