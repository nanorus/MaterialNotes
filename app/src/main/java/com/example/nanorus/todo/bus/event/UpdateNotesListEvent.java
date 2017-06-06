package com.example.nanorus.todo.bus.event;

public class UpdateNotesListEvent {
    private int mSortBy;

    public UpdateNotesListEvent(int sortBy) {
        mSortBy = sortBy;
    }

    public int getSortBy() {
        return mSortBy;
    }
}
