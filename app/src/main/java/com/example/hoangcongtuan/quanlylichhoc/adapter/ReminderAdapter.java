package com.example.hoangcongtuan.quanlylichhoc.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.models.Reminder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    private List<Reminder> mReminders;
    private Context mContext;
    private ItemClickListener clickListener;
    private Calendar mCalendar;

    public ReminderAdapter(Context mContext, List<Reminder> mReminders) {
        this.mReminders = mReminders;
        this.mContext = mContext;
        this.mCalendar = Calendar.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View reminderView = inflater.inflate(R.layout.layout_item_alarm, parent, false);

        ViewHolder viewHolder = new ViewHolder(reminderView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Reminder reminder = mReminders.get(position);

        holder.tvTitle.setText(reduceString(
                reminder.getTitle())
        );
        holder.tvContent.setText(reduceString(
                reminder.getContent())
        );
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        try {
            mCalendar.setTime(
                    sdf.parse(reminder.getDate())
            );

            SimpleDateFormat sdfDate = new SimpleDateFormat("EEEE dd/MM/yyyy");
            SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a");

            holder.tvDate.setText(
                    sdfDate.format(mCalendar.getTime())
            );
            holder.tvTime.setText(
                    sdfTime.format(mCalendar.getTime())
            );

            holder.tvSomeDay.setText(
                    getSomeDay(mCalendar)
            );

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "Co loi khi truy xuat csdl!", Toast.LENGTH_LONG).show();
        }

    }

    private String getSomeDay(Calendar mCalendar) {
        Calendar calCurrent = Calendar.getInstance();
        Calendar calSomeDay = Calendar.getInstance();
        calCurrent.set(calCurrent.get(Calendar.YEAR), calCurrent.get(Calendar.MONTH), calCurrent.get(Calendar.DATE));
        calSomeDay.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DATE));

        long current = calCurrent.getTimeInMillis();
        long someday = calSomeDay.getTimeInMillis();

        int days = (int) ((someday - current) / (24 * 60 * 60 * 1000));
        if (days == 0)
            return mContext.getResources().getString(R.string.today);
        else if (days == 1)
            return mContext.getResources().getString(R.string.tomorrow);
        else if (days == -1)
            return mContext.getResources().getString(R.string.lastday);
        else if (days > 0)
            return mContext.getResources().getString(R.string.infuture);
        else if (days < 0)
            return mContext.getResources().getString(R.string.inpast);

        return null;
    }

    private String reduceString(String str) {
        if (str == null)
            return "";
        if (str.length() > 25)
            return str.substring(0, 25) + "...";
        return str;
    }

    @Override
    public int getItemCount() {
        return mReminders.size();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public interface ItemClickListener {
        public void onClick(View view, int position, boolean isLongClick);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        private TextView tvTitle, tvContent, tvDate, tvTime, tvSomeDay;
        public RelativeLayout viewBackground;
        public ConstraintLayout viewForeground;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvSomeDay = itemView.findViewById(R.id.tvSomeDay);
            viewBackground = itemView.findViewById(R.id.background);
            viewForeground = itemView.findViewById(R.id.foreground);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(clickListener != null)
                clickListener.onClick(view, getAdapterPosition(), false);
        }

        @Override
        public boolean onLongClick(View view) {
            if(clickListener != null)
                clickListener.onClick(view, getAdapterPosition(), true);
            return true;
        }
    }

    public void addReminder(Reminder reminder) {
        mReminders.add(reminder);
        notifyDataSetChanged();
    }

    public void removeReminder(int position) {
        mReminders.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreReminder(Reminder reminder, int position) {
        mReminders.add(position, reminder);
        notifyItemInserted(position);
    }

    public Reminder getReminder(int position) {
        return mReminders.get(position);
    }

    public void removeAllReminder() {
        mReminders.clear();
        notifyDataSetChanged();
    }
}
