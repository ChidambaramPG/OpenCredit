package chidhu.opencredit;

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

/**
 * Author   : Chidambaram P G
 * Date     : 28-04-2018
 */

public class TotalCreditAdapter extends RecyclerView.Adapter<TotalCreditAdapter.MyViewHolder> {

    List<CustomerTransaction> todysTrns;
    Context ctx;
    AlertDialog alertDialog;
    AlertDialog.Builder builder;
    Date c;


    public TotalCreditAdapter(List<CustomerTransaction> todysTrns, Context ctx) {
        this.todysTrns = todysTrns;
        this.ctx = ctx;
    }

    @Override
    public TotalCreditAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.monthly_transaction_layout,parent,false);
        return new TotalCreditAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TotalCreditAdapter.MyViewHolder holder, final int position) {

//        final OpenCreditDatabase opDB = Room.databaseBuilder(ctx,OpenCreditDatabase.class,"OpenCreditDB")
//                .fallbackToDestructiveMigration().allowMainThreadQueries().build();
        holder.nme.setText(todysTrns.get(position).getName());
        holder.amt.setText("Out of \u20B9"+todysTrns.get(position).getTotAmount());
        if(todysTrns.get(position).getNumTrans() == 1){
            holder.type.setText(todysTrns.get(position).getNumTrans() + " transaction");
        }else{
            holder.type.setText(todysTrns.get(position).getNumTrans() + " transactions");
        }
        holder.dt.setText("\u20B9"+String.valueOf(Float.valueOf(todysTrns.get(position).getTotAmount()) - Float.valueOf(todysTrns.get(position).getTotPaid())) + " left");

        holder.lyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(ctx,UserTransactionActivity.class);
                i.putExtra("number",todysTrns.get(position).getNumber());
                i.putExtra("name",todysTrns.get(position).getName());
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
