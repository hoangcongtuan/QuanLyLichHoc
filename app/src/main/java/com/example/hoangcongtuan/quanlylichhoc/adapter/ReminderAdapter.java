package com.example.hoangcongtuan.quanlylichhoc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.models.Reminder;

import java.util.List;


public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    private List<Reminder> mReminders;
    private Context mContext;
    private ItemClickListener clickListener;

    public ReminderAdapter(Context mContext, List<Reminder> mReminders) {
        this.mReminders = mReminders;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View reminderView = inflater.inflate(R.layout.item_alarm, parent, false);

        ViewHolder viewHolder = new ViewHolder(reminderView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Reminder reminder = mReminders.get(position);

        holder.tvTitle.setText(reduceString(
                reminder.getTitle())
        );
        holder.tvContent.setText(
                reminder.getContent()
        );
        holder.tvDate.setText(reduceString(
                reminder.getDate())
        );
        holder.tvTime.setText(
                reminder.getTime()
        );
    }

    private String reduceString(String str) {
        if (str == null)
            return "";
        if (str.length() > 15)
            return str.substring(0, 15) + "...";
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
        private TextView tvTitle, tvContent, tvDate, tvTime;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);

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
}
