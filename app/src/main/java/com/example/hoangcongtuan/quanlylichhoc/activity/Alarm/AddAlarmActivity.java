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
import com.example.hoangcongtuan.quanlylichhoc.constant.Constant;
import com.example.hoangcongtuan.quanlylichhoc.models.Reminder;
import com.example.hoangcongtuan.quanlylichhoc.utils.ReminderDatabase;
import com.example.hoangcongtuan.quanlylichhoc.utils.ReminderManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddAlarmActivity extends AppCompatActivity implements View.OnClickListener{


    public static final String extraTieuDe = "tieu_de";
    public static final String extraNoiDung = "noi_dung";

    private Calendar calendar;
    private String time;
    private String date;
    private EditText edtTitle, edtContent;
    private TextView tvDate, tvTime;
    private Reminder reminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

        initWidget();
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
        if (intent.hasExtra(extraTieuDe)) {
            String title = intent.getStringExtra(extraTieuDe);
            String content = intent.getStringExtra(extraNoiDung);
            edtTitle.setText(title);
            edtContent.setText(content);
        }
    }

    private void saveReminder() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        date = sdfDate.format(calendar.getTime());

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a");
        time = sdfTime.format(calendar.getTime());

        String reminderTitle = edtTitle.getText().toString();
        String reminderContent = edtContent.getText().toString();

        int mRepeat = 0;
        String mType = "none";
        reminder = new Reminder(reminderTitle, reminderContent, date, time, mRepeat, mType);
        ReminderDatabase.getsInstance(getApplicationContext()).addReminder(reminder);

        ReminderManager.getsInstance(getApplicationContext()).setReminder(reminder.getId(), calendar);
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                calendar.set(calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DATE),
                        hour, minute);
                SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a");
                tvTime.setText(
                        sdfTime.format(calendar.getTime())
                );
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

        timePickerDialog.show();
    }

    private void showDatePickerDialog() {

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(year, month, day);
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd/MM/yyyy");
                tvDate.setText(
                        sdf.format(calendar.getTime())
                );
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));

        datePickerDialog.show();
    }

    public void initWidget() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(
                    getResources().getString(R.string.alarm_add_act_title)
            );
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        edtTitle = findViewById(R.id.edtReminderTitle);
        edtContent = findViewById(R.id.edtReminderContent);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);


        calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd/MM/yyyy");

        date = sdf.format(calendar.getTime());
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a");
        time = sdfTime.format(calendar.getTime());
        tvDate.setText(date);
        tvTime.setText(time);

        tvDate.setOnClickListener(this);
        tvTime.setOnClickListener(this);
    }

}
