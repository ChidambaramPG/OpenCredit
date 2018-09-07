package chidhu.opencredit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Author   : Chidambaram P G
 * Date     : 19-04-2018
 */

public class TodaysTransactionsAdapter extends RecyclerView.Adapter<TodaysTransactionsAdapter.MyViewHolder>{

    List<Transaction> todysTrns;
    Context ctx;

    public TodaysTransactionsAdapter(List<Transaction> todysTrns, Context ctx) {
        this.todysTrns = todysTrns;
        this.ctx = ctx;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todays_transactions_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.nme.setText(todysTrns.get(position).getUname());
        holder.amt.setText(todysTrns.get(position).getAmount());
        holder.type.setText(todysTrns.get(position).getTransType());
        holder.dt.setText(todysTrns.get(position).getDate());
        if(todysTrns.get(position).getTransType().equals("debit")){
            holder.typeImg.setImageDrawable(ctx.getDrawable(R.drawable.ic_debit_icon));
        }
    }

    @Override
    public int getItemCount() {
        return todysTrns.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nme,amt,dt,type;
        ImageView typeImg;
        public MyViewHolder(View itemView) {
            super(itemView);
            nme = itemView.findViewById(R.id.monthTxt);
            amt = itemView.findViewById(R.id.balTxt);
            dt = itemView.findViewById(R.id.balTxt2);
            type = itemView.findViewById(R.id.yearTxt);
            typeImg = itemView.findViewById(R.id.typeImg);
        }
    }
}
