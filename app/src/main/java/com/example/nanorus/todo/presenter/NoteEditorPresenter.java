package com.example.nanorus.todo.presenter;

import com.example.nanorus.todo.bus.EventBus;
import com.example.nanorus.todo.bus.event.UpdateNotesListEvent;
import com.example.nanorus.todo.model.DatabaseManager;
import com.example.nanorus.todo.model.pojo.NotePojo;
import com.example.nanorus.todo.utils.PreferenceUse;
import com.example.nanorus.todo.view.note_editor_activity.INoteEditorActivity;
import com.example.nanorus.todo.view.note_editor_activity.NoteEditorActivity;

import java.util.Calendar;


public class NoteEditorPresenter implements INoteEditorPresenter {
    private INoteEditorActivity mView;
    private PreferenceUse mPreferences;

    public NoteEditorPresenter(INoteEditorActivity view) {
        mView = view;
        mPreferences = new PreferenceUse(mView.getContext());
    }

    @Override
    public void onFabClicked() {
        if (!mView.getName().isEmpty()) {
            try {
                NotePojo notePojo = new NotePojo(mView.getName(), mView.getDateTimePojo(),
                        mView.getDescription(), Integer.parseInt(mView.getPriority()));
                switch (mView.getType()) {
                    case NoteEditorActivity.INTENT_TYPE_ADD:
                        DatabaseManager.addNote(notePojo, mView.getContext());
                        // add new notification
                        break;

                    case NoteEditorActivity.INTENT_TYPE_UPDATE:
                        int id = DatabaseManager.getNoteDbIdByPosition(mView.getContext(),
                                mView.getPosition(), mPreferences.loadSortType());
                        DatabaseManager.updateNote(notePojo, mView.getContext(), id);
                        // delete old notification
                        // add new notification
                        break;
                }
                EventBus.getBus().post(new UpdateNotesListEvent(mPreferences.loadSortType()));
                mView.onBackPressedView();
            } catch (java.lang.NumberFormatException e) {
                mView.showToastShot("Enter priority, NUMBER");
            }
        } else {
            mView.showToastShot("Enter Name");
        }


    }

    @Override
    public void deleteNote() {
        int id = DatabaseManager.getNoteDbIdByPosition(mView.getContext(), mView.getPosition(),
                mPreferences.loadSortType());
        DatabaseManager.deleteNote(id, mView.getContext());
        mView.onBackPressedView();
        EventBus.getBus().post(new UpdateNotesListEvent(mPreferences.loadSortType()));
        // delete notification
    }


    @Override
    public void setFields() {
        switch (mView.getType()) {
            case NoteEditorActivity.INTENT_TYPE_ADD:
                mView.setTitle("Add note");
                Calendar c = Calendar.getInstance();
                mView.setDateTimeVariables(
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH),
                        c.get(Calendar.HOUR_OF_DAY),
                        c.get(Calendar.MINUTE)
                );
                setDateTime();
                break;
            case NoteEditorActivity.INTENT_TYPE_UPDATE:
                mView.setTitle("Edit note");

                int id = DatabaseManager.getNoteDbIdByPosition(mView.getContext(), mView.getPosition(),
                        mPreferences.loadSortType());
                NotePojo notePojo = DatabaseManager.getNote(mView.getContext(), id);

                mView.setName(notePojo.getName());
                mView.setDescription(notePojo.getDescription());
                mView.setPriority(String.valueOf(notePojo.getPriority()));

                // set date and time
                if (notePojo.getDateTimePojo() != null) {
                    mView.setDateTimeVariables(
                            notePojo.getDateTimePojo().getYear(),
                            notePojo.getDateTimePojo().getMonth(),
                            notePojo.getDateTimePojo().getDay(),
                            notePojo.getDateTimePojo().getHour(),
                            notePojo.getDateTimePojo().getMinute()
                    );
                    setDateTime();
                }
                break;
        }


    }


    @Override
    public void onDescriptionTextChanged() {
        setDescriptionSymbolsLengthText();
    }

    @Override
    public void setDescriptionSymbolsLengthText() {
        String text;
        int currentLength = mView.getCurrentDescriptionLength();
        int maxLength = mView.getMaxDescriptionLength();
        if (currentLength < maxLength) {
            text = String.valueOf(currentLength) + "/" + maxLength;
        } else {
            text = "Достигнут лимит символов " + String.valueOf(currentLength) + "/" + maxLength;
        }
        mView.setDescriptionSymbolsLengthText(text);
    }

    @Override
    public void releasePresenter() {
        mView = null;
    }

    @Override
    public void setDateTime() {

        String monthString;
        switch (mView.getMonth()) {
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
        if (mView.getMinute() < 10) {
            minuteString = "0" + String.valueOf(mView.getMinute());
        } else minuteString = String.valueOf(mView.getMinute());

        String hourString;
        if (mView.getHour() < 10) {
            hourString = "0" + String.valueOf(mView.getHour());
        } else hourString = String.valueOf(mView.getHour());

        String dateTime = String.valueOf(mView.getDay()) + " " + monthString + " " + String.valueOf(mView.getYear()) +
                ", " + hourString + ":" + minuteString;
        mView.setDateTime(dateTime);
    }
}
