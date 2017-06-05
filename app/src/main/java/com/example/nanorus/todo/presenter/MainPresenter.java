package com.example.nanorus.todo.presenter;

import com.example.nanorus.todo.bus.EventBus;
import com.example.nanorus.todo.bus.event.UpdateNotesListEvent;
import com.example.nanorus.todo.model.database.DatabaseUse;
import com.example.nanorus.todo.model.pojo.NotePojo;
import com.example.nanorus.todo.model.pojo.NoteRecyclerPojo;
import com.example.nanorus.todo.view.MainActivity.MainActivity;
import com.example.nanorus.todo.view.MainActivity.MainView;

import java.util.ArrayList;
import java.util.List;

public class MainPresenter implements MainView.Action {
    MainActivity mActivity;

    public MainPresenter(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    public List<NoteRecyclerPojo> getAllNotesRecyclerPojo() {
        ArrayList<NoteRecyclerPojo> noteRecyclerPojos = new ArrayList<>();
        List<NotePojo> notePojos = DatabaseUse.getAllNotes(mActivity.getActivity());

        for (int i = 0; i < notePojos.size(); i++){
            noteRecyclerPojos.add(new NoteRecyclerPojo(
                    notePojos.get(i).getName(),
                    null,
                    notePojos.get(i).getPriority()
            ));
        }
        return noteRecyclerPojos;
    }



    @Override
    public void releasePresenter() {
        mActivity = null;
    }

    @Override
    public void onTouchClearNotes() {
        DatabaseUse.clearNotes(mActivity.getActivity());
        EventBus.getBus().post(new UpdateNotesListEvent());
    }
}
