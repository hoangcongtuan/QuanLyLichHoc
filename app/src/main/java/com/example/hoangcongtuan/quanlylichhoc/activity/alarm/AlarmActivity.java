package com.example.hoangcongtuan.quanlylichhoc.activity.alarm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.activity.base.BaseActivity;
import com.example.hoangcongtuan.quanlylichhoc.adapter.ReminderAdapter;
import com.example.hoangcongtuan.quanlylichhoc.helper.RecyclerItemTouchHelper;
import com.example.hoangcongtuan.quanlylichhoc.models.Reminder;
import com.example.hoangcongtuan.quanlylichhoc.utils.CircularAnimUtil;
import com.example.hoangcongtuan.quanlylichhoc.helper.ReminderDBHelper;
import com.example.hoangcongtuan.quanlylichhoc.utils.ReminderManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AlarmActivity extends BaseActivity implements ReminderAdapter.ItemClickListener,
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    private final static int RC_DETAIL = 0;
    private final static int RC_ADD = 1;
    private static final String TAG = AlarmActivity.class.getName();

    private ReminderAdapter reminderAdapter;
    private CoordinatorLayout alarmLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        initWidget();
    }

    public void initWidget() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.alarm_act_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        alarmLayout = findViewById(R.id.alarm_layout);
        RecyclerView rvAlarm = findViewById(R.id.rvAlarms);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(
                0, ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvAlarm);

        ArrayList<Reminder> listReminders = ReminderDBHelper.getsInstance(getApplicationContext()).getAllReminders();
        reminderAdapter = new ReminderAdapter(this, listReminders);
        rvAlarm.setAdapter(reminderAdapter);
        reminderAdapter.setClickListener(this);

        rvAlarm.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvAlarm.getContext(),
                ((LinearLayoutManager)rvAlarm.getLayoutManager()).getOrientation());
        rvAlarm.addItemDecoration(dividerItemDecoration);

        final FloatingActionButton btnAddAlarm = findViewById(R.id.btnAdd);
        btnAddAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AlarmActivity.this, AddAlarmActivity.class);
                CircularAnimUtil.startActivityForResult(AlarmActivity.this, i, RC_ADD, btnAddAlarm, R.color.colorPrimary);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view, int position, boolean isLongClick) {
        Reminder reminder = reminderAdapter.getReminder(position);
        Intent i = new Intent(AlarmActivity.this, AlarmDetailActivity.class);
        i.putExtra(ReminderManager.KEY_REMINDER_ID, reminder.getId());
        startActivityForResult(i, RC_DETAIL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_DETAIL || requestCode == RC_ADD) {
            //update alarm list
            reminderAdapter.removeAllReminder();
            ArrayList<Reminder> lstReminder = ReminderDBHelper.getsInstance(getApplicationContext()).getAllReminders();
            for(Reminder r : lstReminder)
                reminderAdapter.addReminder(r);

            reminderAdapter.notifyDataSetChanged();
            if (requestCode == RC_ADD && resultCode == RESULT_OK)
                Snackbar.make(alarmLayout, getResources().getString(R.string.add_alarm_success), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof ReminderAdapter.ViewHolder) {
            final Reminder deleteReminder = reminderAdapter.getReminder(position);
            final int deletePosition = position;

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.US);
            final Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(
                        sdf.parse(deleteReminder.getDate())
                );
            } catch (ParseException e) {
                Log.d(TAG, "onSwiped: Error when parse date time from reminder");
                Crashlytics.log(Log.ERROR, TAG, "onSwiped: Error when parse date time from reminder");
                e.printStackTrace();
            }

            ReminderDBHelper.getsInstance(getApplicationContext()).deleteReminder(deleteReminder.getId());
            ReminderManager.getsInstance(getApplicationContext()).deleteReminder(deleteReminder.getId());
            reminderAdapter.removeReminder(viewHolder.getAdapterPosition());

            Snackbar.make(alarmLayout, getResources().getString(R.string.remove_alarm_success), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.undo), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            reminderAdapter.restoreReminder(deleteReminder, deletePosition);
                            ReminderDBHelper.getsInstance(getApplicationContext()).addReminder(deleteReminder);
                            ReminderManager.getsInstance(getApplicationContext()).setReminder(deleteReminder.getId(), calendar);
                        }
                    })
                    .show();
        }
    }
}