package chidhu.opencredit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddInventoryActivity extends AppCompatActivity {

    TextInputEditText prodDesc, prodHSN,prodItem,prodCess, prodPurPric, prodSelPric,prodDiscnt,prodQty,prodItemNote;
    Spinner prodType,prodGST,prodUnit;
    FloatingActionButton addItem;
    String prodDescStr = "",prodHSNStr = "",prodItemStr = "",prodCessStr = "",prodPurPricStr = "",prodSelPricStr = "",prodDiscntStr = "",prodQtyStr = "",prodItemNoteStr = "",prodTypeStr = "",prodGSTStr = "",prodUnitStr = "";
    List<String> itemDescs = new ArrayList<>();
    DatabaseReference dbRef;
    FirebaseUser user;
    boolean itemExist = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_inventory);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SpannableString s = new SpannableString("ADD ITEM");
        s.setSpan(new chidhu.opencredit.TypefaceSpan(this, "Oswald-Regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);

        dbRef = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();



        dbRef.child("INVENTORY").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snap1:dataSnapshot.getChildren()){
                    Map<String, Object> val = (Map<String, Object>) snap1.getValue();


                    prodDescStr = (String) val.get("prodDesc");


                    System.out.println(prodDescStr + " :"+prodHSNStr + " :"+prodItemStr + " :"+prodCessStr + " :"+prodPurPricStr + " :"+prodSelPricStr + " :"+prodDiscntStr + " :"+prodQtyStr + " :"+prodItemNoteStr + " :"+prodTypeStr + " :"+prodGSTStr + " :"+prodUnitStr);
                    itemDescs.add(prodDescStr);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        prodDesc = findViewById(R.id.prodDesc);
        prodHSN = findViewById(R.id.prodHSN);
        prodItem = findViewById(R.id.prodItem);
        prodCess = findViewById(R.id.prodCessAmnt);
        prodPurPric = findViewById(R.id.prodPurPrice);
        prodSelPric = findViewById(R.id.prodSelPrice);
        prodDiscnt = findViewById(R.id.prodDiscount);
        prodQty = findViewById(R.id.prodQty);
        prodItemNote = findViewById(R.id.prodNote);
        prodType = findViewById(R.id.ItemTypeSpinner);
        prodGST = findViewById(R.id.TaxRateSpinner);
        prodUnit = findViewById(R.id.UnitSpinner);

        addItem = findViewById(R.id.addItmBtn);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prodDescStr = prodDesc.getText().toString();
                prodHSNStr = prodHSN.getText().toString();
                prodItemStr = prodItem.getText().toString();
                prodCessStr = prodCess.getText().toString();
                prodPurPricStr = prodPurPric.getText().toString();
                prodSelPricStr = prodSelPric.getText().toString();
                prodDiscntStr = prodDiscnt.getText().toString();
                prodQtyStr = prodQty.getText().toString();
                prodItemNoteStr = prodItemNote.getText().toString();
                prodTypeStr = prodType.getSelectedItem().toString();
                prodGSTStr = prodGST.getSelectedItem().toString();
                prodUnitStr = prodUnit.getSelectedItem().toString();

                if(prodDescStr.isEmpty()){
                    prodDesc.setError("Required");
                }
                if(prodItemStr.isEmpty()){
                    prodItem.setError("Required");
                }
                if(prodCessStr.isEmpty()){
                    prodCess.setError("Required");
                }
                if(prodPurPricStr.isEmpty()){
                    prodPurPric.setError("Required");
                }
                if(prodSelPricStr.isEmpty()){
                    prodSelPric.setError("Required");
                }
                if(prodDiscntStr.isEmpty()){
                    prodDiscnt.setError("Required");
                }
                if(prodQtyStr.isEmpty()){
                    prodQty.setError("Required");
                }

                for(String item:itemDescs){
                    if(prodDescStr.equals(item)){
                        itemExist = true;
                    }
                }

                if(itemExist){
                    Toast.makeText(AddInventoryActivity.this, "Item with same HSN exists. Verify if the product in inventory.", Toast.LENGTH_SHORT).show();
                }else{
                    InventoryItems item = new InventoryItems(prodDescStr,prodHSNStr,prodItemStr,prodCessStr,prodPurPricStr,prodSelPricStr,prodDiscntStr,prodQtyStr,prodItemNoteStr,prodTypeStr,prodGSTStr,prodUnitStr);
                    dbRef.child("INVENTORY").child(user.getUid()).child(prodDescStr).setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(AddInventoryActivity.this, prodDescStr+ " with HSN "+ prodHSNStr +" added to inventory.", Toast.LENGTH_SHORT).show();
                                prodDesc.setText("");
                                prodHSN.setText("");
                                prodItem.setText("");
                                prodCess.setText("");
                                prodPurPric.setText("");
                                prodSelPric.setText("");
                                prodDiscnt.setText("");
                                prodQty.setText("");
                                prodItemNote.setText("");

                                prodDescStr = "";
                                prodHSNStr = "";
                                prodItemStr = "";
                                prodCessStr = "";
                                prodPurPricStr = "";
                                prodSelPricStr = "";
                                prodDiscntStr = "";
                                prodQtyStr = "";
                                prodItemNoteStr = "";
                                prodTypeStr = "";
                                prodGSTStr = "";
                                prodUnitStr = "";

                            }
                        }
                    });
                }

            }
        });
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

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
