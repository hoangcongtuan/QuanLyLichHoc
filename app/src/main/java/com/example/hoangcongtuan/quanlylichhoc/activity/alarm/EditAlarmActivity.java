package com.example.hoangcongtuan.quanlylichhoc.activity.alarm;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.models.Reminder;
import com.example.hoangcongtuan.quanlylichhoc.utils.ReminderDBHelper;
import com.example.hoangcongtuan.quanlylichhoc.utils.ReminderManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EditAlarmActivity extends AppCompatActivity implements View.OnClickListener{

    private Calendar mCalendar;
    private EditText edtTitle, edtContent;
    private TextView tvDate, tvTime;
    private Reminder reminder;
    private int remiderId;

    public EditAlarmActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);

        init();
        initWidget();
    }


    private void init() {
        mCalendar = Calendar.getInstance();
    }

    public void initWidget() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.alarm_edit_act_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        edtTitle = findViewById(R.id.edtReminderTitle);
        edtContent = findViewById(R.id.edtReminderContent);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);

        tvDate.setOnClickListener(this);
        tvTime.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        if (intent.hasExtra(ReminderManager.KEY_REMINDER_ID)) {
            remiderId = intent.getIntExtra(ReminderManager.KEY_REMINDER_ID, 0);
            if (remiderId != 0) {
                reminder = ReminderDBHelper.getsInstance(getApplicationContext()).getReminder(remiderId);
                //show remider to UI
                edtTitle.setText(reminder.getTitle());
                edtContent.setText(reminder.getContent());

                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
                try {
                    mCalendar.setTime(
                            sdf.parse(reminder.getDate())
                    );

                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat sdfDate = new SimpleDateFormat("EEEE dd/MM/yyyy");

                    tvDate.setText(
                            sdfDate.format(mCalendar.getTime())
                    );
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a");

                    tvTime.setText(
                            sdfTime.format(mCalendar.getTime())
                    );

                } catch (ParseException e) {
                    e.printStackTrace();
                    errorOccurReturn();
                    Toast.makeText(this,
                            getResources().getString(R.string.error_alarm_database_access), Toast.LENGTH_LONG).show();
                }


            }
            else{
                errorOccurReturn();
                Toast.makeText(this,
                        getResources().getString(R.string.error_alarm_database_access), Toast.LENGTH_LONG).show();
            }


        }
        else {
            errorOccurReturn();
            Toast.makeText(this,
                    getResources().getString(R.string.error_alarm_database_access), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_add_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_save:
                updateRemider();
                Intent intent = new Intent();
                intent.putExtra(ReminderManager.KEY_REMINDER_ID, reminder.getId());
                setResult(RESULT_OK, intent);
                finish();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void errorOccurReturn() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
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

    private void updateRemider() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        String mDate = sdfDate.format(mCalendar.getTime());

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a");
        String mTime = sdfTime.format(mCalendar.getTime());
        String mTitle = edtTitle.getText().toString();
        String mContent = edtContent.getText().toString();
        int mRepeat = 0;
        String mType = "none";
        reminder = new Reminder(mTitle, mContent, mDate, mTime, mRepeat, mType);
        reminder.setId(remiderId);
        ReminderDBHelper.getsInstance(getApplicationContext()).updateReminder(reminder);
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
                @SuppressLint("SimpleDateFormat")
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
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd/MM/yyyy");
                tvDate.setText(
                        sdf.format(mCalendar.getTime())
                );
            }
        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DATE));

        datePickerDialog.show();
    }
}
