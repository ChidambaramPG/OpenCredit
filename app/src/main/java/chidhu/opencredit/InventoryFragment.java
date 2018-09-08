package chidhu.opencredit;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InventoryFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    FloatingActionButton add;

    RecyclerView inventoryList;
    RecyclerView.Adapter adapter;
    List<InventoryItems> items = new ArrayList<>();

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    String prodDescStr = "",prodHSNStr = "",prodItemStr = "",prodCessStr = "",prodPurPricStr = "",prodSelPricStr = "",prodDiscntStr = "",prodQtyStr = "",prodItemNoteStr = "",prodTypeStr = "",prodGSTStr = "",prodUnitStr = "";


    public InventoryFragment() {
        // Required empty public constructor
    }

    public static InventoryFragment newInstance() {
        InventoryFragment fragment = new InventoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbRef.child("INVENTORY").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snap1:dataSnapshot.getChildren()){
                    Map<String, Object> val = (Map<String, Object>) snap1.getValue();

                    prodDescStr = (String) val.get("prodDesc");
                    prodHSNStr = (String) val.get("prodHSN");
                    prodItemStr = (String) val.get("prodItem");
                    prodCessStr = (String) val.get("prodCess");
                    prodPurPricStr = (String) val.get("prodPurPric");
                    prodSelPricStr = (String) val.get("prodSelPric");
                    prodDiscntStr = (String) val.get("prodDiscnt");
                    prodQtyStr = (String) val.get("prodQty");
                    prodItemNoteStr = (String) val.get("prodItemNote");
                    prodTypeStr = (String) val.get("prodType");
                    prodGSTStr = (String) val.get("prodGST");
                    prodUnitStr = (String) val.get("prodUnit");

                    System.out.println(prodDescStr + " :"+prodHSNStr + " :"+prodItemStr + " :"+prodCessStr + " :"+prodPurPricStr + " :"+prodSelPricStr + " :"+prodDiscntStr + " :"+prodQtyStr + " :"+prodItemNoteStr + " :"+prodTypeStr + " :"+prodGSTStr + " :"+prodUnitStr);
                    items.add(new InventoryItems(prodDescStr,prodHSNStr,prodItemStr,prodCessStr,prodPurPricStr,prodSelPricStr,prodDiscntStr,prodQtyStr,prodItemNoteStr,prodTypeStr,prodGSTStr,prodUnitStr));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);
        add = view.findViewById(R.id.addItem);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(),AddInventoryActivity.class);
                startActivity(i);
            }
        });
        inventoryList = view.findViewById(R.id.inventoryRecycler);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        inventoryList.setLayoutManager(mLayoutManager);
        adapter = new InventoryItemsAdapter(items,getContext());
        inventoryList.setAdapter(adapter);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
