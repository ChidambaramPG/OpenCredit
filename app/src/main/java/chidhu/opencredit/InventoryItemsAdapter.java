package chidhu.opencredit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author   : Chidambaram P G
 * Date     : 11-07-2018
 */
public class InventoryItemsAdapter extends RecyclerView.Adapter<InventoryItemsAdapter.MyViewHolder> {
    List<InventoryItems> itemsList;
    Context ctx;

    Date c;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();


    android.app.AlertDialog dialog;
    android.app.AlertDialog.Builder builder;

    Spinner prodType,prodGST,prodUnit;
    TextInputEditText prodDesc, prodHSN,prodItem,prodCess, prodPurPric, prodSelPric,prodDiscnt,prodQty,prodItemNote;
    String prodDescStr = "",prodHSNStr = "",prodItemStr = "",prodCessStr = "",prodPurPricStr = "",prodSelPricStr = "",prodDiscntStr = "",prodQtyStr = "",prodItemNoteStr = "",prodTypeStr = "",prodGSTStr = "",prodUnitStr = "";
    List<String> itemDescs = new ArrayList<>();
    boolean itemExist = false;


    public InventoryItemsAdapter(List<InventoryItems> itemsList, Context ctx) {
        this.itemsList = itemsList;
        this.ctx = ctx;
    }

    @Override
    public InventoryItemsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_item_recycler_lyt, parent, false);
        return new InventoryItemsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final InventoryItemsAdapter.MyViewHolder holder, final int position) {
        final InventoryItems item = itemsList.get(position);
        holder.name.setText(item.getProdDesc());
        holder.hsn.setText(item.getProdHSN());
        holder.price.setText("\u20B9" + item.getProdSelPric());
        holder.qty.setText(item.getProdQty() + " " + item.getProdUnit());
        if(Integer.valueOf(item.getProdQty())<10){
            holder.qty.setTextColor(Color.RED);
        }

        String catgory = item.getProdUnit();
        holder.itmType.setImageResource(R.drawable.ic_milk_box);

        switch (catgory){
            case "BOU":

                break;

            case "Bags":
                holder.itmType.setImageResource(R.drawable.ic_shopping_bag);
                break;

            case "Bale":

                break;

            case "Bottles":
                holder.itmType.setImageResource(R.drawable.ic_milk_bottle);
                break;

            case "Boxes":
                holder.itmType.setImageResource(R.drawable.ic_milk_box);
                break;

            case "Buckles":

                break;

            case "Bunches":

                break;

            case "Bundles":

                break;

            case "Cans":
                holder.itmType.setImageResource(R.drawable.ic_can);
                break;

            case "Carat":

                break;

            case "Cartons":
                holder.itmType.setImageResource(R.drawable.ic_milk_box);
                break;

            case "Centimeter":
                holder.itmType.setImageResource(R.drawable.ic_ruler);
                break;

            case "Cubic Centimeter":
                holder.itmType.setImageResource(R.drawable.ic_ruler);
                break;

            case "Cubic Meter":
                holder.itmType.setImageResource(R.drawable.ic_ruler);
                break;

            case "Dozen":

                break;

            case "Drums":
                holder.itmType.setImageResource(R.drawable.ic_oil);
                break;

            case "Grams":
                holder.itmType.setImageResource(R.drawable.ic_weight);
                break;

            case "Great Gross":

                break;

            case "Gross":

                break;

            case "Gross Yards":

                break;

            case "Kilograms":
                holder.itmType.setImageResource(R.drawable.ic_weight);
                break;

            case "Kiloliter":

                break;

            case "Kilometers":
                holder.itmType.setImageResource(R.drawable.ic_ruler);
                break;

            case "Meter":
                holder.itmType.setImageResource(R.drawable.ic_ruler);
                break;

            case "Metric Ton":
                holder.itmType.setImageResource(R.drawable.ic_weight);
                break;

            case "Milliliters":
                holder.itmType.setImageResource(R.drawable.ic_ruler);
                break;

            case "Numbers":

                break;

            case "Others":

                break;

            case "Packs":
                holder.itmType.setImageResource(R.drawable.ic_package);
                break;

            case "Pairs":

                break;

            case "Pieces":

                break;

            case "Quintal":
                holder.itmType.setImageResource(R.drawable.ic_weight);
                break;

            case "Rolls":

                break;

            case "Sets":

                break;

            case "Square Feet":
                holder.itmType.setImageResource(R.drawable.ic_ruler);
                break;

            case "Square Meter":
                holder.itmType.setImageResource(R.drawable.ic_ruler);
                break;

            case "Square Yards":
                holder.itmType.setImageResource(R.drawable.ic_ruler);
                break;

            case "Tablets":

                break;

            case "Ten Grams":
                holder.itmType.setImageResource(R.drawable.ic_weight);
                break;

            case "Thousands":

                break;

            case "Tonnes":
                holder.itmType.setImageResource(R.drawable.ic_weight);
                break;

            case "Tubes":

                break;

            case "US Gallons":
                holder.itmType.setImageResource(R.drawable.ic_oil);
                break;

            case "Units":

                break;

            case "Yards":
                holder.itmType.setImageResource(R.drawable.ic_ruler);
                break;


        }
        holder.delt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbRef.child("INVENTORY").child(user.getUid()).child(itemsList.get(position).getProdDesc()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            itemsList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position,itemsList.size());
                        }else{
                            Toast.makeText(ctx, "Network error.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                
            }
        });

        holder.lyt.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                builder = new android.app.AlertDialog.Builder(new ContextThemeWrapper(ctx, R.style.Theme_Dialog));
                dialog = builder.create();
                LayoutInflater inflater = dialog.getLayoutInflater();
                View dialoglayout = inflater.inflate(R.layout.inventory_item_edit_lyt, null);

                prodDesc = dialoglayout.findViewById(R.id.prodDesc);
                prodHSN =dialoglayout. findViewById(R.id.prodHSN);
                prodItem = dialoglayout.findViewById(R.id.prodItem);
                prodCess = dialoglayout.findViewById(R.id.prodCessAmnt);
                prodPurPric = dialoglayout.findViewById(R.id.prodPurPrice);
                prodSelPric = dialoglayout.findViewById(R.id.prodSelPrice);
                prodDiscnt = dialoglayout.findViewById(R.id.prodDiscount);
                prodQty = dialoglayout.findViewById(R.id.prodQty);
                prodItemNote = dialoglayout.findViewById(R.id.prodNote);

                prodType = dialoglayout.findViewById(R.id.ItemTypeSpinner);
                prodGST = dialoglayout.findViewById(R.id.TaxRateSpinner);
                prodUnit = dialoglayout.findViewById(R.id.UnitSpinner);

                prodDesc.setText(item.getProdDesc());
                prodHSN.setText(item.getProdHSN());
                prodItem.setText(item.getProdItem());
                prodCess.setText(item.getProdCess());
                prodPurPric.setText(item.getProdPurPric());
                prodSelPric.setText(item.getProdSelPric());
                prodDiscnt.setText(item.getProdDiscnt());
                prodQty.setText(item.getProdQty());
                prodItemNote.setText(item.getProdItemNote());



                builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {

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

                        InventoryItems item = new InventoryItems(prodDescStr,prodHSNStr,prodItemStr,prodCessStr,prodPurPricStr,prodSelPricStr,prodDiscntStr,prodQtyStr,prodItemNoteStr,prodTypeStr,prodGSTStr,prodUnitStr);
                        dbRef.child("INVENTORY").child(user.getUid()).child(prodDescStr).setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(ctx, prodDescStr+ " with HSN "+ prodHSNStr +" added to inventory.", Toast.LENGTH_SHORT).show();
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

                                    dialogInterface.cancel();

                                }else{
                                    Toast.makeText(ctx, "Unable to save details. Try later", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                    }
                });

                builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                builder.setView(dialoglayout);
                builder.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name,hsn,price,qty;
        ImageView delt,itmType;
        RelativeLayout lyt;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.itemNameTxt);
            hsn = itemView.findViewById(R.id.itemHSNTxt);
            price = itemView.findViewById(R.id.itemPriceTxt);
            qty  = itemView.findViewById(R.id.itemQtyTxt);
            delt = itemView.findViewById(R.id.delBtn);
            lyt= itemView.findViewById(R.id.user_details_lyt);
            itmType = itemView.findViewById(R.id.itemTypeImg);
        }
    }
}
