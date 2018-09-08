package chidhu.opencredit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        holder.name.setText(itemsList.get(position).getProdDesc());
        holder.hsn.setText(itemsList.get(position).getProdHSN());
        holder.price.setText("\u20B9" + itemsList.get(position).getProdSelPric());
        holder.qty.setText(itemsList.get(position).getProdQty() + " " + itemsList.get(position).getProdUnit());
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

    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name,hsn,price,qty;
        ImageView delt;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.itemNameTxt);
            hsn = itemView.findViewById(R.id.itemHSNTxt);
            price = itemView.findViewById(R.id.itemPriceTxt);
            qty  = itemView.findViewById(R.id.itemQtyTxt);
            delt = itemView.findViewById(R.id.delBtn);
        }
    }
}
