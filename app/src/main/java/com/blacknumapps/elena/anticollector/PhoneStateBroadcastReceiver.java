package com.blacknumapps.elena.anticollector;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import com.android.internal.telephony.ITelephony;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elena on 29.08.2017.
 */

public class PhoneStateBroadcastReceiver extends BroadcastReceiver {

    private final String TAG = getClass().getName();
    private static String number = null;

    ContentResolver contentResolver, cr;
    Cursor cursor, c;

    DBHelper dbHelper;
    SQLiteDatabase db;

    Context ct;
    List<String> phoneNumbers;
    List<String> blockNumbers;

    boolean isContacts= true;

    public static final String BRD= "com.example.elena.anticollector.OnlyContacts";
    public static final String TAG1= "report";

    @Override
    public void onReceive(Context context, Intent intent) {

        ct= context;

        //Toast.makeText(context, ""+ isContacts, Toast.LENGTH_SHORT).show();
        if (intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            Log.d(TAG, intent.getAction() + ", EXTRA_STATE: " + state);
            // on ringing get incoming number

            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                //Turn ON the mute
                audioManager.setStreamMute(AudioManager.STREAM_RING, true);
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                try {
                    Class clazz = Class.forName(telephonyManager.getClass().getName());
                    Method method = clazz.getDeclaredMethod("getITelephony");
                    method.setAccessible(true);
                    ITelephony telephonyService = (ITelephony) method.invoke(telephonyManager);
                    //Checking incoming call number

                    getBlockList();
                    if(isContacts)
                    {
                        getContacts();
                        if (!phoneNumbers.contains(number)  || blockNumbers.contains(number))
                        {
                            telephonyService = (ITelephony) method.invoke(telephonyManager);
                            telephonyService.silenceRinger();
                            telephonyService.endCall();
                        }
                    }


                    else if(blockNumbers.contains(number))
                    {
                        telephonyService = (ITelephony) method.invoke(telephonyManager);
                        telephonyService.silenceRinger();
                        telephonyService.endCall();
                    }

                } catch (Exception e) {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                }
                //Turn OFF the mute
                audioManager.setStreamMute(AudioManager.STREAM_RING, false);
            }
        }
//
//        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
//            number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
//
//            getBlockList();
//
//
//        }

        if(intent.getAction().equalsIgnoreCase(BRD))
        {
            Log.d(TAG1, "intent brd");
            isContacts= intent.getBooleanExtra("chk", false);

            Log.d(TAG1, "isContacts= "+ isContacts);
        }

    }

    private void getContacts()
    {
        contentResolver=  ct.getContentResolver();
        cursor= contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        phoneNumbers= new ArrayList<>();

        while (cursor.moveToNext())
        {
            String phnNumber= cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneNumbers.add(phnNumber);
        }

        cursor.close();
    }

    private void getBlockList()
    {
        dbHelper = new DBHelper(ct);
        db = dbHelper.getReadableDatabase();

        c = db.query("blcontacts", null, null, null, null, null, null);

        blockNumbers= new ArrayList<>();

        if (c.moveToFirst())
        {
            do
            {
                String blockNumber= c.getString(c.getColumnIndex("phonenumber"));
                blockNumbers.add(blockNumber);
            }while (c.moveToNext());

        }

        c.close();
        dbHelper.close();
    }
}
