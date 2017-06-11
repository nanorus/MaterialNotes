package com.example.nanorus.todo.presenter;

import com.example.nanorus.todo.bus.EventBus;
import com.example.nanorus.todo.bus.event.UpdateNotesListEvent;
import com.example.nanorus.todo.model.database.DatabaseUse;
import com.example.nanorus.todo.model.pojo.NotePojo;
import com.example.nanorus.todo.model.pojo.NoteRecyclerPojo;
import com.example.nanorus.todo.utils.PreferenceUse;
import com.example.nanorus.todo.view.MainActivity.MainActivity;
import com.example.nanorus.todo.view.MainActivity.MainView;

import java.util.ArrayList;
import java.util.List;

public class MainPresenter implements MainView.Action {
    MainActivity mActivity;
    private PreferenceUse mPreferences;

    public MainPresenter(MainActivity activity) {
        mActivity = activity;
        mPreferences = new PreferenceUse(activity);
    }

    @Override
    public List<NoteRecyclerPojo> getAllNotesRecyclerPojo(int sortBy) {
        ArrayList<NoteRecyclerPojo> noteRecyclerPojos = new ArrayList<>();
        List<NotePojo> notePojos = DatabaseUse.getAllNotes(mActivity, sortBy);

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
        int id = DatabaseUse.getNoteDbIdByPosition(mActivity, position, mPreferences.loadSortType());
        DatabaseUse.deleteNote(id, mActivity);
        EventBus.getBus().post(new UpdateNotesListEvent(mPreferences.loadSortType()));
    }


    @Override
    public void releasePresenter() {
        mActivity = null;
    }

    @Override
    public void onTouchClearNotes() {
        DatabaseUse.clearNotes(mActivity);
        EventBus.getBus().post(new UpdateNotesListEvent(DatabaseUse.SORT_BY_DATE_CREATING));
    }
}
