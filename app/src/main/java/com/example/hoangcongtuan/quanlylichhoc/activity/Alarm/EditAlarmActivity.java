package com.example.hoangcongtuan.quanlylichhoc.activity.Alarm;

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

public class EditAlarmActivity extends AppCompatActivity implements View.OnClickListener{

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
    private Reminder reminder;
    private int remiderId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Alarm");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        init();
        getWidgets();
        setWidgets();
        addWidgetsListener();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void init() {
        mCalendar = Calendar.getInstance();
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

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        if (intent.hasExtra(ReminderManager.KEY_REMINDER_ID)) {
            remiderId = intent.getIntExtra(ReminderManager.KEY_REMINDER_ID, 0);
            if (remiderId != 0) {
                reminder = ReminderDatabase.getsInstance(getApplicationContext()).getReminder(remiderId);
                //show remider to UI
                edtTitle.setText(reminder.getTitle());
                edtContent.setText(reminder.getContent());

                tvDate.setText(reminder.getDate());
                tvTime.setText(reminder.getTime());

                mCalendar = ReminderManager.getsInstance(this).toCalendar(reminder.getDate(), reminder.getTime());
                mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
                mMinute = mCalendar.get(Calendar.MINUTE);
                mYear = mCalendar.get(Calendar.YEAR);
                mMonth = mCalendar.get(Calendar.MONTH);
                mDay = mCalendar.get(Calendar.DATE);
            }
            else{
                errorOccurReturn();
                Toast.makeText(this, "Khong tim thay Alarm!", Toast.LENGTH_LONG).show();
            }


        }
        else {
            errorOccurReturn();
            Toast.makeText(this, "Khong tim thay Alarm!", Toast.LENGTH_LONG).show();
        }

    }

    public void errorOccurReturn() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
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
                updateRemider();
                Intent intent = new Intent();
                intent.putExtra(ReminderManager.KEY_REMINDER_ID, reminder.getId());
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.btnCancel:
                finish();
                break;
        }
    }

    private void updateRemider() {
        mCalendar.set(Calendar.MONTH, mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendar.set(Calendar.MINUTE, mMinute);
        mCalendar.set(Calendar.SECOND, 0);

        mTitle = edtTitle.getText().toString();
        mContent = edtContent.getText().toString();

        reminder = new Reminder(mTitle, mContent, mDate, mTime, mRepeat, mType);
        reminder.setId(remiderId);
        ReminderDatabase.getsInstance(getApplicationContext()).updateReminder(reminder);

        ReminderManager.getsInstance(getApplicationContext()).setReminder(reminder.getId(), mCalendar);

        String s = mDay + "/" + (mMonth +1)+ "/" + mYear + " " + mHour + ":" + mMinute;
        Toast.makeText(this, "Đã cap nhat nhắc nhở vào lúc " + s, Toast.LENGTH_SHORT).show();

    }

    private void showTimePickerDialog() {
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendar.get(Calendar.MINUTE);

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
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);
        mDay = mCalendar.get(Calendar.DATE);

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
