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
import java.util.Date;
import java.util.List;
import java.util.Map;

import chidhu.opencredit.databaseclasses.OpenCreditDatabase;


public class ThisMonthTransactionFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    RecyclerView todaysCreditList;
    RecyclerView.Adapter adapter;
    public OpenCreditDatabase opDB;
    List<Transaction> thisMonthTransactions = new ArrayList<>();
    Date c;
    int credit=0, debit = 0, bal = 0;
    TextView creditTxt, debitTxt, balTxt;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    List<String> customerList = new ArrayList<>();

    public ThisMonthTransactionFragment() {
        // Required empty public constructor
    }

    public static ThisMonthTransactionFragment newInstance() {
        ThisMonthTransactionFragment fragment = new ThisMonthTransactionFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        c = Calendar.getInstance().getTime();
        SimpleDateFormat trnsDt = new SimpleDateFormat("dd-MMM-yyyy");
        final String formattedTrnsDate = trnsDt.format(c);
        SimpleDateFormat trnsTi = new SimpleDateFormat("hh-mm");
        String formattedTrnsTime = trnsTi.format(c);
        SimpleDateFormat trnsMonth = new SimpleDateFormat("MMM");
        final String formattedTrnsMonth = trnsMonth.format(c);
        SimpleDateFormat trnsYear = new SimpleDateFormat("yyyy");
        final String formattedTrnsYear = trnsYear.format(c);

        dbRef.child("CUSTOMER_LIST").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap:dataSnapshot.getChildren()){
                    customerList.add(snap.getKey());
                }
                for(String cust:customerList) {
                    dbRef.child("TRANSACTIONS").child(user.getUid()).child(cust).child(formattedTrnsYear)
                            .child(formattedTrnsMonth).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            System.out.println("this month transactions");
                            for(DataSnapshot snap: dataSnapshot.getChildren()){

                                for(DataSnapshot snap1: snap.getChildren()){

                                    Map<String, Object> val = (Map<String, Object>) snap1.getValue();
                                    String date = (String) val.get("date");
                                    String time = (String) val.get("time");
                                    String amount = (String) val.get("amount");
                                    String transType = (String) val.get("transType");
                                    String uname = (String) val.get("uname");
                                    String number = (String) val.get("number");
                                    String month = (String) val.get("month");
                                    String year = (String) val.get("year");
                                    String note = (String) val.get("note");
                                    String bill = (String) val.get("bill");
                                    String notified = (String) val.get("notified");

                                    thisMonthTransactions.add(new Transaction(date,time,amount,transType,uname,number,month,year,note,bill,notified));
                                    System.out.println(transType);
                                    credit += Integer.valueOf(amount);
                                }
                            }
                            adapter.notifyDataSetChanged();
                            creditTxt.setText(String.valueOf(credit));
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_this_month_transaction, container, false);
        creditTxt = view.findViewById(R.id.creditTxtLabl);

        todaysCreditList = view.findViewById(R.id.todaysTransactionList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        todaysCreditList.setLayoutManager(mLayoutManager);
        adapter = new ThisMonthTransactionAdapter(thisMonthTransactions,getContext());
        todaysCreditList.setAdapter(adapter);


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
