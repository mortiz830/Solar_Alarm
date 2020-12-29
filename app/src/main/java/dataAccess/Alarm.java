package dataAccess;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.solar_alarm.AlarmReceiver;

import java.time.LocalTime;
import java.util.Calendar;

@Entity(tableName = "alarm_table")
public class Alarm
{
    @PrimaryKey @NonNull
    private int AlarmId;

    @NonNull
    private LocalTime AlarmTime;

    @NonNull
    private long Created = System.currentTimeMillis();

    @NonNull
    private String AlarmName;

    private boolean Started;

    public Alarm(LocalTime alarmTime, String alarmName)
    {
        AlarmTime = alarmTime;
        AlarmName = alarmName;
    }

    public LocalTime GetAlarmTime()
    {
        return AlarmTime;
    }
    public String    GetAlarmName()
    {
        return AlarmName;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void schedule(Context context)
    {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent       intent       = new Intent(context, AlarmReceiver.class);

        intent.putExtra("TITLE", AlarmName);

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, AlarmId, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, AlarmTime.getHour());
        calendar.set(Calendar.MINUTE, AlarmTime.getMinute());

        // if alarm time has already passed, increment day by 1
        if (calendar.getTimeInMillis() <= System.currentTimeMillis())
        {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }

        Started = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, AlarmId, intent, 0);
        alarmManager.cancel(alarmPendingIntent);
        this.Started = false;

        String toastText = String.format("Alarm cancelled for %02d:%02d with id %d", AlarmTime.getHour(), AlarmTime.getMinute(), AlarmId);
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
        Log.i("cancel", toastText);
    }
}
