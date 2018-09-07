package chidhu.opencredit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import chidhu.opencredit.databaseclasses.OpenCreditDatabase;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class UserTransactionActivity extends AppCompatActivity {

    RecyclerView userTransList;
    RecyclerView.Adapter adapter;
    public OpenCreditDatabase opDB;
    List<Transaction> userTrans = new ArrayList<>();;
    Date c;
    int credit=0, debit = 0, bal = 0;
    TextView balTxt;
    Button receive;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    AlertDialog dialog;
    AlertDialog.Builder builder;

    String num, name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_transaction);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        Intent i = getIntent();
        num = i.getStringExtra("number");
        name = i.getStringExtra("name");
        SpannableString s = new SpannableString(name.toUpperCase());
        s.setSpan(new chidhu.opencredit.TypefaceSpan(this, "Oswald-Regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);

        c = Calendar.getInstance().getTime();
        balTxt = findViewById(R.id.creditTxt);
        receive = findViewById(R.id.receiveBtn);

        userTransList = findViewById(R.id.userTransactionList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        userTransList.setLayoutManager(mLayoutManager);
        adapter = new UserTransactionAdapter(userTrans,UserTransactionActivity.this);
        userTransList.setAdapter(adapter);


        dbRef.child("TRANSACTIONS").child(user.getUid()).child(num)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        userTrans.clear();

                        for(DataSnapshot snap: dataSnapshot.getChildren()){
                            for(DataSnapshot snap1: snap.getChildren()){
                                for(DataSnapshot snap2: snap1.getChildren()){
                                    for(DataSnapshot snap3: snap2.getChildren()){
                                        System.out.println(snap3);
                                        Map<String, Object> val = (Map<String, Object>) snap3.getValue();
                                        String date = (String) val.get("date");
                                        String time = (String) val.get("time");
                                        String amount = (String) val.get("amount");
                                        String transType = (String) val.get("transType");
                                        String uname = (String) val.get("uname");
                                        String number = (String) val.get("number");
                                        String month = (String) val.get("month");
                                        String year = (String) val.get("year");
                                        String note = (String) val.get("note");
                                        String bill = (String) val.get("bill");
                                        String notified = (String) val.get("notified");

                                        userTrans.add(new Transaction(date,time,amount,transType,uname,number,month,year,note,bill,notified));
                                        System.out.println(uname + " - " + amount);
                                        if(transType.equals("credit")) {
                                            credit += Float.valueOf(amount);
                                        }else if(transType.equals("debit")){
                                            debit += Float.valueOf(amount);
                                        }

                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                        Collections.sort(userTrans, new Comparator<Transaction>() {
                            @Override
                            public int compare(Transaction t0, Transaction t1) {
                                SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh-mm-ss");
                                try {
                                    Date d1 = formatter.parse(t0.getDate()+" "+t0.getTime());
                                    Date d2 = formatter.parse(t1.getDate()+" "+t1.getTime());
                                    return d1.compareTo(d2);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                return 1;
                            }
                        });

                        adapter.notifyDataSetChanged();

                        balTxt.setText("\u20B9"+String.valueOf(credit - debit));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        receive.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {

                
                SimpleDateFormat trnsDt = new SimpleDateFormat("dd-MMM-yyyy");
                final String formattedTrnsDate = trnsDt.format(c);
                SimpleDateFormat trnsTi = new SimpleDateFormat("hh-mm-ss");
                final String formattedTrnsTime = trnsTi.format(c);
                SimpleDateFormat trnsMonth = new SimpleDateFormat("MMM");
                final String formattedTrnsMonth = trnsMonth.format(c);
                SimpleDateFormat trnsYear = new SimpleDateFormat("yyyy");
                final String formattedTrnsYear = trnsYear.format(c);

                builder = new AlertDialog.Builder(new ContextThemeWrapper(UserTransactionActivity.this, R.style.Theme_Dialog));
                dialog = builder.create();
                LayoutInflater inflater = dialog.getLayoutInflater();
                View dialoglayout = inflater.inflate(R.layout.reveive_alert_layout, null);
                final EditText amount = dialoglayout.findViewById(R.id.amntTxt);
                builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if( amount.getText().toString().isEmpty()){
                            Toast.makeText(UserTransactionActivity.this, "Amount not entered", Toast.LENGTH_SHORT).show();
                        }else{
                            Transaction receive = new Transaction(formattedTrnsDate,formattedTrnsTime,amount.getText().toString(),
                                    "debit",name,num,formattedTrnsMonth,formattedTrnsYear,"[]","","false");

                            dbRef.child("TRANSACTIONS").child(user.getUid()).child(num).child(formattedTrnsYear)
                                    .child(formattedTrnsMonth).child(formattedTrnsDate)
                                    .child(formattedTrnsTime).setValue(receive);
                        }
                        
                    }
                });
                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.hide();
                    }
                });
                builder.setView(dialoglayout);
                builder.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete_user_menu, menu);

        Drawable drawable = menu.findItem(R.id.action_delete).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.colorWhite));
        menu.findItem(R.id.action_delete).setIcon(drawable);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent i = new Intent(this,MainActivity.class);
                startActivity(i);
                break;
            case R.id.action_delete:
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(UserTransactionActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(UserTransactionActivity.this);
                }
                builder.setTitle("Delete Customer")
                        .setMessage("Are you sure you want to delete this customer?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                dbRef.child("CUSTOMER_LIST").child(user.getUid()).child(num).removeValue();
                                dbRef.child("TRANSACTIONS").child(user.getUid()).child(num).removeValue();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
