package com.example.hoangcongtuan.quanlylichhoc.activity.Alarm;

import android.annotation.SuppressLint;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddAlarmActivity extends AppCompatActivity implements View.OnClickListener{
    private Calendar mCalendar;
    private String mTime;
    private String mDate;
    private EditText edtTitle, edtContent;
    private TextView tvDate, tvTime;
    private Reminder reminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.alarm_add_act_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

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
        edtTitle = findViewById(R.id.edtReminderTitle);
        edtContent = findViewById(R.id.edtReminderContent);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
    }

    private void setWidgets() {
        mCalendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd/MM/yyyy");

        mDate = sdf.format(mCalendar.getTime());
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a");
        mTime = sdfTime.format(mCalendar.getTime());
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
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        mDate = sdfDate.format(mCalendar.getTime());

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a");
        mTime = sdfTime.format(mCalendar.getTime());

        String mTitle = edtTitle.getText().toString();
        String mContent = edtContent.getText().toString();

        int mRepeat = 0;
        String mType = "none";
        reminder = new Reminder(mTitle, mContent, mDate, mTime, mRepeat, mType);
        ReminderDatabase.getsInstance(getApplicationContext()).addReminder(reminder);

        ReminderManager.getsInstance(getApplicationContext()).setReminder(reminder.getId(), mCalendar);
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                mCalendar.set(mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DATE),
                        hour, minute);
                SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a");
                tvTime.setText(
                        sdfTime.format(mCalendar.getTime())
                );
            }
        }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), false);

        timePickerDialog.show();
    }

    private void showDatePickerDialog() {

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                mCalendar.set(year, month, day);
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd/MM/yyyy");
                tvDate.setText(
                        sdf.format(mCalendar.getTime())
                );
            }
        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DATE));

        datePickerDialog.show();
    }
}
