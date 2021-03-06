package chidhu.opencredit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.TabSettings;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.savvi.rangedatepicker.CalendarPickerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import chidhu.opencredit.databaseclasses.OpenCreditDatabase;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.itextpdf.text.html.HtmlTags.FONT;

public class UserTransactionActivity extends AppCompatActivity {

    RecyclerView userTransList;
    RecyclerView.Adapter adapter;
    public OpenCreditDatabase opDB;
    ArrayList<Transaction> userTrans = new ArrayList<>();
    Date c;
    int credit=0, debit = 0, bal = 0;
    TextView balTxt;
    Button receive;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    AlertDialog dialog;
    AlertDialog.Builder builder;


    android.app.AlertDialog dateDialog;
    android.app.AlertDialog.Builder dateBuilder;

    String num, name;

    ProgressDialog progressDialog;

    private static float fontSmall =  7,fontBig = 10;
    private static String gap = "                 ";

    CalendarPickerView calendar;
    static Date billStatStrtDate = null;
    static Date billStatEndDate = null;

    Date date = null, date2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_transaction);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);

        progressDialog = new ProgressDialog(getApplicationContext());

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
//                        Toast.makeText(UserTransactionActivity.this, "respomse : "+response, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(UserTransactionActivity.this, "PDF creation needs write acess.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();

        Intent i = getIntent();
        num = i.getStringExtra("number");
        name = i.getStringExtra("name");
        SpannableString s = new SpannableString(name.toUpperCase());
        s.setSpan(new chidhu.opencredit.TypefaceSpan(this, "Oswald-Regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);

        c = Calendar.getInstance().getTime();
        balTxt = findViewById(R.id.creditTxt);
        receive = findViewById(R.id.receiveBtn);

        userTransList = findViewById(R.id.userTransactionList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        userTransList.setLayoutManager(mLayoutManager);
        adapter = new UserTransactionAdapter(userTrans,UserTransactionActivity.this);
        userTransList.setAdapter(adapter);


        dbRef.child("TRANSACTIONS").child(user.getUid()).child(num)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        userTrans.clear();

                        for(DataSnapshot snap: dataSnapshot.getChildren()){
                            for(DataSnapshot snap1: snap.getChildren()){
                                for(DataSnapshot snap2: snap1.getChildren()){
                                    for(DataSnapshot snap3: snap2.getChildren()){
                                        System.out.println(snap3);
                                        Map<String, Object> val = (Map<String, Object>) snap3.getValue();
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

                                        userTrans.add(new Transaction(date,time,amount,transType,uname,number,month,year,note,bill,notified));
                                        System.out.println(uname + " - " + amount);
                                        if(transType.equals("credit")) {
                                            credit += Float.valueOf(amount);
                                        }else if(transType.equals("debit")){
                                            debit += Float.valueOf(amount);
                                        }

                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                        Collections.sort(userTrans, new Comparator<Transaction>() {
                            @Override
                            public int compare(Transaction t0, Transaction t1) {
                                SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh-mm-ss");
                                try {
                                    Date d1 = formatter.parse(t0.getDate()+" "+t0.getTime());
                                    Date d2 = formatter.parse(t1.getDate()+" "+t1.getTime());
                                    return d1.compareTo(d2);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                return 1;
                            }
                        });

                        adapter.notifyDataSetChanged();

                        balTxt.setText("\u20B9"+String.valueOf(credit - debit));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        receive.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                SimpleDateFormat trnsDt = new SimpleDateFormat("dd-MMM-yyyy");
                final String formattedTrnsDate = trnsDt.format(c);
                SimpleDateFormat trnsTi = new SimpleDateFormat("hh-mm-ss");
                final String formattedTrnsTime = trnsTi.format(c);
                SimpleDateFormat trnsMonth = new SimpleDateFormat("MMM");
                final String formattedTrnsMonth = trnsMonth.format(c);
                SimpleDateFormat trnsYear = new SimpleDateFormat("yyyy");
                final String formattedTrnsYear = trnsYear.format(c);

                builder = new AlertDialog.Builder(new ContextThemeWrapper(UserTransactionActivity.this, R.style.Theme_Dialog));
                dialog = builder.create();
                LayoutInflater inflater = dialog.getLayoutInflater();
                View dialoglayout = inflater.inflate(R.layout.reveive_alert_layout, null);
                final EditText amount = dialoglayout.findViewById(R.id.amntTxt);
                builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if( amount.getText().toString().isEmpty()){
                            Toast.makeText(UserTransactionActivity.this, "Amount not entered", Toast.LENGTH_SHORT).show();
                        }else{
                            Transaction receive = new Transaction(formattedTrnsDate,formattedTrnsTime,amount.getText().toString(),
                                    "debit",name,num,formattedTrnsMonth,formattedTrnsYear,"[]","","false");

                            dbRef.child("TRANSACTIONS").child(user.getUid()).child(num).child(formattedTrnsYear)
                                    .child(formattedTrnsMonth).child(formattedTrnsDate)
                                    .child(formattedTrnsTime).setValue(receive);
                        }
                        
                    }
                });
                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.hide();
                    }
                });
                builder.setView(dialoglayout);
                builder.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete_user_menu, menu);

        Drawable drawable = menu.findItem(R.id.action_delete).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.colorWhite));
        menu.findItem(R.id.action_delete).setIcon(drawable);

        return true;
    }

    public static Calendar toCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent i = new Intent(this,BottomNavActivity.class);
                startActivity(i);
                break;
            case R.id.action_delete:
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(UserTransactionActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(UserTransactionActivity.this);
                }
                builder.setTitle("Delete Customer")
                        .setMessage("Are you sure you want to delete this customer?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                dbRef.child("CUSTOMER_LIST").child(user.getUid()).child(num).removeValue();
                                dbRef.child("TRANSACTIONS").child(user.getUid()).child(num).removeValue();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;
            case R.id.action_print_all:

                Toast.makeText(this, "Select date", Toast.LENGTH_SHORT).show();
                dateBuilder = new android.app.AlertDialog.Builder(new ContextThemeWrapper(UserTransactionActivity.this, R.style.Theme_Dialog));
                dateDialog = dateBuilder.create();
                LayoutInflater inflater = dateDialog.getLayoutInflater();
                View dialoglayout = inflater.inflate(R.layout.date_selection_lyt, null);
                calendar = dialoglayout.findViewById(R.id.calendar_view);
                Calendar cal01 = Calendar.getInstance();
                SimpleDateFormat calDt = new SimpleDateFormat("dd-MMM-yyyy");

                try {
                    Date st = calDt.parse(userTrans.get(0).getDate());
                    Date en = calDt.parse(userTrans.get(userTrans.size()-1).getDate());

                    ArrayList<Date> selecteddates = new ArrayList<>();
                    selecteddates.add(st);
                    selecteddates.add(en);


                    cal01.setTime(en);
                    cal01.add(Calendar.DAY_OF_YEAR,1);

                    calendar.init(st,new Date(String.valueOf(cal01.getTime())))
                            .inMode(CalendarPickerView.SelectionMode.RANGE);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                dateBuilder.setPositiveButton("PRINT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        System.out.println(calendar.getSelectedDates().get(0));
                        System.out.println(calendar.getSelectedDates().get(calendar.getSelectedDates().size()-1));

                        if(calendar.getSelectedDates().size()<2){
                            Toast.makeText(UserTransactionActivity.this, "Select a start date and end date", Toast.LENGTH_SHORT).show();
                        }else {


                            Date strt = new Date(String.valueOf(calendar.getSelectedDates().get(0)));
                            Date end = new Date(String.valueOf(calendar.getSelectedDates().get(calendar.getSelectedDates().size() - 1)));
                            billStatStrtDate = strt;
                            billStatEndDate = end;

                            try {

                                File path = null;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO) {

                                    path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                                    File file = new File(path, "/" +"Report_"+ userTrans.get(0).getUname()+"_"+billStatStrtDate+"-"+billStatEndDate + ".pdf");
                                    System.out.println("Writing to" + file.getAbsolutePath());
                                    Document document = new Document();
                                    PdfWriter.getInstance(document, new FileOutputStream(file));

                                    Rectangle one = new Rectangle(216, 360);
                                    document.setPageSize(one);
                                    document.setMargins(20, 20, 5, 20);

                                    document.open();
                                    addMetaData(document, userTrans);
                                    addTitlePage(document, userTrans);
                                    addContent(document, userTrans);
                                    addBottom(document, userTrans);

                                    document.close();
                                    Toast.makeText(getApplicationContext(), "File saved to your Documents folder", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    Uri uri = FileProvider.getUriForFile(getApplicationContext(),BuildConfig.APPLICATION_ID,file);
                                    intent.setDataAndType(uri, "application/pdf");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                    startActivity(intent);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                    }
                });

                dateBuilder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dateDialog.dismiss();
                    }
                });

                dateBuilder.setView(dialoglayout);
                dateBuilder.show();





                break;
        }
        return super.onOptionsItemSelected(item);
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
        Paragraph p3 = new Paragraph("Customer : "+ transaction.get(0).getUname(),font2pt);
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


        Paragraph p1= new Paragraph(" Item No"+gap+"Date"+gap+"Type"+gap+"Amount ",font2pt);
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
                    Paragraph p1= new Paragraph(String.valueOf(index)+gap+item.getDate()+gap+item.getTransType()+gap+item.getAmount(),font2pt);
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

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
