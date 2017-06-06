package com.example.nanorus.todo.view.NoteEditorActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nanorus.nanojunior.R;
import com.example.nanorus.todo.presenter.NoteEditorPresenter;

public class NoteEditorActivity extends AppCompatActivity implements NoteEditorView.View {
    NoteEditorPresenter mPresenter;
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    Toolbar mToolbar;
    FloatingActionButton mFab;
    EditText editor_et_noteName;
    TextView mTitle;
    EditText editor_et_description;
    EditText editor_et_priority;
    ImageView note_editor_iv_delete;
    int mType;
    int mPosition;
    public final static int INTENT_TYPE_UPDATE = 1;
    public final static int INTENT_TYPE_ADD = 2;

    boolean isNameTouched = false;
    boolean isDescriptionTouched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        Intent intent = getIntent();
        mType = intent.getIntExtra("type", 2);

        mPresenter = new NoteEditorPresenter(getActivity());
        mToolbar = (Toolbar) findViewById(R.id.note_editor_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.noteEditor_collapsing);
        mTitle = (TextView) findViewById(R.id.note_editor_title);
        editor_et_noteName = (EditText) findViewById(R.id.editor_et_noteName);
        editor_et_description = (EditText) findViewById(R.id.editor_et_description);
        editor_et_priority = (EditText) findViewById(R.id.note_editor_et_priority);
        note_editor_iv_delete = (ImageView) findViewById(R.id.note_editor_iv_delete);
        mFab = (FloatingActionButton) findViewById(R.id.editor_fab_go);

        switch (mType) {
            case INTENT_TYPE_ADD:
                mTitle.setText("Add note");
                // mCollapsingToolbarLayout.setTitle("Add note");
                note_editor_iv_delete.setVisibility(View.INVISIBLE);

                break;
            case INTENT_TYPE_UPDATE:
                mTitle.setText("Edit note");
                // mCollapsingToolbarLayout.setTitle("Edit note");
                mPosition = intent.getIntExtra("position", 0);

                mPresenter.setFields(mPosition);
                break;
        }

        setListeners();
    }

    @Override
    public void setName(String name) {
        editor_et_noteName.setText(name);
    }

    @Override
    public void setPriority(String priority) {
        editor_et_priority.setText(priority);
    }

    @Override
    public void setDescription(String description) {
        editor_et_description.setText(description);
    }

    @Override
    public NoteEditorActivity getActivity() {
        return this;
    }

    @Override
    protected void onDestroy() {
        mPresenter.releasePresenter();

        mPresenter = null;
        super.onDestroy();
    }

    void setListeners() {

        switch (mType) {
            case INTENT_TYPE_ADD:


                mFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.onFabClicked(
                                NoteEditorActivity.INTENT_TYPE_ADD,
                                0,
                                editor_et_noteName.getText().toString(),
                                editor_et_description.getText().toString(),
                                editor_et_priority.getText().toString(),
                                null);
                    }
                });

                editor_et_noteName.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (!isNameTouched) {
                            editor_et_noteName.setText("");
                            isNameTouched = true;
                        }
                        return false;
                    }
                });
                editor_et_description.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (!isDescriptionTouched) {
                            editor_et_description.setText("");
                            isDescriptionTouched = true;
                        }
                        return false;
                    }
                });
                break;


            case INTENT_TYPE_UPDATE:
                note_editor_iv_delete.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        CharSequence[] items = {"Yes", "No"};
                        final int DIALOG_YES = 0;
                        final int DIALOG_NO = 1;

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                        builder.setTitle("Delete this note?");
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DIALOG_YES:
                                        mPresenter.deleteNote(mPosition);
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
                mFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.onFabClicked(
                                NoteEditorActivity.INTENT_TYPE_UPDATE,
                                mPosition,
                                editor_et_noteName.getText().toString(),
                                editor_et_description.getText().toString(),
                                editor_et_priority.getText().toString(),
                                null);
                    }
                });
                break;
        }
    }

}
