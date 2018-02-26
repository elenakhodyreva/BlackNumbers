package com.blacknumapps.elena.anticollector;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;

/**
 * A simple {@link Fragment} subclass.
 */
public class StartFragment extends Fragment implements View.OnClickListener,
        LoaderManager.LoaderCallbacks{

    View v;

    Button btnOn, btnOff, btnAdd;
    EditText etNumber, etName;
    CheckBox cbOnlyContacts;
    ListView lvContacts;
    public static final int CM_DELETE_ID= 1;

    boolean cbIsChecked= false;

    DBHelper dbHelper;
    SQLiteDatabase db;
    Cursor c;
    SimpleCursorAdapter simpleCursorAdapter;

    public static final String BRD= "com.example.elena.anticollector.OnlyContacts";

    public StartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        v = inflater.inflate(R.layout.start_fragment, container, false);
        setWidgets();
        return  v;
    }

    public void setWidgets()
    {
        btnOn= (Button)v.findViewById(R.id.btnOn);
        btnOff= (Button)v.findViewById(R.id.btnOff);
        btnAdd= (Button)v.findViewById(R.id.btnAdd);

        btnOn.setOnClickListener(this);
        btnOff.setOnClickListener(this);
        btnAdd.setOnClickListener(this);

        etNumber= (EditText)v.findViewById(R.id.etNumber);
        etName= (EditText)v.findViewById(R.id.etName);

        lvContacts= (ListView)v.findViewById(R.id.lvContacts);
        registerForContextMenu(lvContacts);

        cbOnlyContacts= (CheckBox)v.findViewById(R.id.cbOnlyContacts);
        cbOnlyContacts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent= new Intent(BRD);
                if(cbOnlyContacts.isChecked())
                {
                    cbIsChecked= true;
                    Bundle args= new Bundle();
                    args.putBoolean("chk", cbIsChecked);
                    intent.putExtras(args);
                    getContext().sendBroadcast(intent);
                }
                else
                {
                    cbIsChecked= false;
                    Bundle args= new Bundle();
                    args.putBoolean("chk", cbIsChecked);
                    intent.putExtras(args);
                    getContext().sendBroadcast(intent);
                }
            }
        });


        dbHelper = new DBHelper(getContext());
        db = dbHelper.getReadableDatabase();

        c = db.query("blcontacts", null, null, null, null, null, null);

        String from[] = {"phonenumber", "name"};
        int to[] = {R.id.tvBLPhone, R.id.tvBLName};

        simpleCursorAdapter = new SimpleCursorAdapter(getContext(),
                R.layout.blacklist_item, null,
                from, to, 0);

        lvContacts.setAdapter(simpleCursorAdapter);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btnOn:

                getActivity().startService(new Intent(getContext(), MyService.class));



                if(cbOnlyContacts.isChecked())
                {
                    MyTask mt= new MyTask();
                    mt.execute();
                }

                break;

            case R.id.btnOff:
                getActivity().stopService(new Intent(getContext(), MyService.class));
                break;

            case R.id.btnAdd:
                addContactClicked();
                break;
        }
    }



    public void addContactClicked()
    {
        if(!etNumber.getText().toString().isEmpty() && etNumber.getText().toString()!= null
                && !etName.getText().toString().isEmpty() && etName.getText().toString()
                != null)
        {
            dbHelper= new DBHelper(getContext());
            ContentValues cv = new ContentValues();
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            cv.put("phonenumber", etNumber.getText().toString());
            cv.put("name", etName.getText().toString());

            long rowId= db.insert("blcontacts", null, cv);
            getLoaderManager().getLoader(0).forceLoad();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        c.close();
        dbHelper.close();
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(getContext(), db);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        simpleCursorAdapter.swapCursor((Cursor)data);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_result);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getItemId()== CM_DELETE_ID)
        {
            AdapterView.AdapterContextMenuInfo acmi= (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

            db.delete("blcontacts", "_id" + " = " + acmi.id, null);
            getLoaderManager().getLoader(0).forceLoad();
            return true;
        }

        return super.onContextItemSelected(item);
    }

    static class MyCursorLoader extends CursorLoader {

        SQLiteDatabase db;

        public MyCursorLoader(Context context, SQLiteDatabase db) {
            super(context);
            this.db = db;
        }

        @Override
        public Cursor loadInBackground() {
            Cursor cursor = db.query("blcontacts", null, null, null, null, null, null);

            return cursor;
        }
    }

    class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cbIsChecked= true;
            Log.d("tag", "is checked");
            Bundle args= new Bundle();
            args.putBoolean("chk", cbIsChecked);
            Intent intent= new Intent(BRD);
            intent.putExtras(args);
            getContext().sendBroadcast(intent);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
