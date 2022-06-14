package com.example.utehystudent;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String title = "CẢNH BÁO";
        String message = intent.getStringExtra("message");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "android")
                .setSmallIcon(R.drawable.utehy_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(false)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(123, builder.build());

        SharedPreferences pref = context.getSharedPreferences("Alarm", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("status", "finish");
        editor.apply();

        cancelAlarm(context);

    }

    private void cancelAlarm(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Log.d("alarmm", "cancelAlarm: HỦY");
    }
}
