package chidhu.opencredit;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import chidhu.opencredit.databaseclasses.OpenCreditDatabase;

public class TodayCreditFragment extends Fragment{

    private OnFragmentInteractionListener mListener;
    RecyclerView todaysCreditList;
    RecyclerView.Adapter adapter;
    public OpenCreditDatabase opDB;
//    List<Transactions> todaysTransactions;
//    List<Customers> allCustomers;
    List<CustomerTransaction> csts = new ArrayList<>();
    Date c;
    int credit=0, debit = 0, bal = 0;
    TextView creditTxt, debitTxt, balTxt;


    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    List<Customer> customerList = new ArrayList<>();

    public TodayCreditFragment() {
        // Required empty public constructor
    }

    public static TodayCreditFragment newInstance() {
        TodayCreditFragment fragment = new TodayCreditFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("onCreate");


        dbRef.child("CUSTOMER_LIST").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap:dataSnapshot.getChildren()){
                    customerList.add(new Customer(snap.getValue().toString(),snap.getKey()));
                }
                c = Calendar.getInstance().getTime();
                SimpleDateFormat trnsDt = new SimpleDateFormat("dd-MMM-yyyy");
                final String formattedTrnsDate = trnsDt.format(c);
                SimpleDateFormat trnsTi = new SimpleDateFormat("hh-mm");
                String formattedTrnsTime = trnsTi.format(c);
                SimpleDateFormat trnsMonth = new SimpleDateFormat("MMM");
                final String formattedTrnsMonth = trnsMonth.format(c);
                SimpleDateFormat trnsYear = new SimpleDateFormat("yyyy");
                final String formattedTrnsYear = trnsYear.format(c);

                for(final Customer cust:customerList){
                    DatabaseReference custRef = dbRef.child("TRANSACTIONS").child(user.getUid()).child(cust.getNumber()).child(formattedTrnsYear)
                            .child(formattedTrnsMonth).child(formattedTrnsDate);

                    custRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String num = null,name  = null,transType  = null,amount  = null;
                                    int custTotCredit = 0,custTotDebit = 0;
                                    int numsTrans = 0;

                                    for(DataSnapshot snap:dataSnapshot.getChildren()){

                                        Map<String, Object> val = (Map<String, Object>) snap.getValue();
                                        num = (String) val.get("number");
                                        name = (String) val.get("uname");
                                        amount = (String) val.get("amount");
                                        transType = (String) val.get("transType");
                                        if(transType.equals("credit")){
                                            custTotCredit += Float.valueOf(amount);
                                            credit += Float.valueOf(amount);
                                        }else if(transType.equals("debit")){
                                            custTotDebit += Float.valueOf(amount);
                                            debit += Float.valueOf(amount);
                                        }

                                        numsTrans ++;
                                    }
                                    int cstCount =0;
                                    if(name == null){
                                        csts.add(new CustomerTransaction(cust.getNumber(),cust.getName(),String.valueOf(0),String.valueOf(0),0));
                                    }else{
                                        csts.add(new CustomerTransaction(num,name,String.valueOf(custTotCredit),String.valueOf(custTotDebit),numsTrans));
                                    }

                                    Collections.sort(csts, new Comparator<CustomerTransaction>() {
                                        @Override
                                        public int compare(CustomerTransaction c0, CustomerTransaction c1) {
                                            return c0.getName().compareToIgnoreCase(c1.getName());
                                        }
                                    });

                                    if(adapter != null){
                                        adapter.notifyDataSetChanged();
                                    }

                                    bal = credit - debit;

                                    try {
                                        balTxt.setText("\u20B9" + String.valueOf(bal));
                                    }catch(Exception e){
//                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

//                                    System.out.println("modified count: "+cstCount);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }

                            });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        System.out.println("onCreateView");
        View view = inflater.inflate(R.layout.fragment_today_credit, container, false);

        balTxt = view.findViewById(R.id.balTxt2);
        todaysCreditList = view.findViewById(R.id.todaysTransactionList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        todaysCreditList.setLayoutManager(mLayoutManager);
        adapter = new TodaysCreditAdapter(csts,getContext());
        todaysCreditList.setAdapter(adapter);
        balTxt.setText("\u20B9"+String.valueOf(bal));

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
        System.out.println("onAttach");
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
        System.out.println("onDetach");
    }

//    @Override
//    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
//        if (viewHolder instanceof TodaysCreditAdapter.MyViewHolder) {
//            Toast.makeText(getContext(), "Swiped :" + csts.get(viewHolder.getAdapterPosition()).getNumber(), Toast.LENGTH_SHORT).show();
//        }
//    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("onStart");

    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("onResume");

    }
}
