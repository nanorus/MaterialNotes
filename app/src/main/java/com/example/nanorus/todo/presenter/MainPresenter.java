package com.example.nanorus.todo.presenter;

import com.example.nanorus.todo.bus.EventBus;
import com.example.nanorus.todo.bus.event.UpdateNotesListEvent;
import com.example.nanorus.todo.model.DatabaseManager;
import com.example.nanorus.todo.model.pojo.NotePojo;
import com.example.nanorus.todo.model.pojo.NoteRecyclerPojo;
import com.example.nanorus.todo.utils.PreferenceUse;
import com.example.nanorus.todo.view.MainActivity.MainActivity;
import com.example.nanorus.todo.view.MainActivity.MainView;

import java.util.ArrayList;
import java.util.List;

public class MainPresenter implements MainView.Action {
    MainView.View mActivity;
    private PreferenceUse mPreferences;

    public MainPresenter(MainActivity activity) {
        mActivity = activity;
        mPreferences = new PreferenceUse(activity);
    }

    @Override
    public List<NoteRecyclerPojo> getAllNotesRecyclerPojo(int sortBy) {


        ArrayList<NoteRecyclerPojo> noteRecyclerPojos = new ArrayList<>();
        List<NotePojo> notePojos = DatabaseManager.getAllNotes(mActivity.getActivity(), sortBy);

        for (int i = 0; i < notePojos.size(); i++){
            noteRecyclerPojos.add(new NoteRecyclerPojo(
                    notePojos.get(i).getName(),
                    notePojos.get(i).getDateTimePojo(),
                    notePojos.get(i).getPriority()
            ));
        }
        return noteRecyclerPojos;
    }



    @Override
    public void deleteNote(int position) {
        int id = DatabaseManager.getNoteDbIdByPosition(mActivity.getActivity(), position, mPreferences.loadSortType());
        DatabaseManager.deleteNote(id, mActivity.getActivity());
        EventBus.getBus().post(new UpdateNotesListEvent(mPreferences.loadSortType()));
    }


    @Override
    public void releasePresenter() {
        mActivity = null;
    }

    @Override
    public void onTouchClearNotes() {
        DatabaseManager.clearNotes(mActivity.getActivity());
        EventBus.getBus().post(new UpdateNotesListEvent(DatabaseManager.SORT_BY_DATE_CREATING));
    }



}
