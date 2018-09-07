package chidhu.opencredit;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import chidhu.opencredit.databaseclasses.OpenCreditDatabase;
import chidhu.opencredit.databaseclasses.Transactions;

/**
 * Author   : Chidambaram P G
 * Date     : 13-05-2018
 */

class CustomerProfileAdapter extends RecyclerView.Adapter<CustomerProfileAdapter.MyViewHolder> {

    List<Transactions> todysTrns;
    Context ctx;
    AlertDialog alertDialog;
    AlertDialog.Builder builder;

    public CustomerProfileAdapter(List<Transactions> todysTrns, Context ctx) {
        this.todysTrns = todysTrns;
        this.ctx = ctx;
    }

    @Override
    public CustomerProfileAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_profie_transactin_layout,parent,false);
        return new CustomerProfileAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomerProfileAdapter.MyViewHolder holder, int position) {

        final OpenCreditDatabase opDB = Room.databaseBuilder(ctx,OpenCreditDatabase.class,"OpenCreditDB")
                .fallbackToDestructiveMigration().allowMainThreadQueries().build();
        holder.nme.setText(todysTrns.get(position).getDate());
        holder.amt.setText(todysTrns.get(position).getAmount());

        if(todysTrns.get(position).getTransType().equals("credit")) {
            holder.amt.setTextColor(Color.RED);
        }

        holder.type.setText(todysTrns.get(position).getTime());
        holder.dt.setText(todysTrns.get(position).getTransType());
        if(todysTrns.get(position).getTransType().equals("debit")){
            holder.typeImg.setImageDrawable(ctx.getDrawable(R.drawable.ic_debit_icon));
        }

    }

    @Override
    public int getItemCount() {
        return this.todysTrns.size();
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
                lyt = itemView.findViewById(R.id.user_details_lyt);
            }

    }
}
