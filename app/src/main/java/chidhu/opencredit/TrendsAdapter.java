package chidhu.opencredit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Author   : Chidambaram P G
 * Date     : 19-05-2018
 */

public class TrendsAdapter extends RecyclerView.Adapter<TrendsAdapter.MyViewHolder> {

    ArrayList<Integer> cre = new ArrayList<>();
    ArrayList<Integer> deb  = new ArrayList<>();
    ArrayList<Integer> bal  = new ArrayList<>();
    Context ctx;
    String[] months;

    public TrendsAdapter(ArrayList<Integer> cre, ArrayList<Integer> deb, ArrayList<Integer> bal, String[] months, Context ctx) {
        this.cre = cre;
        this.deb = deb;
        this.bal = bal;
        this.ctx = ctx;
        this.months = months;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.monthly_transaction_lyt,parent,false);
        return new TrendsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.mont.setText(months[position]);
        holder.year.setText("2018");
        holder.cred.setText(cre.get(position).toString());
        holder.debt.setText(deb.get(position).toString());
        holder.baln.setText("Rs." + bal.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return cre.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mont,year,cred,debt,baln;
        public MyViewHolder(View itemView) {
            super(itemView);
            mont= itemView.findViewById(R.id.monthTxt);
            year= itemView.findViewById(R.id.yearTxt);
            cred= itemView.findViewById(R.id.creditTxtLabl);
            debt= itemView.findViewById(R.id.debitTxt);
            baln= itemView.findViewById(R.id.balTxt);

        }
    }
}
