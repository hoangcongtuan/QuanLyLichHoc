package com.example.hoangcongtuan.quanlylichhoc.activity.alarm;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.activity.base.BaseActivity;
import com.example.hoangcongtuan.quanlylichhoc.models.Reminder;
import com.example.hoangcongtuan.quanlylichhoc.utils.ReminderDBHelper;
import com.example.hoangcongtuan.quanlylichhoc.utils.ReminderManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AlarmDetailActivity extends BaseActivity {

    public final static int RC_EDIT = 1;
    private static final String TAG = AlarmDetailActivity.class.getName();

    private TextView tvDate, tvTime, tvTitle, tvContent;
    private int reminderId;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_details);
        init();
        initWidget();
        //get reminder
        Intent i = getIntent();
        if (i.hasExtra(ReminderManager.KEY_REMINDER_ID)) {
            reminderId = i.getIntExtra(ReminderManager.KEY_REMINDER_ID, 0);

            Reminder reminder = ReminderDBHelper.getsInstance(getApplicationContext()).getReminder(reminderId);
            tvTitle.setText(reminder.getTitle());
            tvContent.setText(reminder.getContent());
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.US);
            try {
                calendar.setTime(
                        sdf.parse(reminder.getDate())
                );

                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat sdfDate = new SimpleDateFormat("EEEE dd/MM/yyyy", Locale.US);

                tvDate.setText(
                        sdfDate.format(calendar.getTime())
                );
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a", Locale.US);

                tvTime.setText(
                        sdfTime.format(calendar.getTime())
                );

            } catch (ParseException e) {
                Crashlytics.log(Log.ERROR, TAG, "Error when parse date time from reminder");
                e.printStackTrace();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getResources().getString(R.string.error_title));
                builder.setMessage(
                        getResources().getString(R.string.error_parse_date_time)
                );

                builder.setPositiveButton(
                        getResources().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }
                );

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
    }

    public void init() {
        calendar = Calendar.getInstance();
    }

    public void initWidget() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.alarm_detail_act_title));
        }

        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        tvTitle = findViewById(R.id.tvReminderTitle);
        tvContent = findViewById(R.id.tvReminderContent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alarm_detail, menu);
        return true;
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.xoa_nhac_nho));
        builder.setMessage(getResources().getString(R.string.xoa_nhac_nho_detail));
        builder.setPositiveButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //xoa
                deleteAlarm();
                //put data back
                Intent backIntent = new Intent();
                //backIntent.putExtra("REMINDER_ID", reminderId);
                setResult(RESULT_OK, backIntent);
                AlarmDetailActivity.this.finish();

            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //ko xoa
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.edit:
                //Test
                Intent editIntent = new Intent(AlarmDetailActivity.this, EditAlarmActivity.class);
                editIntent.putExtra(ReminderManager.KEY_REMINDER_ID, reminderId);

                startActivityForResult(editIntent, RC_EDIT);
                break;
            case R.id.delete:
                showDeleteDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAlarm() {
        ReminderManager.getsInstance(getApplicationContext()).deleteReminder(reminderId);
        ReminderDBHelper.getsInstance(getApplicationContext()).deleteReminder(reminderId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_EDIT && resultCode == RESULT_OK) {
            if (data.hasExtra(ReminderManager.KEY_REMINDER_ID)) {
                //Update UI proper to new Alarm
                Reminder reminder = ReminderDBHelper.getsInstance(getApplicationContext()).getReminder(
                        data.getIntExtra(ReminderManager.KEY_REMINDER_ID, 0)
                );
                tvTitle.setText(reminder.getTitle());
                tvContent.setText(reminder.getContent());

                tvDate.setText(reminder.getDate());
                tvTime.setText(reminder.getTime());
            }
        }
    }
}
