package com.example.nanorus.todo.view.main_activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;

import com.example.nanorus.nanojunior.R;
import com.example.nanorus.todo.bus.EventBus;
import com.example.nanorus.todo.bus.event.UpdateNotesListEvent;
import com.example.nanorus.todo.model.DatabaseManager;
import com.example.nanorus.todo.model.pojo.MainActivityRotateSavePojo;
import com.example.nanorus.todo.model.pojo.NoteRecyclerPojo;
import com.example.nanorus.todo.presenter.MainPresenter;
import com.example.nanorus.todo.utils.PreferenceUse;
import com.example.nanorus.todo.view.note_editor_activity.NoteEditorActivity;
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
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IMainActivity {
    private int mSortType;
    private int mPosition;
    private MainPresenter mPresenter;
    private Toolbar mToolbar;
    private FloatingActionButton mFab;
    private RecyclerView mNotesRecyclerView;
    private SwipeRefreshLayout mSwipeRefresh;
    private PreferenceUse mPreferences;
    private final String ROTATE_FRAGMENT_TAG = "ROTATE_FRAGMENT";
    private List<NoteRecyclerPojo> mNotes;
    private LinearLayoutManager mLinearLayoutManager;
    private boolean mIsRotated = false;

    private ImageButton main_btn_clear_all;
    private NotesRecyclerViewAdapter adapter;

    private RotateFragment mRotateFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPreferences = new PreferenceUse(getContext());

        // views
        main_btn_clear_all = (ImageButton) findViewById(R.id.main_btn_clear_all);
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mFab = (FloatingActionButton) findViewById(R.id.main_fab_add);
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.main_swipeRefresh);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // recyclerView
        mNotesRecyclerView = (RecyclerView) findViewById(R.id.main_rv_notesList);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mNotesRecyclerView.setLayoutManager(manager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mNotesRecyclerView.getContext(),
                manager.getOrientation());
        mNotesRecyclerView.addItemDecoration(dividerItemDecoration);

        // recyclerView listener
        ItemClickSupport.addTo(mNotesRecyclerView).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    setPosition(position);
                    goNoteEditorActivity(NoteEditorActivity.INTENT_TYPE_UPDATE, position);
                });
        ItemClickSupport.addTo(mNotesRecyclerView).setOnItemLongClickListener((recyclerView, position, v) -> {
            setPosition(position);
            showAlert(getContext(), "Delete this note?", "This action cannot be undone.", "Delete", "Cancel",
                    (dialog, which) -> mPresenter.deleteNote(),
                    (dialog, which) -> dialog.dismiss()
            );
            return false;
        });


        // register all listeners
        setListeners();
        // register bus
        EventBus.getBus().register(this);

        // checking for rotation to save/load data
        FragmentManager fm = getSupportFragmentManager();
        mRotateFragment = (RotateFragment) fm.findFragmentByTag(ROTATE_FRAGMENT_TAG);
        if (mRotateFragment == null) {
            // new screen
            mIsRotated = false;
            mRotateFragment = new RotateFragment();
            fm.beginTransaction().add(mRotateFragment, ROTATE_FRAGMENT_TAG).commit();
        } else {
            // rotated screen
            mIsRotated = true;
        }

        // creating presenter
        mPresenter = new MainPresenter(getView());

        // navigation drawer
        setDrawer();
    }


    @Override
    public int getPosition() {
        return mPosition;
    }

    @Override
    public void setPosition(int position) {
        mPosition = position;
    }


    @Override
    public int getSortType() {
        return mPreferences.loadSortType();
    }

    @Override
    public void setSortType(int sortType) {
        mPreferences.saveSortType(sortType);
    }


    @Override
    public void setAdapter(List<NoteRecyclerPojo> data) {
        mNotes = data;
        mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        adapter = new NotesRecyclerViewAdapter(mNotes);
        mNotesRecyclerView.setLayoutManager(mLinearLayoutManager);
        mNotesRecyclerView.setAdapter(adapter);
    }

    @Override
    public void updateAdapter(List<NoteRecyclerPojo> data) {
        mNotes = data;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showAlert(Context context, String title, String message, String buttonPositiveTitle, String buttonNegativeTitle, AlertDialog.OnClickListener positiveOnClickListener, AlertDialog.OnClickListener negativeOnClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if (title != null)
            builder.setTitle(title);
        if (message != null)
            builder.setMessage(message);

        builder.setPositiveButton(buttonPositiveTitle, positiveOnClickListener);
        builder.setNegativeButton(buttonNegativeTitle, negativeOnClickListener);
        builder.show();

    }


    @Subscribe
    public void updateNotesListListener(UpdateNotesListEvent event) {
        updateNotesList(event.getSortBy());
    }

    @Override
    public void updateNotesList(int sortBy) {
        mSortType = sortBy;
        mPresenter.setNotesList();
    }


    @Override
    public void saveRotateData(MainActivityRotateSavePojo data) {
        mRotateFragment.setSavePojo(data);
    }

    @Override
    public MainActivityRotateSavePojo loadRotateData() {
        return mRotateFragment.getSavePojo();
    }

    @Override
    public boolean isSwipeRefreshing() {
        return mSwipeRefresh.isRefreshing();
    }

    @Override
    public boolean isRotated() {
        return mIsRotated;
    }

    @Override
    public IMainActivity getView() {
        return this;
    }

    @Override
    public Activity getContext() {
        return this;
    }


    @Override
    protected void onDestroy() {
        // save
        mPresenter.saveRotateData();

        // release
        mPresenter.releasePresenter();
        EventBus.getBus().unregister(this);
        mPresenter = null;
        super.onDestroy();
    }


    private void setListeners() {
        main_btn_clear_all.setOnClickListener(v -> showAlert(getContext(), "Clear ALL notes?", "This will remove every note and can't be undone.", "Delete", "Cancel",
                (dialog, which) -> mPresenter.onTouchClearNotes(),
                (dialog, which) -> dialog.dismiss()
        ));
        mSwipeRefresh.setOnRefreshListener(() -> EventBus.getBus().post(new UpdateNotesListEvent(mPreferences.loadSortType())));
        mFab.setOnClickListener(v -> goNoteEditorActivity(NoteEditorActivity.INTENT_TYPE_ADD, 0));
    }

    @Override
    public void setSwipeRefreshing(boolean isRefreshing) {
        mSwipeRefresh.setRefreshing(isRefreshing);
    }

    private void goNoteEditorActivity(int type, int position) {
        Intent intent = new Intent(getContext(), NoteEditorActivity.class);
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
                .withHeaderBackground(R.mipmap.header)
                //.addProfiles(
                //        new ProfileDrawerItem().withName("Mike Penz").withEmail("mikepenz@gmail.com").withIcon(getResources().getDrawable(R.drawable.profile))
                //)
                .withOnAccountHeaderListener((view, profile, currentProfile) -> false)
                .build();


        ArrayList<IDrawerItem> items = new ArrayList<>();
        items.add(new PrimaryDrawerItem().withIdentifier(21).withName("Created"));
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
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    // do something with the clicked item :D
                    switch ((int) drawerItem.getIdentifier()) {
                        case 1:
                            goNoteEditorActivity(NoteEditorActivity.INTENT_TYPE_ADD, 0);
                            break;
                        case 21:
                            mPreferences.saveSortType(DatabaseManager.SORT_BY_DATE_CREATING);
                            EventBus.getBus().post(new UpdateNotesListEvent(DatabaseManager.SORT_BY_DATE_CREATING));
                            break;
                        case 22:
                            mPreferences.saveSortType(DatabaseManager.SORT_BY_NAME);
                            EventBus.getBus().post(new UpdateNotesListEvent(DatabaseManager.SORT_BY_NAME));
                            break;
                        case 23:
                            mPreferences.saveSortType(DatabaseManager.SORT_BY_PRIORITY);
                            EventBus.getBus().post(new UpdateNotesListEvent(DatabaseManager.SORT_BY_PRIORITY));
                            break;
                        case 24:
                            mPreferences.saveSortType(DatabaseManager.SORT_BY_DATE_TIME);
                            EventBus.getBus().post(new UpdateNotesListEvent(DatabaseManager.SORT_BY_DATE_TIME));
                            break;
                    }

                    return false;
                })
                .build();
    }


}

