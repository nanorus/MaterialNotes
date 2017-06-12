package com.example.nanorus.todo.presenter;

import android.widget.Toast;

import com.example.nanorus.todo.bus.EventBus;
import com.example.nanorus.todo.bus.event.UpdateNotesListEvent;
import com.example.nanorus.todo.model.DatabaseManager;
import com.example.nanorus.todo.model.pojo.DateTimePojo;
import com.example.nanorus.todo.model.pojo.NotePojo;
import com.example.nanorus.todo.utils.PreferenceUse;
import com.example.nanorus.todo.view.NoteEditorActivity.NoteEditorActivity;
import com.example.nanorus.todo.view.NoteEditorActivity.NoteEditorView;


public class NoteEditorPresenter implements NoteEditorView.Action {
    private NoteEditorView.View mActivity;
    private PreferenceUse mPreferences;

    public NoteEditorPresenter(NoteEditorActivity activity) {
        mActivity = activity;
        mPreferences = new PreferenceUse(activity);
    }

    @Override
    public void onFabClicked(int type, int position, String name, String description, String priority, DateTimePojo dateTimePojo) {

        try {
            NotePojo notePojo = new NotePojo(name, dateTimePojo, description, Integer.parseInt(priority));
            switch (type) {
                case NoteEditorActivity.INTENT_TYPE_ADD:
                    DatabaseManager.addNote(notePojo, mActivity.getActivity());

                    // add new notification

                    break;

                case NoteEditorActivity.INTENT_TYPE_UPDATE:
                    int id = DatabaseManager.getNoteDbIdByPosition(mActivity.getActivity(), position, mPreferences.loadSortType());
                    DatabaseManager.updateNote(notePojo, mActivity.getActivity(), id);

                    // delete old notification
                    // add new notification

                    break;
            }

            EventBus.getBus().post(new UpdateNotesListEvent(mPreferences.loadSortType()));
            mActivity.onBackPressedView();

        } catch (java.lang.NumberFormatException e) {
            Toast.makeText(mActivity.getActivity(), "Priority must be a NUMBER", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void deleteNote(int position) {
        int id = DatabaseManager.getNoteDbIdByPosition(mActivity.getActivity(), position, mPreferences.loadSortType());
        DatabaseManager.deleteNote(id, mActivity.getActivity());
        mActivity.onBackPressedView();
        EventBus.getBus().post(new UpdateNotesListEvent(mPreferences.loadSortType()));

        // delete notification

    }


    @Override
    public void setFields(int position) {
        int id = DatabaseManager.getNoteDbIdByPosition(mActivity.getActivity(), position, mPreferences.loadSortType());
        NotePojo notePojo = DatabaseManager.getNote(mActivity.getActivity(), id);

        mActivity.setName(notePojo.getName());
        mActivity.setDescription(notePojo.getDescription());
        mActivity.setPriority(String.valueOf(notePojo.getPriority()));

        // set date and time
        if (notePojo.getDateTimePojo() != null) {

            setDateTime(notePojo.getDateTimePojo().getYear(), notePojo.getDateTimePojo().getMonth(),
                    notePojo.getDateTimePojo().getDay(), notePojo.getDateTimePojo().getHour(),
                    notePojo.getDateTimePojo().getMinute()
            );


        }

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
        mActivity.setDateTimeVariables(year, month, day, hour, minute);

        String monthString;
        switch (month) {
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
        String minuteString;
        if (minute < 10) {
            minuteString = "0" + String.valueOf(minute);
        } else minuteString = String.valueOf(minute);

        String hourString;
        if (hour < 10) {
            hourString = "0" + String.valueOf(hour);
        } else hourString = String.valueOf(hour);

        String dateTime = String.valueOf(day) + " " + monthString + " " + String.valueOf(year) +
                "   " + hourString + ":" + minuteString;
        mActivity.setDateTime(dateTime);
    }
}
