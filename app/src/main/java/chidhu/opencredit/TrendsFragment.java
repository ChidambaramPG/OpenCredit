package chidhu.opencredit;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import chidhu.opencredit.databaseclasses.OpenCreditDatabase;
import chidhu.opencredit.databaseclasses.Transactions;

public class TrendsFragment extends Fragment {

    List<Transactions> trnsctns;
    public OpenCreditDatabase opDB;
    Date c;
    ArrayList<Integer> credits = new ArrayList<>();
    ArrayList<Integer> debits  = new ArrayList<>();
    ArrayList<Integer> balance  = new ArrayList<>();
    RecyclerView mothlyTrans;
    RecyclerView.Adapter adapter;
    List<Customer> customerList = new ArrayList<>();

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();


    private OnFragmentInteractionListener mListener;

    public TrendsFragment() {
        // Required empty public constructor
    }

    public static TrendsFragment newInstance() {
        TrendsFragment fragment = new TrendsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MainActivity) getActivity())
                .setActionBarTitle("TRENDS");

        opDB = Room.databaseBuilder(getContext(),OpenCreditDatabase.class,"OpenCreditDB")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();


        c = Calendar.getInstance().getTime();
        SimpleDateFormat trnsMonth = new SimpleDateFormat("MMM");
        String formattedTrnsMonth = trnsMonth.format(c);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_trends, container, false);

        final String[] Months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

        final BarChart chart = view.findViewById(R.id.chart);
        chart.setNoDataText("NO ENTRIES YET!");
        Description desc = new Description();
        desc.setText("Monthly sales chart");
        chart.setDescription(desc);
        final List<BarEntry> creditEntries = new ArrayList<>();
        final List<BarEntry> debitEntries = new ArrayList<>();
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
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {

                        customerList.add(new Customer(snap.getValue().toString(), snap.getKey()));
                    }

                    final int[] count = {0};
                    final int[] credit = new int[12];
                    final int[] debit = new int[12];

                    for (final Customer cust : customerList) {
                        for(int i=0;i<12;i++) {


                            DatabaseReference monRef = dbRef.child("TRANSACTIONS").child(user.getUid())
                                    .child(cust.getNumber()).child(formattedTrnsYear).child(Months[i]);

                            monRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.getKey().equals("Jan")) {
                                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                            for (DataSnapshot snap1 : snap.getChildren()) {
                                                Map<String, Object> val = (Map<String, Object>) snap1.getValue();
                                                String num = (String) val.get("number");
                                                String name = (String) val.get("uname");
                                                String transType = (String) val.get("transType");
                                                String amount = (String) val.get("amount");
                                                System.out.println(num + " : " + name + " : " + transType + " : " + amount);
                                                if(transType.equals("credit")){
                                                    credit[0] += Integer.valueOf(amount);
                                                }else{
                                                    debit[0] += Integer.valueOf(amount);

                                                }
                                            }
                                        }
                                    }
                                    if (dataSnapshot.getKey().equals("Feb")) {
                                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                            for (DataSnapshot snap1 : snap.getChildren()) {
                                                Map<String, Object> val = (Map<String, Object>) snap1.getValue();
                                                String num = (String) val.get("number");
                                                String name = (String) val.get("uname");
                                                String transType = (String) val.get("transType");
                                                String amount = (String) val.get("amount");
                                                System.out.println(num + " : " + name + " : " + transType + " : " + amount);
                                                if(transType.equals("credit")){
                                                    credit[1] += Integer.valueOf(amount);
                                                }else{
                                                    debit[1] += Integer.valueOf(amount);
                                                }
                                            }
                                        }
                                    }
                                    if (dataSnapshot.getKey().equals("Mar")) {
                                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                            for (DataSnapshot snap1 : snap.getChildren()) {
                                                Map<String, Object> val = (Map<String, Object>) snap1.getValue();
                                                String num = (String) val.get("number");
                                                String name = (String) val.get("uname");
                                                String transType = (String) val.get("transType");
                                                String amount = (String) val.get("amount");
                                                System.out.println(num + " : " + name + " : " + transType + " : " + amount);
                                                if(transType.equals("credit")){
                                                    credit[2] += Integer.valueOf(amount);
                                                }else{
                                                    debit[2] += Integer.valueOf(amount);
                                                }
                                            }
                                        }
                                    }
                                    if (dataSnapshot.getKey().equals("Apr")) {
                                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                            for (DataSnapshot snap1 : snap.getChildren()) {
                                                Map<String, Object> val = (Map<String, Object>) snap1.getValue();
                                                String num = (String) val.get("number");
                                                String name = (String) val.get("uname");
                                                String transType = (String) val.get("transType");
                                                String amount = (String) val.get("amount");
                                                System.out.println(num + " : " + name + " : " + transType + " : " + amount);
                                                if(transType.equals("credit")){
                                                    credit[3] += Integer.valueOf(amount);
                                                }else{
                                                    debit[3] += Integer.valueOf(amount);
                                                }
                                            }
                                        }
                                    }
                                    if (dataSnapshot.getKey().equals("May")) {
                                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                            for (DataSnapshot snap1 : snap.getChildren()) {
                                                Map<String, Object> val = (Map<String, Object>) snap1.getValue();
                                                String num = (String) val.get("number");
                                                String name = (String) val.get("uname");
                                                String transType = (String) val.get("transType");
                                                String amount = (String) val.get("amount");
                                                System.out.println(num + " : " + name + " : " + transType + " : " + amount);
                                                if(transType.equals("credit")){
                                                    credit[4] += Integer.valueOf(amount);
                                                }else{
                                                    debit[4] += Integer.valueOf(amount);
                                                }
                                            }
                                        }
                                    }
                                    if (dataSnapshot.getKey().equals("Jun")) {
                                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                            for (DataSnapshot snap1 : snap.getChildren()) {
                                                Map<String, Object> val = (Map<String, Object>) snap1.getValue();
                                                String num = (String) val.get("number");
                                                String name = (String) val.get("uname");
                                                String transType = (String) val.get("transType");
                                                String amount = (String) val.get("amount");
                                                System.out.println(num + " : " + name + " : " + transType + " : " + amount);
                                                if(transType.equals("credit")){
                                                    credit[5] += Integer.valueOf(amount);
                                                }else{
                                                    debit[5] += Integer.valueOf(amount);
                                                }
                                            }
                                        }
                                    }
                                    if (dataSnapshot.getKey().equals("Jul")) {
                                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                            for (DataSnapshot snap1 : snap.getChildren()) {
                                                Map<String, Object> val = (Map<String, Object>) snap1.getValue();
                                                String num = (String) val.get("number");
                                                String name = (String) val.get("uname");
                                                String transType = (String) val.get("transType");
                                                String amount = (String) val.get("amount");
                                                System.out.println(num + " : " + name + " : " + transType + " : " + amount);
                                                if(transType.equals("credit")){
                                                    credit[6] += Integer.valueOf(amount);
                                                }else{
                                                    debit[6] += Integer.valueOf(amount);
                                                }
                                            }
                                        }
                                    }
                                    if (dataSnapshot.getKey().equals("Aug")) {
                                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                            for (DataSnapshot snap1 : snap.getChildren()) {
                                                Map<String, Object> val = (Map<String, Object>) snap1.getValue();
                                                String num = (String) val.get("number");
                                                String name = (String) val.get("uname");
                                                String transType = (String) val.get("transType");
                                                String amount = (String) val.get("amount");
                                                System.out.println(num + " : " + name + " : " + transType + " : " + amount);
                                                if(transType.equals("credit")){
                                                    credit[7] += Float.valueOf(amount);
                                                }else{
                                                    debit[7] += Float.valueOf(amount);
                                                }
                                            }
                                        }
                                    }
                                    if (dataSnapshot.getKey().equals("Sep")) {
                                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                            for (DataSnapshot snap1 : snap.getChildren()) {
                                                Map<String, Object> val = (Map<String, Object>) snap1.getValue();
                                                String num = (String) val.get("number");
                                                String name = (String) val.get("uname");
                                                String transType = (String) val.get("transType");
                                                String amount = (String) val.get("amount");
                                                System.out.println(num + " : " + name + " : " + transType + " : " + amount);
                                                if(transType.equals("credit")){
                                                    credit[8] += Float.valueOf(amount);
                                                }else{
                                                    debit[8] += Float.valueOf(amount);
                                                }
                                            }
                                        }
                                    }
                                    if (dataSnapshot.getKey().equals("Oct")) {
                                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                            for (DataSnapshot snap1 : snap.getChildren()) {
                                                Map<String, Object> val = (Map<String, Object>) snap1.getValue();
                                                String num = (String) val.get("number");
                                                String name = (String) val.get("uname");
                                                String transType = (String) val.get("transType");
                                                String amount = (String) val.get("amount");
                                                System.out.println(num + " : " + name + " : " + transType + " : " + amount);
                                                if(transType.equals("credit")){
                                                    credit[9] += Float.valueOf(amount);
                                                }else{
                                                    debit[9] += Float.valueOf(amount);
                                                }
                                            }
                                        }
                                    }
                                    if (dataSnapshot.getKey().equals("Nov")) {
                                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                            for (DataSnapshot snap1 : snap.getChildren()) {
                                                Map<String, Object> val = (Map<String, Object>) snap1.getValue();
                                                String num = (String) val.get("number");
                                                String name = (String) val.get("uname");
                                                String transType = (String) val.get("transType");
                                                String amount = (String) val.get("amount");
                                                System.out.println(num + " : " + name + " : " + transType + " : " + amount);
                                                if(transType.equals("credit")){
                                                    credit[10] += Integer.valueOf(amount);
                                                }else{
                                                    debit[10] += Integer.valueOf(amount);
                                                }
                                            }
                                        }
                                    }
                                    if (dataSnapshot.getKey().equals("Dec")) {
                                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                            for (DataSnapshot snap1 : snap.getChildren()) {
                                                Map<String, Object> val = (Map<String, Object>) snap1.getValue();
                                                String num = (String) val.get("number");
                                                String name = (String) val.get("uname");
                                                String transType = (String) val.get("transType");
                                                String amount = (String) val.get("amount");
                                                System.out.println(num + " : " + name + " : " + transType + " : " + amount);
                                                if(transType.equals("credit")){
                                                    credit[11] += Integer.valueOf(amount);
                                                }else{
                                                    debit[11] += Integer.valueOf(amount);
                                                }
                                            }
                                        }
                                    }
                                    System.out.println(credit[5]);
                                    count[0]++;
                                    if(count[0] == (customerList.size()*12)){
                                        System.out.println("End of execution");

                                        for(int k=0;k<credit.length;k++){
                                            creditEntries.add(new BarEntry(k,credit[k]));
                                            debitEntries.add(new BarEntry(k,debit[k]));
                                            credits.add(credit[k]);
                                            debits.add(debit[k]);
                                            balance.add(credit[k]-debit[k]);
                                            System.out.println("k :" + k + " , credit:"+credit[k] + " , debit:"+debit[k]);
                                        }
                                        BarDataSet creditSet = new BarDataSet(creditEntries, "CREDIT");
                                        creditSet.setColor(getResources().getColor(R.color.colorCredit));
                                        BarDataSet debitSet = new BarDataSet(debitEntries, "DEBIT");
                                        debitSet.setColor(getResources().getColor(R.color.colorDebit));

                                        float groupSpace = 0.06f;
                                        float barSpace = 0.02f;
                                        float barWidth = 0.45f;

                                        BarData data = new BarData(creditSet, debitSet);
                                        data.setBarWidth(barWidth);
                                        chart.setData(data);
                                        chart.groupBars(0, groupSpace, barSpace);

                                        YAxis left = chart.getAxisLeft();
                                        left.setDrawLabels(false);
                                        left.setDrawZeroLine(true);

                                        YAxis yAxis = chart.getAxisRight();
                                        yAxis.setValueFormatter(new LargeValueFormatter());

                                        XAxis xAxis = chart.getXAxis();
                                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                        xAxis.setDrawGridLines(false);
                                        xAxis.setValueFormatter(new MyXAxisValueFormatter(Months));
                                        xAxis.setGranularity(1f);

                                        chart.invalidate();
                                        adapter.notifyDataSetChanged();

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        mothlyTrans = view.findViewById(R.id.monthlyTransRecycler);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mothlyTrans.setLayoutManager(mLayoutManager);
        adapter = new TrendsAdapter(credits,debits,balance,Months,getContext());
        mothlyTrans.setAdapter(adapter);
        return view;
    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter {

        private String[] mValues;

        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            return mValues[(int) value];
        }

        /** this is only needed if numbers are returned, else return 0 */
        public int getDecimalDigits() { return 0; }
    }

    public static int getMaxValue(ArrayList<Integer> array) {
        int maxValue = array.get(0);
        for (int i = 1; i < array.size(); i++) {
            if (array.get(i) > maxValue) {
                maxValue = array.get(i);
            }
        }
        return maxValue;
    }

    // getting the miniumum value
    public static int getMinValue(ArrayList<Integer> array) {
        int minValue = array.get(0);
        for (int i = 1; i < array.size(); i++) {
            if (array.get(i) < minValue) {
                minValue = array.get(i);
            }
        }
        return minValue;
    }



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
