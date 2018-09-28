package chidhu.opencredit;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.wang.avi.AVLoadingIndicatorView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import chidhu.opencredit.databaseclasses.Customers;
import chidhu.opencredit.databaseclasses.OpenCreditDatabase;
import chidhu.opencredit.databaseclasses.Transactions;

public class TotalCreditFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    RecyclerView thisMonthCreditList;
    RecyclerView.Adapter adapter;
    public OpenCreditDatabase opDB;
    List<Transactions> thisMonthTransactions;
    List<Customers> allCustomers;
    List<CustomerTransaction> csts = new ArrayList<>();
    List<CustomerTransaction> tempCsts = new ArrayList<>();
    List<CustomerTransaction> tempTrans = new ArrayList<>();
    Date c;
    int credit=0, debit = 0, bal = 0;
    TextView creditTxt, debitTxt, balTxt;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    List<Customer> customerList = new ArrayList<>();

    AVLoadingIndicatorView avi;

    public TotalCreditFragment() {
        // Required empty public constructor
    }


    public static TotalCreditFragment newInstance() {
        TotalCreditFragment fragment = new TotalCreditFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("total credit");

        c = Calendar.getInstance().getTime();
        SimpleDateFormat trnsDt = new SimpleDateFormat("dd-MMM-yyyy");
        final String formattedTrnsDate = trnsDt.format(c);
        SimpleDateFormat trnsTi = new SimpleDateFormat("hh-mm");
        String formattedTrnsTime = trnsTi.format(c);
        SimpleDateFormat trnsMonth = new SimpleDateFormat("MMM");
        final String formattedTrnsMonth = trnsMonth.format(c);


        String[] Months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        String lstMonth = formattedTrnsMonth;

        for(int mn=0;mn<Months.length;mn++){
            if(formattedTrnsMonth.equals("Jan")){
                lstMonth = "Dec";
            }else if(formattedTrnsMonth.equals(Months[mn])){
                lstMonth = Months[mn-1];
            }
        }

        fetchTransactions();

    }

    private void fetchTransactions() {

        bal = 0;

        SimpleDateFormat trnsYear = new SimpleDateFormat("yyyy");
        final String formattedTrnsYear = trnsYear.format(c);

        dbRef.child("CUSTOMER_LIST").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap:dataSnapshot.getChildren()){
                    customerList.add(new Customer(snap.getValue().toString(),snap.getKey()));
                }
                for(final Customer cust:customerList){
                    dbRef.child("TRANSACTIONS").child(user.getUid()).child(cust.getNumber()).child(formattedTrnsYear)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String num = null,name  = null,transType  = null,amount  = null;
                                    int custTotCredit = 0,custTotDebit = 0;
                                    int numsTrans = 0;
                                    for(DataSnapshot snap:dataSnapshot.getChildren()){
                                        for(DataSnapshot snap1: snap.getChildren()){
                                            for(DataSnapshot snap2: snap1.getChildren()){

                                                Map<String, Object> val = (Map<String, Object>) snap2.getValue();
                                                num = (String) val.get("number");
                                                name = (String) val.get("uname");
                                                transType = (String) val.get("transType");
                                                amount = (String) val.get("amount");

                                                if(transType.equals("credit")){
                                                    custTotCredit += Float.valueOf(amount);
                                                    credit += Float.valueOf(amount);
                                                }else if(transType.equals("debit")){
                                                    custTotDebit += Float.valueOf(amount);
                                                    debit += Float.valueOf(amount);
                                                }

                                                numsTrans++;

//                                        System.out.println("num :" + num + "name :" + name + "transType :" + transType + "amount :" + amount);

                                            }
                                        }
                                    }

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
                                    if(adapter!= null){
                                        adapter.notifyDataSetChanged();
                                    }

                                    bal =  credit - debit;

                                    try {
                                        balTxt.setText("\u20B9" + String.valueOf(bal));
                                        avi.hide();
                                    }catch(Exception e){
                                        System.out.println(e);
                                    }

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
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_total_credit, container, false);


        balTxt = view.findViewById(R.id.balTxt2);
        avi = view.findViewById(R.id.avProg);
        avi.show();

        thisMonthCreditList = view.findViewById(R.id.thisMonthTransactionList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        thisMonthCreditList.setLayoutManager(mLayoutManager);
        adapter = new TotalCreditAdapter(csts,getContext());
        thisMonthCreditList.setAdapter(adapter);

        balTxt.setText("\u20B9"+String.valueOf(bal));

        return view;
    }

    public void onButtonPressed(Uri uri) {

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.search_option_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =(SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        System.out.println(searchManager);
        System.out.println(searchView);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                System.out.println(query);
                return false;
            }


            @Override
            public boolean onQueryTextChange(final String newText) {
                System.out.println(newText);

                if(newText.isEmpty()){
                    adapter = new TotalCreditAdapter(csts,getContext());
                    thisMonthCreditList.setAdapter(adapter);

                }else{
                    tempTrans.clear();

                    for(CustomerTransaction trans:csts){

                        System.out.println(trans.getName()+ " : " + trans.getTotAmount());

                        if(trans.getName().contains(newText)){
                            tempTrans.add(trans);
                        }

                    }

                    adapter = new TotalCreditAdapter(tempTrans,getContext());
                    thisMonthCreditList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    System.out.println("Temp Trans List size :" + tempTrans.size());

                }



                return true;
            }


        });

        return;

    }

}
