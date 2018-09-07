package chidhu.opencredit.tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import chidhu.opencredit.InventoryItems;
import chidhu.opencredit.R;

/**
 * Author   : Chidambaram P G
 * Date     : 20-07-2018
 */
public class ObjectAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private ArrayList<InventoryItems> originalList;
    private ArrayList<InventoryItems> suggestions = new ArrayList<>();
    private Filter filter = new CustomFilter();

    /**
     * @param context      Context
     * @param originalList Original list used to compare in constraints.
     */
    public ObjectAdapter(Context context, ArrayList<InventoryItems> originalList) {
        this.context = context;
        this.originalList = originalList;
    }

    @Override
    public int getCount() {
        return suggestions.size(); // Return the size of the suggestions list.
    }

    @Override
    public InventoryItems getItem(int position) {
        return suggestions.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try{
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.adapter_autotext,parent,false);
            }
            TextView autoText = convertView.findViewById(R.id.autoText);
            autoText.setText(suggestions.get(position).getProdDesc());
        }catch (Exception e){
            e.printStackTrace();
        }

        return convertView;
    }


    @Override
    public Filter getFilter() {
        return filter;
    }

    private class CustomFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            suggestions.clear();

            if (originalList != null && constraint != null) { // Check if the Original List and Constraint aren't null.
                for (int i = 0; i < originalList.size(); i++) {
                    if (originalList.get(i).getProdDesc().toLowerCase().contains(constraint)) { // Compare item in original list if it contains constraints.
                        suggestions.add(originalList.get(i)); // If TRUE add item in Suggestions.
                    }
                }
            }
            FilterResults results = new FilterResults(); // Create new Filter Results and return this to publishResults;
            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
