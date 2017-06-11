package com.example.nanorus.todo.presenter;

import android.widget.Toast;

import com.example.nanorus.todo.bus.EventBus;
import com.example.nanorus.todo.bus.event.UpdateNotesListEvent;
import com.example.nanorus.todo.model.database.DatabaseUse;
import com.example.nanorus.todo.model.pojo.DateTimePojo;
import com.example.nanorus.todo.model.pojo.NotePojo;
import com.example.nanorus.todo.utils.PreferenceUse;
import com.example.nanorus.todo.view.NoteEditorActivity.NoteEditorActivity;
import com.example.nanorus.todo.view.NoteEditorActivity.NoteEditorView;


public class NoteEditorPresenter implements NoteEditorView.Action {
    private NoteEditorActivity mActivity;
    private PreferenceUse mPreferences;

    public NoteEditorPresenter(NoteEditorActivity activity) {
        mActivity = activity;
        mPreferences = new PreferenceUse(activity);
    }

    @Override
    public void onFabClicked(int type, int position, String name, String description, String priority,  DateTimePojo dateTimePojo) {


        try {
            NotePojo notePojo = new NotePojo(name, dateTimePojo, description, Integer.parseInt(priority));
            switch (type) {
                case NoteEditorActivity.INTENT_TYPE_ADD:
                    DatabaseUse.addNote(notePojo, mActivity.getActivity());
                    break;

                case NoteEditorActivity.INTENT_TYPE_UPDATE:
                    int id = DatabaseUse.getNoteDbIdByPosition(mActivity, position, mPreferences.loadSortType());
                    DatabaseUse.updateNote(notePojo, mActivity.getActivity(), id);
                    break;
            }

            EventBus.getBus().post(new UpdateNotesListEvent(mPreferences.loadSortType()));
            mActivity.onBackPressed();

        } catch (java.lang.NumberFormatException e) {
            Toast.makeText(mActivity, "Priority must be a NUMBER", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void deleteNote(int position) {
        int id = DatabaseUse.getNoteDbIdByPosition(mActivity, position, mPreferences.loadSortType());
        DatabaseUse.deleteNote(id, mActivity);
        mActivity.onBackPressed();
        EventBus.getBus().post(new UpdateNotesListEvent(mPreferences.loadSortType()));
    }


    @Override
    public void setFields(int position) {
        int id = DatabaseUse.getNoteDbIdByPosition(mActivity, position, mPreferences.loadSortType());
        NotePojo notePojo = DatabaseUse.getNote(mActivity.getActivity(), id);

        mActivity.setName(notePojo.getName());
        mActivity.setDescription(notePojo.getDescription());
        mActivity.setPriority(String.valueOf(notePojo.getPriority()));

        // set date and time

    }

    @Override
    public void setDescriptionSymbolsLengthText(int currentLength, int maxLength) {
        String text;

        if (currentLength < maxLength) {
            text = String.valueOf(currentLength) + "/" + maxLength;
        } else {
            text = "Достигнут лимит символов " + String.valueOf(currentLength) + "/" + maxLength;
        }

        mActivity.setDescriptionSymbolsLengthText(text);
    }

    @Override
    public void releasePresenter() {
        mActivity = null;
    }

    @Override
    public void setDateTime(int year, int month, int day, int hour, int minute) {
        String monthString;
        switch (month){
            case 0:
                monthString = "Jan";
                break;
            case 1:
                monthString = "Feb";
                break;
            case 2:
                monthString = "Mar";
                break;
            case 3:
                monthString = "Apr";
                break;
            case 4:
                monthString = "May";
                break;
            case 5:
                monthString = "Jun";
                break;
            case 6:
                monthString = "Jul";
                break;
            case 7:
                monthString = "Aug";
                break;
            case 8:
                monthString = "Sep";
                break;
            case 9:
                monthString = "Oct";
                break;
            case 10:
                monthString = "Nov";
                break;
            case 11:
                monthString = "Dec";
                break;
            default:
                monthString = "Jan";
                break;
        }

        String dateTime = String.valueOf(day) + " " + monthString + " " + String.valueOf(year) +
                "   " + String.valueOf(hour) + ":" + String.valueOf(minute);
        mActivity.setDateTime(dateTime);
    }
}
