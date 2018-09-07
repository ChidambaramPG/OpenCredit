package chidhu.opencredit;

import android.arch.persistence.room.Room;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import chidhu.opencredit.databaseclasses.Customers;
import chidhu.opencredit.databaseclasses.OpenCreditDatabase;
import chidhu.opencredit.databaseclasses.Transactions;


public class CustomersFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    RecyclerView todaysCreditList;
    RecyclerView.Adapter adapter;
    public OpenCreditDatabase opDB;
    List<Customers> allCustomers;
    List<CustomerTransaction> csts = new ArrayList<>();
    Date c;
    int credit=0, debit = 0, bal = 0;

    public CustomersFragment() {
        // Required empty public constructor
    }

    public static CustomersFragment newInstance() {
        CustomersFragment fragment = new CustomersFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity())
                .setActionBarTitle("CUSTOMERS");

        opDB = Room.databaseBuilder(getContext(),OpenCreditDatabase.class,"OpenCreditDB")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();

        c = Calendar.getInstance().getTime();
        SimpleDateFormat trnsDt = new SimpleDateFormat("dd-MMM-yyyy");
        final String formattedTrnsDate = trnsDt.format(c);

        System.out.println("credit:"+credit);

        allCustomers = opDB.openCreditDAO().getUsers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_customers, container, false);

        todaysCreditList = view.findViewById(R.id.todaysTransactionList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        todaysCreditList.setLayoutManager(mLayoutManager);
        adapter = new CustomerAdapter(allCustomers,getContext());
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
