package com.example.hoangcongtuan.quanlylichhoc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.models.Reminder;
import com.example.hoangcongtuan.quanlylichhoc.utils.ReminderDatabase;
import com.example.hoangcongtuan.quanlylichhoc.utils.ReminderManager;

public class AlamrDetailsActivity extends AppCompatActivity {

    public final static int RC_EDIT = 1;

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

        getSupportActionBar().setTitle("Chi tiết nhắc nhở");

        getWidgets();
        Intent i = getIntent();
        mReminderId = i.getIntExtra(ReminderManager.KEY_REMINDER_ID, 0);

        Reminder reminder = ReminderDatabase.getsInstance(getApplicationContext()).getReminder(mReminderId);
        tvTitle.setText(reminder.getTitle());
        tvContent.setText(reminder.getContent());
        tvDate.setText(reminder.getDate());
        tvTime.setText(reminder.getTime());
    }


    public void getWidgets() {
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvTitle = (TextView) findViewById(R.id.tvReminderTitle);
        tvContent = (TextView) findViewById(R.id.tvReminderContent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
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
                Intent editIntent = new Intent(AlamrDetailsActivity.this, AddAlarmActivity.class);
                editIntent.putExtra(ReminderManager.KEY_REMINDER_ID, mReminderId);

                startActivityForResult(editIntent, RC_EDIT);
                //startActivity(editIntent);
                break;
            case R.id.delete:
                deleteAlarm();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteAlarm() {
        ReminderManager.getsInstance(getApplicationContext()).deleteReminder(mReminderId);
        ReminderDatabase.getsInstance(getApplicationContext()).deleteReminder(mReminderId);
        Toast.makeText(this, "Đã huỷ nhắc nhở", Toast.LENGTH_SHORT).show();

        finish();
    }


}
