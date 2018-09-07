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
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import chidhu.opencredit.databaseclasses.Customers;
import chidhu.opencredit.databaseclasses.OpenCreditDatabase;

/**
 * Author   : Chidambaram P G
 * Date     : 29-04-2018
 */

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.MyViewHolder>{
    List<Customers> todysTrns;
    Context ctx;
    AlertDialog alertDialog;
    AlertDialog.Builder builder;
    Date c;


    public CustomerAdapter(List<Customers> todysTrns, Context ctx) {
        this.todysTrns = todysTrns;
        this.ctx = ctx;
    }

    @Override
    public CustomerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custimers_layout,parent,false);
        return new CustomerAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomerAdapter.MyViewHolder holder, final int position) {

        final OpenCreditDatabase opDB = Room.databaseBuilder(ctx,OpenCreditDatabase.class,"OpenCreditDB")
                .fallbackToDestructiveMigration().allowMainThreadQueries().build();
        holder.nme.setText(todysTrns.get(position).getName());
        holder.amt.setText(todysTrns.get(position).getVerified());
        holder.type.setText(todysTrns.get(position).getNumber());
        holder.dt.setText(todysTrns.get(position).getJoindOn());

        holder.lyt.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {

                Intent prof = new Intent(ctx,CustomerProfileActivity.class);
                prof.putExtra("name",todysTrns.get(position).getName());
                prof.putExtra("number",todysTrns.get(position).getNumber());
                ctx.startActivity(prof);
                Toast.makeText(ctx, "Opening customer profile", Toast.LENGTH_SHORT).show();

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
