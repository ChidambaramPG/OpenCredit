package chidhu.opencredit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;

/**
 * Author   : Chidambaram P G
 * Date     : 21-07-2018
 */
public class AddedItemsAdapter extends RecyclerView.Adapter<AddedItemsAdapter.MyViewHolder>  {

    ArrayList<BillingItems> itemsList = new ArrayList<>();
    Context ctx;
    BillingItems item;

    public AddedItemsAdapter(ArrayList<BillingItems> itemsList, Context ctx) {
        this.itemsList = itemsList;
        this.ctx = ctx;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.billing_items_list_recycler_lyt,parent,false);
        return new AddedItemsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        item = itemsList.get(position);
        holder.prodDesc.setText(item.getItemName());
        holder.prodQty.setText(item.getItemQty());
        holder.prodPric.setText("\u20B9" + item.getItemPrice());
        holder.slNo.setText(String.valueOf(position+1));
        holder.nme.setText(item.getItemName());
        holder.price.setText("\u20B9" + item.getItmUnitPrice());
        holder.tax.setText(item.getItmTax());
        holder.discount.setText(item.getItmDiscount());
        holder.totAmnt.setText("\u20B9"+(Float.parseFloat(item.getItmUnitPrice()) * Float.parseFloat(item.getItemQty())));

        holder.delt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemsList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,itemsList.size());
                Toast.makeText(ctx, "Removed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView prodDesc,prodQty,prodPric,slNo,nme,price,tax,discount,totAmnt;

        RelativeLayout itmLyt,expandableLayout;
        boolean expanded = false;
        ImageView delt;

        public MyViewHolder(View itemView) {
            super(itemView);
            prodDesc = itemView.findViewById(R.id.nmeTxt);
            prodQty = itemView.findViewById(R.id.quantityTxt);
            prodPric = itemView.findViewById(R.id.priceTxt);
            totAmnt = itemView.findViewById(R.id.totAmntTxt);
            expandableLayout = itemView.findViewById(R.id.expandable_layout);
            itmLyt = itemView.findViewById(R.id.item_lyt);

            slNo = itemView.findViewById(R.id.slNoTxt);
            nme = itemView.findViewById(R.id.itmNameExpTxt);
            price = itemView.findViewById(R.id.itemIndPricExpTxt);
            tax = itemView.findViewById(R.id.itmTaxExpTxt2);
            discount = itemView.findViewById(R.id.discountExpTxt2);

            delt = itemView.findViewById(R.id.delItemBtn);


        }
    }
}
