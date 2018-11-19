package chidhu.opencredit;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.savvi.rangedatepicker.CalendarPickerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class AccountFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    Button signout,changePwd,print;
    FirebaseAuth mAuth;
    TextView emailID;
    Calendar myCalendar;

    AlertDialog dialog;
    AlertDialog.Builder builder;

    CalendarPickerView calendar;

    DatabaseReference dbRef;
    static FirebaseUser user;
    ArrayList<Transaction> transList;

    static Date billStatStrtDate = null;
    static Date billStatEndDate = null;

    private static float fontSmall =  7,fontBig = 10;
    private static String gap = "            ";


    public AccountFragment() {
        // Required empty public constructor
    }

    public static AccountFragment newInstance() {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        mAuth = FirebaseAuth.getInstance();
        myCalendar = Calendar.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();
        transList = new ArrayList<>();

        dbRef.child("TRANSACTIONS").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snap01:dataSnapshot.getChildren()){

                    for(DataSnapshot snap02:snap01.getChildren()){

                        for(DataSnapshot snap03:snap02.getChildren()){

                            for(DataSnapshot snap04:snap03.getChildren()){

                                for(DataSnapshot snap05:snap04.getChildren()){
//                                    System.out.println(snap05.getKey());

                                    Transaction temptrans = new Transaction();

                                        for(DataSnapshot snap06:snap05.getChildren()){
                                            if(snap06.getKey().equals("amount")){
                                                temptrans.setAmount(snap06.getValue().toString());
                                            }
                                            if(snap06.getKey().equals("bill")){
                                                temptrans.setBill(snap06.getValue().toString());
                                            }
                                            if(snap06.getKey().equals("date")){
                                                temptrans.setDate(snap06.getValue().toString());
                                            }
                                            if(snap06.getKey().equals("month")){
                                                temptrans.setMonth(snap06.getValue().toString());
                                            }
                                            if(snap06.getKey().equals("note")){
                                                temptrans.setNote(snap06.getValue().toString());
                                            }
                                            if(snap06.getKey().equals("notified")){
                                                temptrans.setNotified(snap06.getValue().toString());
                                            }
                                            if(snap06.getKey().equals("number")){
                                                temptrans.setNumber(snap06.getValue().toString());
                                            }
                                            if(snap06.getKey().equals("time")){
                                                temptrans.setTime(snap06.getValue().toString());
                                            }
                                            if(snap06.getKey().equals("transType")){
                                                temptrans.setTransType(snap06.getValue().toString());
                                            }
                                            if(snap06.getKey().equals("uname")){
                                                temptrans.setUname(snap06.getValue().toString());
                                            }
                                            if(snap06.getKey().equals("year")){
                                                temptrans.setYear(snap06.getValue().toString());
                                            }
                                            System.out.println(snap06.getValue());

                                        }
                                    transList.add(temptrans);
                                }
                            }
                        }
                    }
                }

                Collections.sort(transList, new Comparator<Transaction>(){

                    @Override
                    public int compare(Transaction t1, Transaction t2) {
                        final SimpleDateFormat calDt = new SimpleDateFormat("dd-MMM-yyyy hh-mm-ss");
                        Date d1 = null,d2=null;
                        try {
                            d1 = calDt.parse(t1.getDate() + " " + t1.getTime());
                            d2 = calDt.parse(t2.getDate() + " " + t2.getTime());

                            return d1.compareTo(d2);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        return 1;
                    }

                });

                print.setVisibility(View.VISIBLE);
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
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        emailID = view.findViewById(R.id.emailIdTxt);
        emailID.setText(mAuth.getCurrentUser().getEmail());
        changePwd = view.findViewById(R.id.chngPswdBtn);
        signout = view.findViewById(R.id.signoutBtn);
        print = view.findViewById(R.id.printBtn);
        print.setVisibility(View.GONE);

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent i = new Intent(getContext(),SplashScreenActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });

        changePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chngPswd = new Intent(getContext(),ChangePasswordActivity.class);
                startActivity(chngPswd);
            }
        });

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Select date", Toast.LENGTH_SHORT).show();
                builder = new AlertDialog.Builder(getActivity());
                dialog = builder.create();
                LayoutInflater inflater = dialog.getLayoutInflater();
                View dialoglayout = inflater.inflate(R.layout.date_selection_lyt, null);
                calendar = dialoglayout.findViewById(R.id.calendar_view);

                Calendar cal01 = Calendar.getInstance();
                System.out.println(transList.size());

                Date st = new Date(transList.get(0).getDate());
                Date en = new Date(transList.get(transList.size() -1).getDate());

                cal01.setTime(en);
                cal01.add(Calendar.DAY_OF_YEAR,1);

//                ArrayList<Date> selecteddates = new ArrayList<>();
//                selecteddates.add(st);
//                selecteddates.add(en);

                calendar.init(st,new Date(String.valueOf(cal01.getTime())))
                        .inMode(CalendarPickerView.SelectionMode.RANGE);

                builder.setPositiveButton("Print", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(), "Printing to pdf", Toast.LENGTH_SHORT).show();

                        System.out.println(calendar.getSelectedDates().get(0));
                        System.out.println(calendar.getSelectedDates().get(calendar.getSelectedDates().size()-1));

                        if(calendar.getSelectedDates().size()<2){
                            Toast.makeText(getContext(), "Select a start date and end date", Toast.LENGTH_SHORT).show();
                        }else {


                            Date strt = new Date(String.valueOf(calendar.getSelectedDates().get(0)));
                            Date end = new Date(String.valueOf(calendar.getSelectedDates().get(calendar.getSelectedDates().size() - 1)));
                            billStatStrtDate = strt;
                            billStatEndDate = end;

                            try {

                                File path = null;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO) {
                                    path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                                    File file = new File(path, "/" + user.getUid() + ".pdf");
                                    System.out.println("Writing to" + file.getAbsolutePath());
                                    Document document = new Document();
                                    PdfWriter.getInstance(document, new FileOutputStream(file));
                                    //
                                    Rectangle one = new Rectangle(216, 360);
                                    document.setPageSize(one);
                                    document.setMargins(20, 20, 5, 20);
                                    //
                                    document.open();
                                    addMetaData(document, transList);
                                    addTitlePage(document, transList);
                                    addContent(document, transList);
                                    addBottom(document, transList);

                                    document.close();
                                    Toast.makeText(getContext(), "File saved to your Documents folder", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    Uri uri = FileProvider.getUriForFile(getContext(),BuildConfig.APPLICATION_ID,file);
                                    intent.setDataAndType(uri, "application/pdf");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    startActivity(intent);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                    }
                });
                builder.setView(dialoglayout);
                builder.show();
            }
        });
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

    private static void addMetaData(Document document,ArrayList<Transaction> transaction) {
        System.out.println("inside metadata");
        document.addTitle("Report for "+ transaction.get(0).getUname());
        document.addSubject("Invoice from OpenCredit app");
        document.addKeywords("OpenCredit, Report, "+transaction.get(0).getUname());
        document.addAuthor("OpenCredit Inc");
        document.addCreator("chidambarampg@gmail.com");
    }



    private static void addTitlePage(Document document,ArrayList<Transaction> transaction)
            throws DocumentException {


        Font font5pt = new Font(Font.FontFamily.TIMES_ROMAN, fontBig);
        Font font2pt = new Font(Font.FontFamily.TIMES_ROMAN, fontSmall);

        Paragraph preface = new Paragraph();
        Paragraph p1= new Paragraph(" OPEN CREDIT ",font5pt);
        p1.setAlignment(Element.ALIGN_CENTER);
        preface.add(p1);

        Paragraph p5= new Paragraph("  ------------------------------------------------------------- ",font2pt);
        p5.setAlignment(Element.ALIGN_CENTER);
        preface.add(p5);

//        addEmptyLine(preface, 1);

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat trnsDt = new SimpleDateFormat("dd-MMM-yyyy");
        final String formattedTrnsDate = trnsDt.format(c);

        SimpleDateFormat trnsDt02 = new SimpleDateFormat("ddMMyyyyhhmm");
        final String formattedTrnsDate02 = trnsDt02.format(c);

        SimpleDateFormat trnsDt03 = new SimpleDateFormat("hh-mm-ss");
        final String formattedTrnsTime = trnsDt03.format(c);

        Chunk glue = new Chunk(new VerticalPositionMark());
        Paragraph p3 = new Paragraph("Seller : "+ user.getDisplayName() ,font2pt);
        p3.add(new Chunk(glue));
        p3.add("Report : REP" + formattedTrnsDate02);
        preface.add(p3);

        Chunk glue2 = new Chunk(new VerticalPositionMark());
        Paragraph p4 = new Paragraph("Date : " + formattedTrnsDate,font2pt);
        p4.add(new Chunk(glue2));
        p4.add("Time : " + formattedTrnsTime);
        preface.add(p4);



        Paragraph p6= new Paragraph("  ------------------------------------------------------------- ",font2pt);
        p6.setAlignment(Element.ALIGN_CENTER);
        preface.add(p6);

        document.add(preface);
    }

    private static void addContent(Document document,ArrayList<Transaction> transaction) throws DocumentException {



        Font font5pt = new Font(Font.FontFamily.TIMES_ROMAN, fontBig);
        Font font2pt = new Font(Font.FontFamily.TIMES_ROMAN, fontSmall);

        Paragraph subPara = new Paragraph();


        Paragraph p1= new Paragraph(" Item No"+gap+"Date"+gap+"User"+gap+"Type"+gap+"Amount ",font2pt);
        subPara.add(p1);
        createTable(subPara,transaction);

        document.add(subPara);

    }

    private static void createTable(Paragraph subCatPart,ArrayList<Transaction> transaction) {

        Font font5pt = new Font(Font.FontFamily.TIMES_ROMAN, fontBig);
        Font font2pt = new Font(Font.FontFamily.TIMES_ROMAN, fontSmall);

        SimpleDateFormat trnsDt = new SimpleDateFormat("dd-MMM-yyyy");

        Date strt = billStatStrtDate;
        Date end = billStatEndDate;
        Date cur = null;
        int index = 0;
        for(Transaction item:transaction){
            try {
                cur = trnsDt.parse(item.getDate());
                if(cur.getTime() >= strt.getTime() && cur.getTime()<= end.getTime()){
                    System.out.println(cur);
                    System.out.println("date in between");
                    index++;
                    Paragraph p1= new Paragraph(String.valueOf(index)+gap+item.getDate()+gap+item.getUname()+gap+item.getTransType()+gap+item.getAmount(),font2pt);
                    subCatPart.add(p1);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }



    }

    private static void addBottom(Document document,ArrayList<Transaction> transaction) throws DocumentException {

        Paragraph bottPara = new Paragraph();

        Font font5pt = new Font(Font.FontFamily.TIMES_ROMAN, fontBig);
        Font font2pt = new Font(Font.FontFamily.TIMES_ROMAN, fontSmall);

//        addEmptyLine(bottPara, 3);

        float totalDebit = 0;
        float totalCredit = 0;
        Date strt = billStatStrtDate;
        Date end = billStatEndDate;
        Date cur = null;
        SimpleDateFormat trnsDt = new SimpleDateFormat("dd-MMM-yyyy");
        for(Transaction item:transaction){
            try {
                cur = trnsDt.parse(item.getDate());
                if(cur.getTime() >= strt.getTime() && cur.getTime()<= end.getTime()){
                    if(item.getTransType().equals("debit")){
                        totalDebit = totalDebit + Float.valueOf(item.getAmount());
                    }
                    if(item.getTransType().equals("credit")){
                        totalCredit = totalCredit + Float.valueOf(item.getAmount());
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Paragraph p5= new Paragraph(" ------------------------------------------------------------- ",font2pt);
        p5.setAlignment(Element.ALIGN_CENTER);
        bottPara.add(p5);

        Paragraph p3= new Paragraph("Total Purchase Amount : \u20B9" + totalCredit,font2pt);
        p3.setAlignment(Element.ALIGN_RIGHT);
        bottPara.add(p3);

        Paragraph p6= new Paragraph("Total Paid Amount : \u20B9" + totalDebit,font2pt);
        p6.setAlignment(Element.ALIGN_RIGHT);
        bottPara.add(p6);

        Paragraph p7= new Paragraph("Balance Amount : \u20B9" + (totalCredit - totalDebit),font2pt);
        p7.setAlignment(Element.ALIGN_RIGHT);
        bottPara.add(p7);

        Paragraph p4= new Paragraph("  ------------------------------------------------------------- ",font2pt);
        p4.setAlignment(Element.ALIGN_CENTER);
        bottPara.add(p4);
        document.add(bottPara);

    }

}
