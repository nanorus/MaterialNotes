package com.example.nanorus.todo.view.MainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.squareup.otto.Bus;
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


    private static Bus sBus;



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

        sBus = EventBus.getBus();
        sBus.register(this);

        sBus.post(new UpdateNotesListEvent());
        setListeners();

        // add Priority (1,2,3+)

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
    public void updateNotesListListener(UpdateNotesListEvent event){
        updateNotesList();
    }

    @Override
    protected void onDestroy() {
        mPresenter.releasePresenter();
        mPresenter = null;
        super.onDestroy();
    }

    void setListeners() {
        tb_clear.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mPresenter.onTouchClearNotes();

                return false;
            }
        });

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sBus.post(new UpdateNotesListEvent());
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