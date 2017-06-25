package com.example.nanorus.todo.view.MainActivity;

import android.content.Context;
import android.content.DialogInterface;
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
import android.view.View;
import android.widget.ImageButton;

import com.example.nanorus.nanojunior.R;
import com.example.nanorus.todo.bus.EventBus;
import com.example.nanorus.todo.bus.event.UpdateNotesListEvent;
import com.example.nanorus.todo.model.DatabaseManager;
import com.example.nanorus.todo.model.pojo.NoteRecyclerPojo;
import com.example.nanorus.todo.presenter.MainActivity.MainActivityRotateSavePojo;
import com.example.nanorus.todo.presenter.MainActivity.MainPresenter;
import com.example.nanorus.todo.utils.PreferenceUse;
import com.example.nanorus.todo.view.NoteEditorActivity.NoteEditorActivity;
import com.example.nanorus.todo.view.ui.adapters.NoPredictiveAnimationsLinearLayoutManager;
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
    RecyclerView mNotesRecyclerView;
    SwipeRefreshLayout mSwipeRefresh;
    private PreferenceUse mPreferences;
    private final String ROTATE_FRAGMENT_TAG = "ROTATE_FRAGMENT";
    List<NoteRecyclerPojo> mNotes;
    LinearLayoutManager mLinearLayoutManager;
    boolean mIsRotated = false;

    ImageButton main_btn_clear_all;
    NotesRecyclerViewAdapter adapter;

    RotateFragment mRotateFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPreferences = new PreferenceUse(getActivity());

        main_btn_clear_all = (ImageButton) findViewById(R.id.main_btn_clear_all);
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mFab = (FloatingActionButton) findViewById(R.id.main_fab_add);
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.main_swipeRefresh);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mNotesRecyclerView = (RecyclerView) findViewById(R.id.main_rv_notesList);


        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mNotesRecyclerView.setLayoutManager(manager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mNotesRecyclerView.getContext(),
                manager.getOrientation());
        mNotesRecyclerView.addItemDecoration(dividerItemDecoration);

        ItemClickSupport.addTo(mNotesRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                goNoteEditorActivity(NoteEditorActivity.INTENT_TYPE_UPDATE, position);
            }
        });
        ItemClickSupport.addTo(mNotesRecyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, final int position, View v) {
                showAlert(getActivity(), "Delete this note?", "Delete is an irreversible action.", "Delete", "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mPresenter.deleteNote(position);
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                );
                return false;
            }
        });

        setListeners();

        EventBus.getBus().register(this);

        FragmentManager fm = getSupportFragmentManager();
        mRotateFragment = (RotateFragment) fm.findFragmentByTag(ROTATE_FRAGMENT_TAG);

        if (mRotateFragment == null) {
            // new screen
            mIsRotated = false;

            mRotateFragment = new RotateFragment();
            fm.beginTransaction().add(mRotateFragment, ROTATE_FRAGMENT_TAG).commit();

            mPresenter = new MainPresenter(getActivity());

        } else {
            // rotated screen
            mIsRotated = true;
            mPresenter = new MainPresenter(getActivity());
        }


        setDrawer();

    }



    @Override
    public void setAdapter(List<NoteRecyclerPojo> data) {
        mNotes = data;
        mLinearLayoutManager = new NoPredictiveAnimationsLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
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

    @Override
    public MainActivity getActivity() {
        return this;
    }

    @Subscribe
    public void updateNotesListListener(UpdateNotesListEvent event) {
        updateNotesList(event.getSortBy());
    }

    @Override
    public void updateNotesList(int sortBy) {
        mPresenter.setNotesList(sortBy);
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

        main_btn_clear_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert(getActivity(), "Clear ALL notes?", "This will remove every note and can't be undone.", "Delete", "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mPresenter.onTouchClearNotes();
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                );
            }
        });

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                EventBus.getBus().post(new UpdateNotesListEvent(mPreferences.loadSortType()));
            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNoteEditorActivity(NoteEditorActivity.INTENT_TYPE_ADD, 0);
            }
        });
    }

    @Override
    public void setSwipeRefreshing(boolean isRefreshing) {
        mSwipeRefresh.setRefreshing(isRefreshing);
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
                .withHeaderBackground(R.mipmap.header)
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
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
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
                    }
                })
                .build();
    }


}

