package com.example.nanorus.todo.view.MainActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.nanorus.nanojunior.R;
import com.example.nanorus.todo.bus.EventBus;
import com.example.nanorus.todo.bus.event.UpdateNotesListEvent;
import com.example.nanorus.todo.model.pojo.NoteRecyclerPojo;
import com.example.nanorus.todo.presenter.MainPresenter;
import com.example.nanorus.todo.view.NoteEditorActivity.NoteEditorActivity;
import com.example.nanorus.todo.view.ui.adapters.NotesRecyclerViewAdapter;
import com.example.nanorus.todo.view.ui.recyclerView.ItemClickSupport;
import com.squareup.otto.Subscribe;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MainView.View {
    MainPresenter mPresenter;
    Toolbar mToolbar;
    FloatingActionButton mFab;
    RecyclerView mNotesList;
    SwipeRefreshLayout mSwipeRefresh;
    ImageView tb_clear;
    ImageView list_item_note_iv_priority_color;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPresenter = new MainPresenter(getActivity());
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mFab = (FloatingActionButton) findViewById(R.id.main_fab_add);
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.main_swipeRefresh);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mNotesList = (RecyclerView) findViewById(R.id.main_rv_notesList);
        tb_clear = (ImageView) findViewById(R.id.main_tb_clear);
        list_item_note_iv_priority_color = (ImageView) findViewById(R.id.list_item_note_iv_priority_color);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mNotesList.setLayoutManager(manager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mNotesList.getContext(),
                manager.getOrientation());
        mNotesList.addItemDecoration(dividerItemDecoration);

        ItemClickSupport.addTo(mNotesList).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent intent = new Intent(getActivity(), NoteEditorActivity.class);
                intent.putExtra("type", NoteEditorActivity.INTENT_TYPE_UPDATE);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
        ItemClickSupport.addTo(mNotesList).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, final int position, View v) {
                final CharSequence[] items = {"Yes", "No"};
                final int DIALOG_YES = 0;
                final int DIALOG_NO = 1;

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("Delete the note?");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case DIALOG_YES:
                                mPresenter.deleteNote(position);
                                break;
                            case DIALOG_NO:
                                dialog.dismiss();
                                break;
                        }

                    }
                });
                builder.show();

                return false;
            }
        });


        EventBus.getBus().register(this);
        EventBus.getBus().post(new UpdateNotesListEvent());

        setListeners();


    }


    @Override
    public MainActivity getActivity() {
        return this;
    }

    @Override
    public void updateNotesList() {
        List<NoteRecyclerPojo> list = mPresenter.getAllNotesRecyclerPojo();
        NotesRecyclerViewAdapter adapter = new NotesRecyclerViewAdapter(list);
        mNotesList.setAdapter(adapter);
    }

    @Subscribe
    public void updateNotesListListener(UpdateNotesListEvent event) {
        updateNotesList();
    }

    @Override
    protected void onDestroy() {
        mPresenter.releasePresenter();
        EventBus.getBus().unregister(this);
        mPresenter = null;
        super.onDestroy();
    }

    void setListeners() {
        tb_clear.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                CharSequence[] items = {"Yes", "No"};
                final int DIALOG_YES = 0;
                final int DIALOG_NO = 1;

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Delete ALL notes?");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DIALOG_YES:
                                mPresenter.onTouchClearNotes();
                                break;
                            case DIALOG_NO:
                                dialog.dismiss();
                                break;
                        }
                    }
                });
                builder.show();
                return false;
            }
        });

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                EventBus.getBus().post(new UpdateNotesListEvent());
                mSwipeRefresh.setRefreshing(false);
            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("type", NoteEditorActivity.INTENT_TYPE_ADD);
                startActivity(new Intent(getActivity(), NoteEditorActivity.class));
            }
        });
    }

}