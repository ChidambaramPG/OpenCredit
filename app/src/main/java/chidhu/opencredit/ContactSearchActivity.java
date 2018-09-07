package chidhu.opencredit;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ContactSearchActivity extends AppCompatActivity {

    AutoCompleteTextView autoCompleteTextView;
    ProgressBar loadContactProgress;
    RelativeLayout searchLyt;
    TextView msg;

    private ArrayList<Map<String, String>> arrayListMap;
    private SimpleAdapter simpleAdapter;
    public String PREFS_FILE_NAME = "myPrefs";
    int MY_PERMISSIONS_REQUEST_READ_CONTACTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_search);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SpannableString s = new SpannableString("SEARCH CONTACTS");
        s.setSpan(new chidhu.opencredit.TypefaceSpan(this, "Oswald-Regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);

        arrayListMap = new ArrayList<Map<String, String>>();

        searchLyt = findViewById(R.id.searchLyt);
        searchLyt.setEnabled(false);


        loadContactProgress = findViewById(R.id.loadProgress);
        loadContactProgress.setVisibility(View.VISIBLE);
        msg = findViewById(R.id.msgTxt);

         CheckPermissionAndPopulate();


        autoCompleteTextView = findViewById(R.id.contactSearchAutocomplete);
        autoCompleteTextView.setEnabled(false);
        simpleAdapter = new SimpleAdapter(this, arrayListMap, R.layout.contact_list_view,
                new String[] { "Name", "Phone" }, new int[] {
                R.id.ccontName, R.id.ccontNo });
        autoCompleteTextView.setAdapter(simpleAdapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View arg1, int index,
                                    long arg3) {
                Map<String, String> map = (Map<String, String>) av.getItemAtPosition(index);

                String name  = map.get("Name");
                String number = map.get("Phone");
                autoCompleteTextView.setText(""+name+" ("+number+")");

                Intent returnIntent = new Intent();
                returnIntent.putExtra("resultName",""+name+"");
                returnIntent.putExtra("resultNumber",""+number+"");
                setResult(Activity.RESULT_OK,returnIntent);
                finish();

            }
        });

    }

    private void CheckPermissionAndPopulate() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                if(isFirstTimeAskingPermission(this,android.Manifest.permission.READ_CONTACTS)){
                    firstTimeAskingPermission(this,
                            android.Manifest.permission.READ_CONTACTS, false);
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                } else {
                    //Permission disable by device policy or user denied permanently. Show proper error message
                }
            }
        } else {

            MyAsyncTask pop = new MyAsyncTask();
            pop.execute();

        }
    }

    private class MyAsyncTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
// this is where you implement your long-running task
            PopulatePeopleListFaster();
            return true;
        }

        @Override
        public void onPostExecute(Boolean result) {
            loadContactProgress.setVisibility(View.GONE);
            autoCompleteTextView.setEnabled(true);
            searchLyt.setEnabled(true);
            msg.setText("You can search for contact now!");
        }
    }

    public static void firstTimeAskingPermission(Context context, String permission, boolean isFirstTime){
        SharedPreferences sharedPreference = context.getSharedPreferences("myPrefs", MODE_PRIVATE);
        sharedPreference.edit().putBoolean(permission, isFirstTime).apply();
    }

    public static boolean isFirstTimeAskingPermission(Context context, String permission){
        return context.getSharedPreferences("myPrefs", MODE_PRIVATE).getBoolean(permission, true);
    }

    public void PopulatePeopleListFaster(){

            long startnow;
            long endnow;

            startnow = android.os.SystemClock.uptimeMillis();
            ArrayList arrContacts = new ArrayList();
            arrayListMap.clear();

            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER;
            Cursor cursor = this.getContentResolver().query(uri,
                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone._ID,
                    ContactsContract.Contacts._ID},
                    selection, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

            cursor.moveToFirst();

            while (cursor.isAfterLast() == false) {

                Map<String, String> NamePhoneType = new HashMap<String, String>();
                String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String contactType ="0";
                int phoneContactID = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
                int contactID = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                NamePhoneType.put("Name", contactName);
                NamePhoneType.put("Phone", contactNumber);
                if(contactType.equals("0")) {
                    NamePhoneType.put("Type", "Work");
                }else if(contactType.equals("1")) {
                    NamePhoneType.put("Type", "Home");
                }else if(contactType.equals("2")) {
                    NamePhoneType.put("Type", "Mobile");
                }else {
                    NamePhoneType.put("Type", "Other");
                }
                arrayListMap.add(NamePhoneType);
                cursor.moveToNext();
            }
            cursor.close();
            cursor = null;

            endnow = android.os.SystemClock.uptimeMillis();
            Log.d("END", "TimeForContacts " + (endnow - startnow) + " ms");

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
