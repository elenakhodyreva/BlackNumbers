package com.blacknumapps.elena.anticollector;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Elena on 04.09.2017.
 */

public class MyService extends Service {



    BroadcastReceiver receiver;
    public static final String BRD= "com.example.elena.anticollector.OnlyContacts";
    public static final String TAG= "my_report";
    boolean onlyCont= false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onbind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buttonOnClicked();
        Log.d(TAG, "oncreate");


        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.moon)
                .setOngoing(true)
                .setContentTitle("Black numbers")
                .setContentText("Black numbers сервис работает");
        Notification not = builder.build();

        startForeground(1, not);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onstartcommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "ondestroy");

        if(receiver!= null)
        {
            Log.d(TAG, "receiver off");
            unregisterReceiver(receiver);
        }

        stopForeground(true);
        super.onDestroy();
    }

    public void buttonOnClicked()
    {
        receiver = new PhoneStateBroadcastReceiver();
        IntentFilter filter= new IntentFilter();

        filter.addAction(BRD);
        filter.addAction("android.intent.action.PHONE_STATE");
        filter.addAction("android.intent.action.NEW_OUTGOING_CALL");

        registerReceiver(receiver, filter);

        Log.d(TAG, "receiver on");
    }
}
