package com.example.hoangcongtuan.quanlylichhoc.activity.Alarm;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

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
    private EditText edtTitle, edtContent;
    private TextView tvDate, tvTime;
    private Toolbar toolbar;
    private Reminder reminder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.alarm_add_act_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        getWidgets();
        setWidgets();
        addWidgetsListener();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_add_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_save:
                saveReminder();
                Intent intent = new Intent();
                intent.putExtra(ReminderManager.KEY_REMINDER_ID, reminder.getId());
                setResult(RESULT_OK, intent);
                finish();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getWidgets() {
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
        tvDate.setOnClickListener(this);
        tvTime.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvDate:
                showDatePickerDialog();
                break;
            case R.id.tvTime:
                showTimePickerDialog();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        if (intent.hasExtra("tieu_de")) {
            String title = intent.getStringExtra("tieu_de");
            String content = intent.getStringExtra("noi_dung");
            edtTitle.setText(title);
            edtContent.setText(content);
        }
    }

    private void saveReminder() {
        mCalendar.set(Calendar.MONTH, mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendar.set(Calendar.MINUTE, mMinute);
        mCalendar.set(Calendar.SECOND, 0);

        mDate = mDay + "/" + (mMonth + 1)+ "/" + mYear;
        if (mMinute < 10) {
            mTime = mHour + ":" + "0" + mMinute;
        } else {
            mTime = mHour + ":" + mMinute;
        }

        mTitle = edtTitle.getText().toString();
        mContent = edtContent.getText().toString();

        reminder = new Reminder(mTitle, mContent, mDate, mTime, mRepeat, mType);
        ReminderDatabase.getsInstance(getApplicationContext()).addReminder(reminder);


        ReminderManager.getsInstance(getApplicationContext()).setReminder(reminder.getId(), mCalendar);


    }

    private void showTimePickerDialog() {
        Calendar calendar;
        calendar = Calendar.getInstance();
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
        Calendar calendar;
        calendar = Calendar.getInstance();
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
