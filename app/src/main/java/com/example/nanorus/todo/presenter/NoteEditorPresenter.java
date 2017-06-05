package com.example.nanorus.todo.presenter;

import android.widget.Toast;

import com.example.nanorus.todo.bus.EventBus;
import com.example.nanorus.todo.bus.event.UpdateNotesListEvent;
import com.example.nanorus.todo.model.database.DatabaseUse;
import com.example.nanorus.todo.model.pojo.NotePojo;
import com.example.nanorus.todo.view.NoteEditorActivity.NoteEditorActivity;
import com.example.nanorus.todo.view.NoteEditorActivity.NoteEditorView;


public class NoteEditorPresenter implements NoteEditorView.Action {
    NoteEditorActivity mActivity;

    public NoteEditorPresenter(NoteEditorActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onFabClicked(int type, int position, String name, String description, String priority, String date) {


        try {
            NotePojo notePojo = new NotePojo(name, date, description, Integer.parseInt(priority));
            switch (type) {
                case NoteEditorActivity.INTENT_TYPE_ADD:
                    DatabaseUse.addNote(notePojo, mActivity.getActivity());
                    break;

                case NoteEditorActivity.INTENT_TYPE_UPDATE:
                    int id = DatabaseUse.getNoteDbIdByPosition(mActivity.getActivity(), position);
                    DatabaseUse.updateNote(notePojo, mActivity.getActivity(), id);
                    break;
            }


            EventBus.getBus().post(new UpdateNotesListEvent());
            mActivity.onBackPressed();

        } catch (java.lang.NumberFormatException e) {
            Toast.makeText(mActivity, "Priority must be a NUMBER", Toast.LENGTH_SHORT).show();
        }



    }



    @Override
    public void setFields(int position) {
        int id = DatabaseUse.getNoteDbIdByPosition(mActivity.getActivity(), position);
        NotePojo notePojo = DatabaseUse.getNote(mActivity.getActivity(), id);

        mActivity.setName(notePojo.getName());
        mActivity.setDescription(notePojo.getDescription());
        mActivity.setPriority(String.valueOf(notePojo.getPriority()));
    }

    @Override
    public void releasePresenter() {
        mActivity = null;
    }
}
