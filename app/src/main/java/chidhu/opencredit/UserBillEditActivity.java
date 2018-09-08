package chidhu.opencredit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class UserBillEditActivity extends AppCompatActivity {

    String number,year,month,date,time,amount,note,bill,uname,type;
    TextView dateTxt,timeTxt,billStatTxt;
    TextInputEditText noteTxt, amntTxt;
    ImageView billImg;
    RecyclerView addedItems;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    ArrayList<BillingItems> billing_items = new ArrayList<>();
    AddedItemsAdapter adapter;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_bill_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent billDetails = getIntent();
        type = number = billDetails.getStringExtra("trnsType");
        uname = billDetails.getStringExtra("name");
        number = billDetails.getStringExtra("number");
        year = billDetails.getStringExtra("year");
        month = billDetails.getStringExtra("month");
        date = billDetails.getStringExtra("date");
        time = billDetails.getStringExtra("time");
        amount = billDetails.getStringExtra("amount");
        note = billDetails.getStringExtra("note");
        bill = billDetails.getStringExtra("bill");



        SpannableString s = new SpannableString("EDIT BILL");
        s.setSpan(new chidhu.opencredit.TypefaceSpan(this, "Oswald-Regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);

        dateTxt = findViewById(R.id.dateTxt);
        timeTxt = findViewById(R.id.timeTxt);
//        noteTxt = findViewById(R.id.noteTxt);
        amntTxt = findViewById(R.id.amountTxt);

        addedItems = findViewById(R.id.itemsList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        addedItems.setLayoutManager(mLayoutManager);

        dateTxt.setText(date);
        timeTxt.setText(time);
//        noteTxt.setText(note);
        amntTxt.setText(amount);

        if(note == null ){
            System.out.println("No items");

            billing_items.clear();
//                adapter = new AddedItemsAdapter(billing_items,ctx);
//                addedItems.setAdapter(adapter);
            System.out.println(billing_items.size());
        }else{
            System.out.println("Items Present");
            gson = new Gson();
            Type listType = new TypeToken<ArrayList<BillingItems>>(){}.getType();
            billing_items = gson.fromJson(note, listType);

            adapter = new AddedItemsAdapter(billing_items,this);
            addedItems.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_bill_menu, menu);

        Drawable drawable = menu.findItem(R.id.action_save).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.colorWhite));
        menu.findItem(R.id.action_save).setIcon(drawable);

        Drawable drawable2 = menu.findItem(R.id.action_delete).getIcon();
        drawable2 = DrawableCompat.wrap(drawable2);
        DrawableCompat.setTint(drawable2, ContextCompat.getColor(this, R.color.colorWhite));
        menu.findItem(R.id.action_delete).setIcon(drawable2);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_save:
                if(amntTxt.getText().toString().isEmpty()){
                    Toast.makeText(UserBillEditActivity.this,"Not Entred ",Toast.LENGTH_SHORT);

                }else{
                    Transaction modif = new Transaction(date,time,amntTxt.getText().toString()
                            ,type,uname,number,month,year,gson.toJson(billing_items),bill,"false");

                    dbRef.child("TRANSACTIONS").child(user.getUid()).child(number).child(year).child(month)
                            .child(date).child(time).setValue(modif).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(UserBillEditActivity.this, "Edit successfully saved.", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(UserBillEditActivity.this, "Edit failed. Try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                break;

            case R.id.action_delete:
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(UserBillEditActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(UserBillEditActivity.this);
                }
                builder.setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                dbRef.child("TRANSACTIONS").child(user.getUid()).child(number).child(year).child(month)
                                        .child(date).child(time).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(UserBillEditActivity.this, "Entry removed successfully.", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(UserBillEditActivity.this, "Delete failed. Try again.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
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
