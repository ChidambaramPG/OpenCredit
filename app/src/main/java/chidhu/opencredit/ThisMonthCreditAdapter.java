package chidhu.opencredit;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import chidhu.opencredit.databaseclasses.OpenCreditDatabase;

/**
 * Author   : Chidambaram P G
 * Date     : 19-04-2018
 */

public class ThisMonthCreditAdapter extends RecyclerView.Adapter<ThisMonthCreditAdapter.MyViewHolder> {
    List<CustomerTransaction> todysTrns;
    Context ctx;
    AlertDialog alertDialog;
    AlertDialog.Builder builder;
    Date c;


    public ThisMonthCreditAdapter(List<CustomerTransaction> todysTrns, Context ctx) {
        this.todysTrns = todysTrns;
        this.ctx = ctx;
    }

    @Override
    public ThisMonthCreditAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.monthly_transaction_layout,parent,false);
        return new ThisMonthCreditAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ThisMonthCreditAdapter.MyViewHolder holder, final int position) {

        final OpenCreditDatabase opDB = Room.databaseBuilder(ctx,OpenCreditDatabase.class,"OpenCreditDB")
                .fallbackToDestructiveMigration().allowMainThreadQueries().build();
        holder.nme.setText(todysTrns.get(position).getName());
        holder.amt.setText("Out of \u20B9"+todysTrns.get(position).getTotAmount());
        if(todysTrns.get(position).getNumTrans() == 1){
            holder.type.setText(todysTrns.get(position).getNumTrans() + " transaction");
        }else{
            holder.type.setText(todysTrns.get(position).getNumTrans() + " transactions");
        }
        holder.dt.setText("\u20B9"+String.valueOf(Float.valueOf(todysTrns.get(position).getTotAmount()) - Float.valueOf(todysTrns.get(position).getTotPaid())) + " left");

        holder.lyt.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {

                Intent i = new Intent(ctx,AddCreditActivity.class);
                i.putExtra("name",todysTrns.get(position).getName());
                i.putExtra("number",todysTrns.get(position).getNumber());
                i.putExtra("customer","old");
                i.putExtra("type","credit");
                ctx.startActivity(i);

            }
        });
    }

    @Override
    public int getItemCount() {
        return todysTrns.size();
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
