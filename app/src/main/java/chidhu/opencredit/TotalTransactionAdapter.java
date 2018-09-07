package chidhu.opencredit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Author   : Chidambaram P G
 * Date     : 28-04-2018
 */

public class TotalTransactionAdapter extends RecyclerView.Adapter<TotalTransactionAdapter.MyViewHolder>{
    List<Transaction> todysTrns = new ArrayList<>();
    Context ctx;
    AlertDialog dialog;
    AlertDialog.Builder builder;


    public TotalTransactionAdapter(List<Transaction> todysTrns, Context ctx) {
        this.todysTrns = todysTrns;
        this.ctx = ctx;
    }

    @Override
    public TotalTransactionAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todays_transactions_layout,parent,false);
        return new TotalTransactionAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TotalTransactionAdapter.MyViewHolder holder, final int position) {
        holder.nme.setText(todysTrns.get(position).getUname());
        holder.amt.setText(todysTrns.get(position).getAmount());
        holder.type.setText(todysTrns.get(position).getTransType());
        holder.dt.setText(todysTrns.get(position).getDate());

        if(todysTrns.get(position).getTransType().equals("debit")){
            holder.typeImg.setImageDrawable(ctx.getDrawable(R.drawable.ic_debit_icon));
        }
        holder.lyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder = new AlertDialog.Builder(ctx);
                dialog = builder.create();
                LayoutInflater inflater = dialog.getLayoutInflater();
                View dialoglayout = inflater.inflate(R.layout.transaction_details_view, null);

                TextView amount = dialoglayout.findViewById(R.id.amountTxt);
                TextView date = dialoglayout.findViewById(R.id.dateTxt);
                TextView time = dialoglayout.findViewById(R.id.timeTxt);
                TextView note = dialoglayout.findViewById(R.id.noteTxt);
//                ImageView bill = dialoglayout.findViewById(R.id.bilImg);
                amount.setText("Rs." +todysTrns.get(position).getAmount());
                date.setText(todysTrns.get(position).getDate());
                time.setText(todysTrns.get(position).getTime());
                note.setText(todysTrns.get(position).getNote());

                if(todysTrns.get(position).getBill() != null){
                    File imgFile = new File(todysTrns.get(position).getBill());
                    System.out.println(imgFile.getAbsolutePath());
//                    Picasso.get().load(imgFile).into(bill);
                }

                builder.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
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
            lyt = itemView.findViewById(R.id.todaysTransLyt);
        }
    }

}
