package com.example.solar_alarm;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.ViewHolder
 */
public class AlarmViewHolder extends RecyclerView.ViewHolder
{
    public TextView NameTextView;
    public TextView TimeTextView;
    LinearLayout parent_layout;

    public AlarmViewHolder(@NonNull View textView)
    {
        super(textView);

        NameTextView = itemView.findViewById(R.id.alarmName);
        TimeTextView = itemView.findViewById(R.id.alarmTime);
        parent_layout = itemView.findViewById(R.id.parent_layout);
    }
}
