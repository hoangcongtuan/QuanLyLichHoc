package com.example.hoangcongtuan.quanlylichhoc.activity.Alarm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.adapter.ReminderAdapter;
import com.example.hoangcongtuan.quanlylichhoc.helper.RecyclerItemTouchHelper;
import com.example.hoangcongtuan.quanlylichhoc.models.Reminder;
import com.example.hoangcongtuan.quanlylichhoc.utils.CircularAnimUtil;
import com.example.hoangcongtuan.quanlylichhoc.utils.ReminderDatabase;
import com.example.hoangcongtuan.quanlylichhoc.utils.ReminderManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity implements ReminderAdapter.ItemClickListener, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        ArrayList<Reminder> listReminders = ReminderDatabase.getsInstance(getApplicationContext()).getAllReminders();
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
                CircularAnimUtil.startActivity(AlarmActivity.this, i, btnAddAlarm, R.color.colorPrimary);
                //startActivityForResult(i, RC_ADD);
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
        Intent i = new Intent(AlarmActivity.this, AlarmDetailsActivity.class);
        i.putExtra(ReminderManager.KEY_REMINDER_ID, reminder.getId());
        startActivityForResult(i, RC_DETAIL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_DETAIL || requestCode == RC_ADD) {
            //update alarm list
            reminderAdapter.removeAllReminder();
            ArrayList<Reminder> lstReminder = ReminderDatabase.getsInstance(getApplicationContext()).getAllReminders();
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
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            final Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(
                        sdf.parse(deleteReminder.getDate())
                );
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ReminderDatabase.getsInstance(getApplicationContext()).deleteReminder(deleteReminder.getId());
            ReminderManager.getsInstance(getApplicationContext()).deleteReminder(deleteReminder.getId());
            reminderAdapter.removeReminder(viewHolder.getAdapterPosition());
            //reminderAdapter.notifyDataSetChanged();
            Snackbar.make(alarmLayout, getResources().getString(R.string.remove_alarm_success), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.undo), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            reminderAdapter.restoreReminder(deleteReminder, deletePosition);
                            ReminderDatabase.getsInstance(getApplicationContext()).addReminder(deleteReminder);
                            ReminderManager.getsInstance(getApplicationContext()).setReminder(deleteReminder.getId(), calendar);
                        }
                    })
                    .show();
        }
    }
}
