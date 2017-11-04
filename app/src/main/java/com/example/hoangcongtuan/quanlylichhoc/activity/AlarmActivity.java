package com.example.hoangcongtuan.quanlylichhoc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.adapter.ReminderAdapter;
import com.example.hoangcongtuan.quanlylichhoc.models.Reminder;
import com.example.hoangcongtuan.quanlylichhoc.utils.ReminderDatabase;
import com.example.hoangcongtuan.quanlylichhoc.utils.ReminderManager;

import java.util.ArrayList;

public class AlarmActivity extends AppCompatActivity implements ReminderAdapter.ItemClickListener {

    private final static int RC_DETAIL = 0;
    private ArrayList<Reminder> mReminders;
    private RecyclerView mRecyclerView;
    private ReminderAdapter mAdapter;

    private Button btnAddAlarm;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.rvAlarms);

        mReminders = ReminderDatabase.getsInstance(getApplicationContext()).getAllReminders();
        mAdapter = new ReminderAdapter(this, mReminders);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setClickListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        btnAddAlarm = findViewById(R.id.btnAddAlarm);
        btnAddAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AlarmActivity.this, AddAlarmActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view, int position, boolean isLongClick) {
        Reminder reminder = mReminders.get(position);
        if(isLongClick) {
            ReminderDatabase.getsInstance(getApplicationContext()).deleteReminder(reminder.getId());
            ReminderManager.getsInstance(getApplicationContext()).deleteReminder(reminder.getId());
            mReminders.remove(position);
            mAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Đã xoá nhắc nhở", Toast.LENGTH_SHORT).show();
        } else {
            Intent i = new Intent(AlarmActivity.this, AlamrDetailsActivity.class);
            i.putExtra(ReminderManager.KEY_REMINDER_ID, reminder.getId());
            //startActivityForResult(i, RC_DETAIL);
            startActivity(i);
        }

    }
}
