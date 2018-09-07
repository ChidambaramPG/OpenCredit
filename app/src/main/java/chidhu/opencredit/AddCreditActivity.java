package chidhu.opencredit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import chidhu.opencredit.databaseclasses.Customers;
import chidhu.opencredit.databaseclasses.OpenCreditDatabase;
import chidhu.opencredit.tools.NetworkStateCheck;
import chidhu.opencredit.tools.ObjectAdapter;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.google.gson.Gson;
import com.nmaltais.calcdialog.CalcDialog;
import com.onegravity.contactpicker.contact.Contact;
import com.onegravity.contactpicker.core.ContactPickerActivity;
import com.onegravity.contactpicker.group.Group;


public class AddCreditActivity extends AppCompatActivity implements CalcDialog.CalcDialogCallback{

    EditText dateTxt, timeTxt, creditTxt;
    TextView contactTxt;
    String resultNumber, resultName, trnsType, custType;
    public OpenCreditDatabase opDB;
    Date c;
    boolean customerExist = false;
    Transaction newTrans;
    private static final int GET_PHONE_NUMBER = 3007;
    FirebaseUser user;
    DatabaseReference dbRef, billRef;
    List<String> customerList = new ArrayList<>();
    FirebaseStorage storage;
    private StorageReference stRef;
    String formattedDate, formattedTime;
    RelativeLayout r1, r2, notesTxt;


    ImageView addItem,openCalc;
    android.app.AlertDialog dialog;
    android.app.AlertDialog.Builder builder;
    private ObjectAdapter countryAdapter;
    String prodDescStr = "",prodHSNStr = "",prodItemStr = "",prodCessStr = "",prodPurPricStr = "",
            prodSelPricStr = "",prodDiscntStr = "",prodQtyStr = "",prodItemNoteStr = "",
            prodTypeStr = "",prodGSTStr = "",prodUnitStr = "";
    ArrayList<InventoryItems> items = new ArrayList<>();
//    ArrayList<InventoryItems> added_items = new ArrayList<>();
    ArrayList<BillingItems> billing_items = new ArrayList<>();
    InventoryItems item = null;
    int totPrice = 0;
    
    NetworkStateCheck netChek;
    RecyclerView addedItems;
    AddedItemsAdapter adapter;

    final CalcDialog calcDialog = new CalcDialog();
    private BigDecimal value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_credit);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SpannableString s = new SpannableString("CREDIT");
        s.setSpan(new chidhu.opencredit.TypefaceSpan(this, "Oswald-Regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);

        newTrans = new Transaction();

        dbRef = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();
        stRef = storage.getReference();
        countryAdapter = new ObjectAdapter(getApplicationContext(), items);
        netChek = new NetworkStateCheck(this);
        addedItems = findViewById(R.id.itemsList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        addedItems.setLayoutManager(mLayoutManager);
        adapter = new AddedItemsAdapter(billing_items,this);
        addedItems.setAdapter(adapter);
        
        if(!netChek.isOnline()){
            Toast.makeText(this, "Connect to network", Toast.LENGTH_SHORT).show();
        }
        System.out.println("########################");
        dbRef.child("CUSTOMER_LIST").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    System.out.println(snap.getKey());
                    customerList.add(snap.getKey());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dbRef.child("INVENTORY").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snap1:dataSnapshot.getChildren()){
                    Map<String, Object> val = (Map<String, Object>) snap1.getValue();

                    prodDescStr = (String) val.get("prodDesc");
                    prodHSNStr = (String) val.get("prodHSN");
                    prodItemStr = (String) val.get("prodItem");
                    prodCessStr = (String) val.get("prodCess");
                    prodPurPricStr = (String) val.get("prodPurPric");
                    prodSelPricStr = (String) val.get("prodSelPric");
                    prodDiscntStr = (String) val.get("prodDiscnt");
                    prodQtyStr = (String) val.get("prodQty");
                    prodItemNoteStr = (String) val.get("prodItemNote");
                    prodTypeStr = (String) val.get("prodType");
                    prodGSTStr = (String) val.get("prodGST");
                    prodUnitStr = (String) val.get("prodUnit");

                    System.out.println(prodDescStr + " :"+prodHSNStr + " :"+prodItemStr + " :"+prodCessStr + " :"+prodPurPricStr + " :"+prodSelPricStr + " :"+prodDiscntStr + " :"+prodQtyStr + " :"+prodItemNoteStr + " :"+prodTypeStr + " :"+prodGSTStr + " :"+prodUnitStr);
                    items.add(new InventoryItems(prodDescStr,prodHSNStr,prodItemStr,prodCessStr,prodPurPricStr,prodSelPricStr,prodDiscntStr,prodQtyStr,prodItemNoteStr,prodTypeStr,prodGSTStr,prodUnitStr));
                    countryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        System.out.println("########################");
        c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
        formattedDate = df.format(c);

        SimpleDateFormat tf = new SimpleDateFormat(" HH:mm");
        formattedTime = tf.format(c);

        dateTxt = findViewById(R.id.balTxt2);
        dateTxt.setText(formattedDate);
        timeTxt = findViewById(R.id.timeTxt);
        contactTxt = findViewById(R.id.contactTxt);
        creditTxt = findViewById(R.id.creditTxtLabl);
        notesTxt = findViewById(R.id.notesTxt);
        r1 = findViewById(R.id.relativeLayout);
        r2 = findViewById(R.id.relativeLayout2);
        addItem = findViewById(R.id.addItmBtn);
        openCalc = findViewById(R.id.openCalcBtn);


        addItem.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {

                builder = new android.app.AlertDialog.Builder(new ContextThemeWrapper(AddCreditActivity.this, R.style.Theme_Dialog));
                dialog = builder.create();
                LayoutInflater inflater = dialog.getLayoutInflater();
                View dialoglayout = inflater.inflate(R.layout.alert_add_billing_itm, null);
                final AutoCompleteTextView itemSelected = dialoglayout.findViewById(R.id.itemSrchTxt);
                itemSelected.setAdapter(countryAdapter);
                final EditText qty = dialoglayout.findViewById(R.id.slNoTxt);
                final EditText dsc = dialoglayout.findViewById(R.id.discntTxt);
                Button add = dialoglayout.findViewById(R.id.addItem);
                final TextView unitPrice, unitTax, totPrice;
                unitPrice = dialoglayout.findViewById(R.id.unitPriceTxt);
                unitTax = dialoglayout.findViewById(R.id.unitTaxTxt);
                totPrice = dialoglayout.findViewById(R.id.totPricTxt);

                final boolean[] qtyPresent = {false};
                final boolean[] dscPresent = {false};
                final boolean[] itemPresent = {false};
                itemSelected.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        item = (InventoryItems) adapterView.getItemAtPosition(i);
                        System.out.println(item);
                        System.out.println(item.getProdDesc());
                        itemSelected.setText(item.getProdDesc());
                        itemPresent[0] = true;
                        unitPrice.setText(item.getProdSelPric());
                        unitTax.setText(item.getProdGST());

                        float tot = 0;

                        if(!qty.getText().toString().isEmpty()){
                            if(!dsc.getText().toString().isEmpty()){
                                tot= (Float.valueOf(item.getProdSelPric()) * Float.valueOf(qty.getText().toString()))
                                        - (Float.valueOf(item.getProdSelPric()) * Float.valueOf(qty.getText().toString()) * ((Float.valueOf(dsc.getText().toString()))/100));
                            }else{
                                tot = Float.valueOf(item.getProdSelPric()) * Float.valueOf(qty.getText().toString());
                            }
                            totPrice.setText(String.valueOf(tot));
                        }
                    }
                });


                qty.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int i, KeyEvent keyEvent) {
                        System.out.println(qty.getText());
                        if(item != null && !qty.getText().toString().isEmpty()){
                            if(dsc.getText().toString().isEmpty()){
                                totPrice.setText(String.valueOf(Float.valueOf(item.getProdSelPric()) * Integer.valueOf(qty.getText().toString())));
                            }else{
                                totPrice.setText(String.valueOf(
                                        ((Float.valueOf(item.getProdSelPric()) -
                                                (Float.valueOf(item.getProdSelPric()) * (Float.valueOf(dsc.getText().toString()) / 100)))
                                                        * Integer.valueOf(qty.getText().toString())
                                        )));
                            }

                        }

                        return false;
                    }
                });

                dsc.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int i, KeyEvent keyEvent) {

                        if(item != null && !qty.getText().toString().isEmpty()){
                            if(dsc.getText().toString().isEmpty()){
                                totPrice.setText(String.valueOf(Float.valueOf(item.getProdSelPric()) * Integer.valueOf(qty.getText().toString())));
                            }else{
                                totPrice.setText(String.valueOf(
                                        ((Float.valueOf(item.getProdSelPric()) -
                                                (Float.valueOf(item.getProdSelPric()) * (Float.valueOf(dsc.getText().toString()) / 100)))
                                                * Integer.valueOf(qty.getText().toString())
                                        )));
                            }
                        }
                        return false;
                    }
                });

                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        System.out.println("clicked");
                        Toast.makeText(AddCreditActivity.this, "clicked add button", Toast.LENGTH_SHORT).show();
                        if(qty.getText().toString().isEmpty()){
                            qty.setError("Required");
                        }else{
                            qtyPresent[0] = true;
                        }
                        if(dsc.getText().toString().isEmpty()){
                            dsc.setError("Required");
                        }else{
                            dscPresent[0] = true;
                        }

                        if(itemSelected.getText().toString().isEmpty()){
                            itemSelected.setError("Required");
                        }else{
                            itemPresent[0] = true;
                        }

                        if(qtyPresent[0] && dscPresent[0] && itemPresent[0]){


                            System.out.println("adding items to list");


                            if(item != null){
//                                System.out.println(item.getProdDesc());
                                float totPrice = Float.valueOf(item.getProdSelPric()) * Float.valueOf(qty.getText().toString());
//                                float totPrice = 0;
                                billing_items.add(new BillingItems(item.getProdDesc(),
                                        item.getProdHSN(),String.valueOf(totPrice),qty.getText().toString(),
                                        item.getProdSelPric(),dsc.getText().toString(),item.getProdGST()));
                                System.out.println(billing_items.size());
                                totPrice = 0;
                                for(int i = 0;i<billing_items.size();i++){
                                    totPrice =totPrice + (Float.valueOf((Float.valueOf(billing_items.get(i).getItemPrice()) *  Float.valueOf(qty.getText().toString())) -(Float.valueOf(billing_items.get(i).getItemPrice()) * (Float.valueOf(billing_items.get(i).getItmDiscount())/100))));
                                }
                                creditTxt.setText(String.valueOf(totPrice));
                                System.out.println(creditTxt.getText());
                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }else{

                                System.out.println(" no item added");

                            }

                            System.out.println();
                        }
                    }
                });
                builder.setView(dialoglayout);
                builder.show();

            }
        });

        creditTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    hideKeyboard(view);
                }
            }
        });

        openCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                value = null;
                calcDialog.setValue(value);
                calcDialog.show(getSupportFragmentManager(), "calc_dialog");
            }
        });


        timeTxt.setHint(formattedTime);
        dateTxt.setEnabled(false);
        timeTxt.setEnabled(false);

        contactTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchContactIntent = new Intent(getApplicationContext(), ContactSearchActivity.class);
                startActivityForResult(searchContactIntent, 1);

            }
        });

        Intent userDetails = getIntent();
        Bundle b = userDetails.getExtras();
        if (b != null) {
            String username = (String) b.get("name");
            String number = (String) b.get("number");
            trnsType = (String) b.get("type");
            custType = (String) b.get("customer");
            resultName = username;
            resultNumber = number;
            System.out.println(username + " (" + number + ")");
            if (!custType.equals("new")) {
                contactTxt.setText(resultName.substring(0, 1).toUpperCase() + resultName.substring(1) + " (" + resultNumber + ")");
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                resultName = data.getStringExtra("resultName");
                resultNumber = data.getStringExtra("resultNumber");
                contactTxt.setText(resultName.substring(0, 1).toUpperCase() + resultName.substring(1) + " (" + resultNumber + ")");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

        if (requestCode == GET_PHONE_NUMBER) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.hasExtra(ContactPickerActivity.RESULT_CONTACT_DATA)) {

                    List<Contact> contacts = (List<Contact>) data.getSerializableExtra(ContactPickerActivity.RESULT_CONTACT_DATA);
                    for (Contact contact : contacts) {

                        System.out.println(contact.getDisplayName());

                    }

                    List<Group> groups = (List<Group>) data.getSerializableExtra(ContactPickerActivity.RESULT_GROUP_DATA);
                    for (Group group : groups) {
                        System.out.println(group.getContacts());
                    }

                }
            }
        }

    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.action_save:{

                Gson gson = new Gson();
                String notes = gson.toJson(billing_items);
                System.out.println(notes);

                SimpleDateFormat trnsDt = new SimpleDateFormat("dd-MMM-yyyy");
                final String formattedTrnsDate = trnsDt.format(c);
                SimpleDateFormat trnsTi = new SimpleDateFormat("hh-mm-ss");
                final String formattedTrnsTime = trnsTi.format(c);
                SimpleDateFormat trnsMonth = new SimpleDateFormat("MMM");
                final String formattedTrnsMonth = trnsMonth.format(c);
                SimpleDateFormat trnsYear = new SimpleDateFormat("yyyy");
                final String formattedTrnsYear = trnsYear.format(c);

                if (creditTxt.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "No amount entered. Canceling user and transaction update.", Toast.LENGTH_SHORT).show();
                    Intent returnToCreditPage = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(returnToCreditPage);
                    finish();
                } else if (resultNumber == null) {
                    Toast.makeText(getApplicationContext(), "No user selected. Canceling user and transaction update.", Toast.LENGTH_SHORT).show();
                    Intent returnToCreditPage = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(returnToCreditPage);
                    finish();
                } else {

                    String num = resultNumber;
                    String number = num.replaceAll("[^0-9]", "");
                    resultNumber = number.substring(number.length() - 10);

                    System.out.println("########################");
                    final String[] bill_link = {""};

                    if (customerList == null) {
                        System.out.println("No User");
                        Customers newCustomer = new Customers();
                        newCustomer.setNumber(resultNumber);
                        newCustomer.setName(resultName);
                        newCustomer.setUid(resultName + resultNumber);
                        newCustomer.setVerified("NOT VERIFIED");
                        c = Calendar.getInstance().getTime();
                        SimpleDateFormat joinDt = new SimpleDateFormat("dd-MMM-yyyy hh-mm");
                        String formattedJoiningDate = joinDt.format(c);
                        newCustomer.setJoindOn(formattedJoiningDate);

                        newTrans.setTime(formattedTrnsTime);
                        newTrans.setDate(formattedTrnsDate);
                        newTrans.setMonth(formattedTrnsMonth);
                        newTrans.setYear(formattedTrnsYear);
                        newTrans.setNumber(resultNumber);
                        newTrans.setUname(resultName);
                        newTrans.setAmount(creditTxt.getText().toString());
                        newTrans.setTransType(trnsType);
                        newTrans.setNotified("false");
                        newTrans.setBill("");
                        newTrans.setNote(notes);

                        dbRef.child("CUSTOMER_LIST").child(user.getUid()).child(resultNumber).setValue(resultName);
                        dbRef.child("TRANSACTIONS").child(user.getUid()).child(resultNumber).child(formattedTrnsYear)
                                .child(formattedTrnsMonth).child(formattedTrnsDate)
                                .child(formattedTrnsTime).setValue(newTrans).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(i);
                            }
                        });
                    } else {

                        for (String cust : customerList) {

                            if (cust.equals(resultNumber)) {
                                customerExist = true;
                            }
                        }

                        if (customerExist) {


                            newTrans.setTime(formattedTrnsTime);
                            newTrans.setDate(formattedTrnsDate);
                            newTrans.setMonth(formattedTrnsMonth);
                            newTrans.setYear(formattedTrnsYear);
                            newTrans.setNumber(resultNumber);
                            newTrans.setUname(resultName);
                            newTrans.setAmount(creditTxt.getText().toString());
                            newTrans.setTransType(trnsType);
                            newTrans.setNotified("false");
                            newTrans.setBill("");
                            newTrans.setNote(notes);

                            dbRef.child("TRANSACTIONS").child(user.getUid()).child(resultNumber).child(formattedTrnsYear)
                                    .child(formattedTrnsMonth).child(formattedTrnsDate)
                                    .child(formattedTrnsTime).setValue(newTrans).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
                                    startActivity(i);
                                }
                            });

                        } else {
                            System.out.println("customer doesnt exist");
                            Customers newCustomer = new Customers();
                            newCustomer.setNumber(resultNumber);
                            newCustomer.setName(resultName);
                            newCustomer.setUid(resultName + resultNumber);
                            newCustomer.setVerified("NOT VERIFIED");
                            c = Calendar.getInstance().getTime();
                            SimpleDateFormat joinDt = new SimpleDateFormat("dd-MMM-yyyy hh-mm");
                            String formattedJoiningDate = joinDt.format(c);
                            newCustomer.setJoindOn(formattedJoiningDate);

                            newTrans.setTime(formattedTrnsTime);
                            newTrans.setDate(formattedTrnsDate);
                            newTrans.setMonth(formattedTrnsMonth);
                            newTrans.setYear(formattedTrnsYear);
                            newTrans.setNumber(resultNumber);
                            newTrans.setUname(resultName);
                            newTrans.setAmount(creditTxt.getText().toString());
                            newTrans.setTransType(trnsType);
                            newTrans.setNotified("false");
                            newTrans.setBill("");
                            newTrans.setNote(notes);

                            dbRef.child("CUSTOMER_LIST").child(user.getUid()).child(resultNumber).setValue(resultName);
                            dbRef.child("TRANSACTIONS").child(user.getUid()).child(resultNumber).child(formattedTrnsYear)
                                    .child(formattedTrnsMonth).child(formattedTrnsDate)
                                    .child(formattedTrnsTime).setValue(newTrans).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
                                    startActivity(i);
                                }
                            });
                        }
                    }


                }
                break;
            }
            default:{

            }

        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onValueEntered(BigDecimal value) {
        System.out.println(value.floatValue());
        creditTxt.setText(String.valueOf(value.floatValue()));
    }
}
