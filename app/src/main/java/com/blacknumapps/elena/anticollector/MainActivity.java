package com.blacknumapps.elena.anticollector;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity{

    StartFragment startFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("tag", "oncreate called");

        if(startFragment==null)
            startFragment= new StartFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.lContainer,
                startFragment).commit();
    }

}
