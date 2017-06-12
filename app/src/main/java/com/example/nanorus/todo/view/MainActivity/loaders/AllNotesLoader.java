package com.example.nanorus.todo.view.MainActivity.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.nanorus.todo.model.pojo.NoteRecyclerPojo;
import com.example.nanorus.todo.presenter.MainPresenter;

import java.util.List;

public class AllNotesLoader extends AsyncTaskLoader<List<NoteRecyclerPojo>> {

    int mSortBy;
    MainPresenter mPresenter;

    public AllNotesLoader(Context context, MainPresenter presenter, int sortBy) {
        super(context);
        mPresenter = presenter;
        mSortBy = sortBy;
    }


    @Override
    public List<NoteRecyclerPojo> loadInBackground() {
        List<NoteRecyclerPojo> list = mPresenter.getAllNotesRecyclerPojo(mSortBy);
        return list;
    }

    @Override
    public void deliverResult(List<NoteRecyclerPojo> data) {
        super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
    }

    @Override
    protected void onReset() {
        super.onReset();
    }
}
