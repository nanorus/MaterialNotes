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
import com.example.nanorus.todo.model.database.DatabaseUse;
import com.example.nanorus.todo.model.pojo.NoteRecyclerPojo;
import com.example.nanorus.todo.presenter.MainPresenter;
import com.example.nanorus.todo.utils.PreferenceUse;
import com.example.nanorus.todo.view.NoteEditorActivity.NoteEditorActivity;
import com.example.nanorus.todo.view.ui.adapters.NotesRecyclerViewAdapter;
import com.example.nanorus.todo.view.ui.recyclerView.ItemClickSupport;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainView.View {
    MainPresenter mPresenter;
    Toolbar mToolbar;
    FloatingActionButton mFab;
    RecyclerView mNotesList;
    SwipeRefreshLayout mSwipeRefresh;
    ImageView tb_clear;
    ImageView list_item_note_iv_priority_color;
    private PreferenceUse mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPreferences = new PreferenceUse(getActivity());

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
                goNoteEditorActivity(NoteEditorActivity.INTENT_TYPE_UPDATE, position);
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
        EventBus.getBus().post(new UpdateNotesListEvent(mPreferences.loadSortType()));

        setListeners();
        setDrawer();

    }


    @Override
    public MainActivity getActivity() {
        return this;
    }

    @Override
    public void updateNotesList(int sortBy) {

        List<NoteRecyclerPojo> list = mPresenter.getAllNotesRecyclerPojo(sortBy);
        NotesRecyclerViewAdapter adapter = new NotesRecyclerViewAdapter(list);
        mNotesList.setAdapter(adapter);
    }

    @Subscribe
    public void updateNotesListListener(UpdateNotesListEvent event) {
        updateNotesList(event.getSortBy());
    }

    @Override
    protected void onDestroy() {
        mPresenter.releasePresenter();
        EventBus.getBus().unregister(this);
        mPresenter = null;
        super.onDestroy();
    }


    private void setListeners() {
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
                EventBus.getBus().post(new UpdateNotesListEvent(mPreferences.loadSortType()));
                mSwipeRefresh.setRefreshing(false);
            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNoteEditorActivity(NoteEditorActivity.INTENT_TYPE_ADD, 0);
            }
        });
    }

    private void goNoteEditorActivity(int type, int position) {
        Intent intent = new Intent(getActivity(), NoteEditorActivity.class);
        intent.putExtra("type", type);
        if (type == NoteEditorActivity.INTENT_TYPE_UPDATE) {
            intent.putExtra("position", position);
            intent.putExtra("sortType", mPreferences.loadSortType());
        }
        startActivity(intent);
    }

    private void setDrawer() {

        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                //.addProfiles(
                //        new ProfileDrawerItem().withName("Mike Penz").withEmail("mikepenz@gmail.com").withIcon(getResources().getDrawable(R.drawable.profile))
                //)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();


        ArrayList<IDrawerItem> items = new ArrayList<>();
        items.add(new PrimaryDrawerItem().withIdentifier(21).withName("Date of creating"));
        items.add(new PrimaryDrawerItem().withIdentifier(22).withName("Name"));
        items.add(new PrimaryDrawerItem().withIdentifier(23).withName("Priority"));
        items.add(new PrimaryDrawerItem().withIdentifier(24).withName("Date and time"));

        PrimaryDrawerItem addNote = new PrimaryDrawerItem().withIdentifier(1).withName("Add note");
        SecondaryDrawerItem sort = new SecondaryDrawerItem().withIdentifier(2).withName("Sort by").withSubItems(items);

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        addNote,
                        new DividerDrawerItem(),
                        sort
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        switch ((int) drawerItem.getIdentifier()) {
                            case 1:
                                goNoteEditorActivity(NoteEditorActivity.INTENT_TYPE_ADD, 0);
                                break;
                            case 21:
                                mPreferences.saveSortType(DatabaseUse.SORT_BY_DATE_CREATING);
                                EventBus.getBus().post(new UpdateNotesListEvent(DatabaseUse.SORT_BY_DATE_CREATING));
                                break;
                            case 22:
                                mPreferences.saveSortType(DatabaseUse.SORT_BY_NAME);
                                EventBus.getBus().post(new UpdateNotesListEvent(DatabaseUse.SORT_BY_NAME));
                                break;
                            case 23:
                                mPreferences.saveSortType(DatabaseUse.SORT_BY_PRIORITY);
                                EventBus.getBus().post(new UpdateNotesListEvent(DatabaseUse.SORT_BY_PRIORITY));
                                break;
                            case 24:
                                mPreferences.saveSortType(DatabaseUse.SORT_BY_DATE_TIME);
                                EventBus.getBus().post(new UpdateNotesListEvent(DatabaseUse.SORT_BY_DATE_TIME));
                                break;
                        }

                        return false;
                    }
                })
                .build();
    }

}