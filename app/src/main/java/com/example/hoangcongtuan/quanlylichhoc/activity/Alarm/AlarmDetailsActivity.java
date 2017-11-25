package com.example.hoangcongtuan.quanlylichhoc.activity.Alarm;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.models.Reminder;
import com.example.hoangcongtuan.quanlylichhoc.utils.ReminderDatabase;
import com.example.hoangcongtuan.quanlylichhoc.utils.ReminderManager;

public class AlarmDetailsActivity extends AppCompatActivity {

    public final static int RC_EDIT = 1;
    private static final String TAG = AlarmDetailsActivity.class.getName();

    private TextView tvDate, tvTime, tvTitle, tvContent;

    private int mReminderId;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alamr_details);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle(getResources().getString(R.string.alarm_detail_act_title));

        getWidgets();

    }


    public void getWidgets() {
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvTitle = (TextView) findViewById(R.id.tvReminderTitle);
        tvContent = (TextView) findViewById(R.id.tvReminderContent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent i = getIntent();
        if (i.hasExtra(ReminderManager.KEY_REMINDER_ID)) {
            mReminderId = i.getIntExtra(ReminderManager.KEY_REMINDER_ID, 0);

            Reminder reminder = ReminderDatabase.getsInstance(getApplicationContext()).getReminder(mReminderId);
            tvTitle.setText(reminder.getTitle());
            tvContent.setText(reminder.getContent());
            tvDate.setText(reminder.getDate());
            tvTime.setText(reminder.getTime());
        }

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
                //backIntent.putExtra("REMINDER_ID", mReminderId);
                setResult(RESULT_OK, backIntent);
                AlarmDetailsActivity.this.finish();

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
                Log.d(TAG, "onOptionsItemSelected: Home");
                finish();
                break;
            case R.id.edit:
                //Test
                Intent editIntent = new Intent(AlarmDetailsActivity.this, EditAlarmActivity.class);
                editIntent.putExtra(ReminderManager.KEY_REMINDER_ID, mReminderId);
                Log.d(TAG, "onOptionsItemSelected: Edit");

                startActivityForResult(editIntent, RC_EDIT);
                break;
            case R.id.delete:
                showDeleteDialog();
                Log.d(TAG, "onOptionsItemSelected: Delete");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteAlarm() {
        ReminderManager.getsInstance(getApplicationContext()).deleteReminder(mReminderId);
        ReminderDatabase.getsInstance(getApplicationContext()).deleteReminder(mReminderId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_EDIT && resultCode == RESULT_OK) {
            if (data.hasExtra(ReminderManager.KEY_REMINDER_ID)) {
                //Update UI proper to new Alarm
                Reminder reminder = ReminderDatabase.getsInstance(getApplicationContext()).getReminder(
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
