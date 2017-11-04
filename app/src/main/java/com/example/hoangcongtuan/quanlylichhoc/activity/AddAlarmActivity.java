package com.example.hoangcongtuan.quanlylichhoc.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.models.Reminder;
import com.example.hoangcongtuan.quanlylichhoc.utils.ReminderDatabase;
import com.example.hoangcongtuan.quanlylichhoc.utils.ReminderManager;

import java.util.Calendar;

public class AddAlarmActivity extends AppCompatActivity implements View.OnClickListener{

    private Calendar mCalendar;
    private String mTitle;
    private String mContent;
    private String mTime;
    private String mDate;
    private int mRepeat;
    private String mType;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private Button btnSetTime, btnSetDate, btnSave, btnCancel;
    private EditText edtTitle, edtContent;
    private TextView tvDate, tvTime;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Alarm");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        getWidgets();
        setWidgets();
        addWidgetsListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        if (intent.hasExtra("tieu_de")) {
            edtTitle.setText(
                    intent.getStringExtra("tieu_de")
            );

            edtContent.setText(
                    intent.getStringExtra("noi_dung")
            );
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void getWidgets() {
        btnSetTime = (Button) findViewById(R.id.btnSetTime);
        btnSetDate = (Button) findViewById(R.id.btnSetDate);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        edtTitle = (EditText) findViewById(R.id.edtReminderTitle);
        edtContent = (EditText) findViewById(R.id.edtReminderContent);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTime = (TextView) findViewById(R.id.tvTime);
    }

    private void setWidgets() {
        mCalendar = Calendar.getInstance();
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendar.get(Calendar.MINUTE);
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);
        mDay = mCalendar.get(Calendar.DATE);

        mDate = mDay + "/" + (mMonth + 1)+ "/" + mYear;
        if (mMinute < 10) {
            mTime = mHour + ":" + "0" + mMinute;
        } else {
            mTime = mHour + ":" + mMinute;
        }

        tvDate.setText(mDate);
        tvTime.setText(mTime);
    }

    private void addWidgetsListener() {
        btnSetDate.setOnClickListener(this);
        btnSetTime.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSetDate:
                showDatePickerDialog();
                break;
            case R.id.btnSetTime:
                showTimePickerDialog();
                break;
            case R.id.btnSave:
                saveReminder();
                break;
            case R.id.btnCancel:
                finish();
                break;
        }
    }

    private void saveReminder() {
        mCalendar.set(Calendar.MONTH, mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendar.set(Calendar.MINUTE, mMinute);

        mTitle = edtTitle.getText().toString();
        mContent = edtContent.getText().toString();

        Reminder reminder = new Reminder(mTitle, mContent, mDate, mTime, mRepeat, mType);
        ReminderDatabase.getsInstance(getApplicationContext()).addReminder(reminder);


        ReminderManager.getsInstance(getApplicationContext()).setReminder(reminder.getId(), mCalendar);

        String s = mDay + "/" + (mMonth +1)+ "/" + mYear + " " + mHour + ":" + mMinute;
        Toast.makeText(this, "Đã thêm nhắc nhở vào lúc " + s, Toast.LENGTH_SHORT).show();

        //Test Alarm detail
        Intent i = new Intent(AddAlarmActivity.this, AlarmActivity.class);
        i.putExtra(ReminderManager.KEY_REMINDER_ID, reminder.getId());
        startActivity(i);
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                mHour = hour;
                mMinute = minute;
                if (minute < 10) {
                    mTime = hour + ":" + "0" + minute;
                } else {
                    mTime = hour + ":" + minute;
                }
                tvTime.setText(mTime);
            }
        }, mHour, mMinute, false);

        timePickerDialog.show();
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                mDay = day;
                mMonth = month;
                mYear = year;
                mDate = day + "/" + (month + 1)+ "/" + year;
                tvDate.setText(mDate);
            }
        }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }
}
