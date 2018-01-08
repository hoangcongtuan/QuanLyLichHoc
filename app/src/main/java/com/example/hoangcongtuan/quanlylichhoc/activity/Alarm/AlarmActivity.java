package com.example.hoangcongtuan.quanlylichhoc.activity.Alarm;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.adapter.ReminderAdapter;
import com.example.hoangcongtuan.quanlylichhoc.helper.RecyclerItemTouchHelper;
import com.example.hoangcongtuan.quanlylichhoc.models.Reminder;
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
    private ReminderAdapter mAdapter;

    private CoordinatorLayout alarm_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.alarm_act_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        alarm_layout = findViewById(R.id.alarm_layout);

        RecyclerView mRecyclerView = findViewById(R.id.rvAlarms);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);

        ArrayList<Reminder> mReminders = ReminderDatabase.getsInstance(getApplicationContext()).getAllReminders();
        mAdapter = new ReminderAdapter(this, mReminders);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setClickListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        FloatingActionButton btnAddAlarm = findViewById(R.id.btnAdd);
        btnAddAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AlarmActivity.this, AddAlarmActivity.class);
                startActivityForResult(i, RC_ADD);
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

    private void showDeleteDialog(final Reminder reminder, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.xoa_nhac_nho));
        builder.setMessage(getResources().getString(R.string.xoa_nhac_nho_detail));
        builder.setPositiveButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //xoa
                ReminderDatabase.getsInstance(getApplicationContext()).deleteReminder(reminder.getId());
                ReminderManager.getsInstance(getApplicationContext()).deleteReminder(reminder.getId());
                mAdapter.removeReminder(position);
                //mAdapter.notifyDataSetChanged();
                Snackbar.make(alarm_layout, getResources().getString(R.string.remove_alarm_success), Snackbar.LENGTH_LONG).show();

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
    public void onClick(View view, int position, boolean isLongClick) {
        Reminder reminder = mAdapter.getReminder(position);
        if(isLongClick) {
            showDeleteDialog(reminder, position);
        } else {
            Intent i = new Intent(AlarmActivity.this, AlarmDetailsActivity.class);
            i.putExtra(ReminderManager.KEY_REMINDER_ID, reminder.getId());
            startActivityForResult(i, RC_DETAIL);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: Result");
        if (requestCode == RC_DETAIL || requestCode == RC_ADD) {
            Log.d(TAG, "onActivityResult: RC_DETAIL");
            //update alarm list
            mAdapter.removeAllReminder();
            ArrayList<Reminder> lstReminder = ReminderDatabase.getsInstance(getApplicationContext()).getAllReminders();
            for(Reminder r : lstReminder)
                mAdapter.addReminder(r);
            mAdapter.notifyDataSetChanged();
            if (requestCode == RC_ADD && resultCode == RESULT_OK)
                Snackbar.make(alarm_layout, getResources().getString(R.string.add_alarm_success), Snackbar.LENGTH_LONG).show();

        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof ReminderAdapter.ViewHolder) {
            final Reminder deleteReminder = mAdapter.getReminder(position);
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
            mAdapter.removeReminder(viewHolder.getAdapterPosition());
            //mAdapter.notifyDataSetChanged();
            Snackbar.make(alarm_layout, getResources().getString(R.string.remove_alarm_success), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.undo), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mAdapter.restoreReminder(deleteReminder, deletePosition);
                            ReminderDatabase.getsInstance(getApplicationContext()).addReminder(deleteReminder);
                            ReminderManager.getsInstance(getApplicationContext()).setReminder(deleteReminder.getId(), calendar);
                        }
                    })
                    .show();
        }
    }
}
