package chidhu.opencredit;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Author   : Chidambaram P G
 * Date     : 04-06-2018
 */

public class UserTransactionAdapter extends RecyclerView.Adapter<UserTransactionAdapter.MyViewHolder> {

    List<Transaction> transList = new ArrayList<>();
    Context ctx;
    AlertDialog dialog;
    AlertDialog.Builder builder;
    ArrayList<BillingItems> billing_items = new ArrayList<>();
    AddedItemsAdapter adapter;


    public UserTransactionAdapter(List<Transaction> transList, Context ctx) {
        this.transList = transList;
        this.ctx = ctx;
    }

    @Override
    public UserTransactionAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todays_transactions_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserTransactionAdapter.MyViewHolder holder, final int position) {
        holder.nme.setText(transList.get(position).getUname());
        holder.amt.setText("\u20B9"+transList.get(position).getAmount());
        holder.type.setText(transList.get(position).getTransType());
        holder.dt.setText(transList.get(position).getDate());

        if(transList.get(position).getTransType().equals("debit")){
            holder.typeImg.setImageDrawable(ctx.getDrawable(R.drawable.ic_debit_icon));
        }
        holder.lyt.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
            builder = new AlertDialog.Builder(new ContextThemeWrapper(ctx, R.style.Theme_Dialog));
            dialog = builder.create();

            LayoutInflater inflater = dialog.getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.transaction_details_view, null);

            RecyclerView addedItems = dialoglayout.findViewById(R.id.itemsList);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(ctx);
            addedItems.setLayoutManager(mLayoutManager);


            TextView amount = dialoglayout.findViewById(R.id.amountTxt);
            TextView date = dialoglayout.findViewById(R.id.dateTxt);
            TextView time = dialoglayout.findViewById(R.id.timeTxt);
            amount.setText("\u20B9" +transList.get(position).getAmount());
            date.setText(transList.get(position).getDate());
            time.setText(transList.get(position).getTime());

            if(transList.get(position).getNote() == null ){
                System.out.println("No items");

                billing_items.clear();
//                adapter = new AddedItemsAdapter(billing_items,ctx);
//                addedItems.setAdapter(adapter);
                System.out.println(billing_items.size());
            }else{
                System.out.println("Items Present");
                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<BillingItems>>(){}.getType();
                billing_items = gson.fromJson(transList.get(position).getNote(), listType);

                adapter = new AddedItemsAdapter(billing_items,ctx);
                addedItems.setAdapter(adapter);
                adapter.notifyDataSetChanged();

//                System.out.println(billing_items.size());
            }

            if(!transList.get(position).getBill().isEmpty()){
//                    Picasso.get().load(transList.get(position).getBill()).into(bill);
//                    billMessage.setVisibility(View.GONE);
            }

            builder.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialog.hide();
                }
            });
            builder.setNegativeButton("EDIT BILL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(transList.get(position).getNotified().equals("true")){
                        Toast.makeText(ctx, "Notified bills cannot be edited", Toast.LENGTH_SHORT).show();
                    }else{
                        Intent editIntent = new Intent(ctx,UserBillEditActivity.class);

                        editIntent.putExtra("trnsType",transList.get(position).getTransType());
                        editIntent.putExtra("name",transList.get(position).getUname());
                        editIntent.putExtra("number",transList.get(position).getNumber());
                        editIntent.putExtra("year",transList.get(position).getYear());
                        editIntent.putExtra("month",transList.get(position).getMonth());
                        editIntent.putExtra("date",transList.get(position).getDate());
                        editIntent.putExtra("time",transList.get(position).getTime());
                        editIntent.putExtra("amount",transList.get(position).getAmount());
                        editIntent.putExtra("note",transList.get(position).getNote());
                        editIntent.putExtra("bill",transList.get(position).getBill());
                        ctx.startActivity(editIntent);
                    }
                }
            });

            builder.setView(dialoglayout);
            builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return transList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nme,amt,dt,type;
        ImageView typeImg;
        RelativeLayout lyt;

        public MyViewHolder(View itemView) {
            super(itemView);
            nme = itemView.findViewById(R.id.monthTxt);
            amt = itemView.findViewById(R.id.balTxt);
            dt = itemView.findViewById(R.id.balTxt2);
            type = itemView.findViewById(R.id.yearTxt);
            typeImg = itemView.findViewById(R.id.typeImg);
            lyt = itemView.findViewById(R.id.todaysTransLyt);

        }
    }
}
