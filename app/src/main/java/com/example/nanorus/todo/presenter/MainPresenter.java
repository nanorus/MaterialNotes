package com.example.nanorus.todo.presenter;

import com.example.nanorus.todo.bus.EventBus;
import com.example.nanorus.todo.bus.event.UpdateNotesListEvent;
import com.example.nanorus.todo.model.DatabaseManager;
import com.example.nanorus.todo.model.pojo.MainActivityRotateSavePojo;
import com.example.nanorus.todo.model.pojo.NotePojo;
import com.example.nanorus.todo.model.pojo.NoteRecyclerPojo;
import com.example.nanorus.todo.utils.PreferenceUse;
import com.example.nanorus.todo.view.main_activity.MainActivity;
import com.example.nanorus.todo.view.main_activity.MainView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainPresenter implements MainView.Action {
    MainView.View mActivity;
    private PreferenceUse mPreferences;
    Subscription noteRecyclerPojosSubscriber;
    Observable<List<NoteRecyclerPojo>> mNoteRecyclerPojosObservable;
    List<NoteRecyclerPojo> mNoteRecyclerPojos;

    public MainPresenter(MainActivity activity) {
        mActivity = activity;
        mPreferences = new PreferenceUse(activity);

        if (mActivity.isRotated()) {
            continueSettingNotesList();
        } else {
            mNoteRecyclerPojos = new ArrayList<>();
            mActivity.setAdapter(mNoteRecyclerPojos);
            setNotesList(mPreferences.loadSortType());
        }
    }

    @Override
    public void setNotesList(int sortBy) {
        mActivity.setSwipeRefreshing(true);

        if (mNoteRecyclerPojos != null)
            mNoteRecyclerPojos.clear();
        mActivity.updateAdapter(mNoteRecyclerPojos);

        if (noteRecyclerPojosSubscriber != null) {
            noteRecyclerPojosSubscriber.unsubscribe();
        }

        // getting observable
        Observable<List<NotePojo>> notePojosObservable = DatabaseManager.getAllNotesObservable(mActivity.getActivity(), sortBy, 0);
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
                                           mActivity.updateAdapter(mNoteRecyclerPojos);
                                       }
                                   }
                        );
        mActivity.setSwipeRefreshing(false);

    }

    @Override
    public void continueSettingNotesList() {
        MainActivityRotateSavePojo savePojo = mActivity.loadRotateData();
        if (savePojo != null)
            mNoteRecyclerPojos = savePojo.getNoteRecyclerPojos();
        mActivity.setAdapter(mNoteRecyclerPojos);


        // getting observable
        Observable<List<NotePojo>> notePojosObservable = DatabaseManager.getAllNotesObservable(mActivity.getActivity(),
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
                                           mActivity.updateAdapter(mNoteRecyclerPojos);
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
        noteRecyclerPojosSubscriber = null;
        mNoteRecyclerPojosObservable = null;
        mPreferences = null;
        mNoteRecyclerPojos = null;
        mActivity = null;
    }

    @Override
    public void onTouchClearNotes() {
        DatabaseManager.clearNotes(mActivity.getActivity());
        EventBus.getBus().post(new UpdateNotesListEvent(DatabaseManager.SORT_BY_DATE_CREATING));
    }

    @Override
    public void saveRotateData() {
        mActivity.saveRotateData(new MainActivityRotateSavePojo(mNoteRecyclerPojos));
    }


}
