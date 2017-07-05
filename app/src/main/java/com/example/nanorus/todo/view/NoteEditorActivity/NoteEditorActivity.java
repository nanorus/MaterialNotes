package com.example.nanorus.todo.view.NoteEditorActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.nanorus.nanojunior.R;
import com.example.nanorus.todo.model.pojo.DateTimePojo;
import com.example.nanorus.todo.presenter.NoteEditorPresenter;

public class NoteEditorActivity extends AppCompatActivity implements NoteEditorView.View {
    NoteEditorPresenter mPresenter;
    Toolbar mToolbar;
    ImageButton btn_save;
    ImageButton btn_delete;
    EditText editor_et_noteName;
    TextView mTitle;
    EditText editor_et_dateTime;
    TextView editor_tv_description_length;
    EditText editor_et_description;
    EditText editor_et_priority;

    int mType;
    int mPosition;
    public final static int INTENT_TYPE_UPDATE = 1;
    public final static int INTENT_TYPE_ADD = 2;
    public int mMaxDescriptionSymbolCount = 500;


    int mYear = 2017;
    int mMonth = 5;
    int mDay = 11;
    int mHour = 14;
    int mMinute = 35;

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
        mToolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);

        editor_et_dateTime = (EditText) findViewById(R.id.editor_et_time);
        mTitle = (TextView) findViewById(R.id.note_editor_title);
        editor_tv_description_length = (TextView) findViewById(R.id.editor_tv_description_length);
        editor_et_noteName = (EditText) findViewById(R.id.editor_et_noteName);
        editor_et_description = (EditText) findViewById(R.id.editor_et_description);
        editor_et_priority = (EditText) findViewById(R.id.note_editor_et_priority);
        btn_save = (ImageButton) findViewById(R.id.editor_btn_save);
        btn_delete = (ImageButton) findViewById(R.id.editor_btn_delete);
        switch (mType) {
            case INTENT_TYPE_ADD:
                mTitle.setText(R.string.toolbar_title_add);
                btn_delete.setVisibility(View.INVISIBLE);
                mPresenter.setDateTime(mYear, mMonth, mDay, mHour, mMinute);

                break;

            case INTENT_TYPE_UPDATE:
                mTitle.setText(R.string.toolbar_title_edit);
                mPosition = intent.getIntExtra("position", 0);

                mPresenter.setFields(mPosition);
                break;
        }

        int currentSymbolCount = editor_et_description.getText().length();
        mPresenter.setDescriptionSymbolsLengthText(currentSymbolCount, mMaxDescriptionSymbolCount);

        setListeners();

    }

    @Override
    public DateTimePojo getDateTimePojo() {
        return new DateTimePojo(mYear, mMonth, mDay, mHour, mMinute);
    }

    @Override
    public void setDateTime(String dateTime) {
        editor_et_dateTime.setText(dateTime);
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
    public void setDateTimeVariables(int year, int month, int day, int hour, int minute) {
        mYear = year;
        mMonth = month;
        mDay = day;
        mHour = hour;
        mMinute = minute;
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
    public void setDescriptionSymbolsLengthText(String text) {
        editor_tv_description_length.setText(text);
    }

    @Override
    public NoteEditorActivity getActivity() {
        return this;
    }

    @Override
    public void onBackPressedView() {
        onBackPressed();
    }

    @Override
    public void showToastShot(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        mPresenter.releasePresenter();
        mPresenter = null;
        super.onDestroy();
    }

    void setListeners() {

        // priority
        editor_et_priority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final NumberPicker numberPicker = new NumberPicker(getActivity());
                numberPicker.setMinValue(1);
                numberPicker.setMaxValue(15);
                numberPicker.setValue(Integer.parseInt(editor_et_priority.getText().toString()));
                setPickerDividerColor(numberPicker, getResources().getColor(R.color.accent));

                builder.setTitle("Pick the number of priority");
                builder.setPositiveButton("Pick", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor_et_priority.setText(String.valueOf(numberPicker.getValue()));
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


                builder.setView(numberPicker);

                builder.show();
            }
        });


        // day and time
        editor_et_dateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder dateBilder = new AlertDialog.Builder(getActivity());
                final DatePicker datePicker = new DatePicker(getActivity());
                dateBilder.setView(datePicker);
                dateBilder.setPositiveButton("Set date", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mYear = datePicker.getYear();
                        mMonth = datePicker.getMonth();
                        mDay = datePicker.getDayOfMonth();

                        AlertDialog.Builder timeBuilder = new AlertDialog.Builder(getActivity());
                        final TimePicker timePicker = new TimePicker(getActivity());
                        timePicker.setIs24HourView(true);
                        timeBuilder.setView(timePicker);
                        timeBuilder.setPositiveButton("set time", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    mMinute = timePicker.getMinute();
                                } else {
                                    mMinute = timePicker.getCurrentMinute();
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    mHour = timePicker.getHour();
                                } else {
                                    mHour = timePicker.getCurrentHour();
                                }

                                mPresenter.setDateTime(mYear, mMonth, mDay, mHour, mMinute);

                            }
                        });
                        timeBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        timeBuilder.show();
                    }
                });
                dateBilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dateBilder.show();
            }
        });


        // description
        editor_et_description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int currentSymbolCount = editor_et_description.getText().length();
                mPresenter.setDescriptionSymbolsLengthText(currentSymbolCount, mMaxDescriptionSymbolCount);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        switch (mType) {
            case INTENT_TYPE_ADD:
                // save
                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.onFabClicked(
                                NoteEditorActivity.INTENT_TYPE_ADD,
                                0,
                                editor_et_noteName.getText().toString(),
                                editor_et_description.getText().toString(),
                                editor_et_priority.getText().toString(),
                                getDateTimePojo());

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

                if (!isLandScapeOrientation()) {
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
                } else if (isLandScapeOrientation()) {
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

                }
                break;


            case INTENT_TYPE_UPDATE:

                btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAlert(getActivity(), "Delete this note?", "Delete is an irreversible action.", "Delete", "Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mPresenter.deleteNote(mPosition);
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

                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.onFabClicked(
                                NoteEditorActivity.INTENT_TYPE_UPDATE,
                                mPosition,
                                editor_et_noteName.getText().toString(),
                                editor_et_description.getText().toString(),
                                editor_et_priority.getText().toString(),
                                getDateTimePojo());
                    }
                });
                break;
        }

    }

    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    private boolean isLandScapeOrientation() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }



    private void setPickerDividerColor(NumberPicker picker, int color) {

        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }


}
