package com.example.solar_alarm;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.ViewHolder
 */
public class AlarmViewHolder extends RecyclerView.ViewHolder
{
    public TextView TextView;

    public AlarmViewHolder(@NonNull TextView textView)
    {
        super(textView);
        TextView = textView;
    }
}
