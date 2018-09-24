package chidhu.opencredit;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Section;
import com.itextpdf.text.TabSettings;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.karumi.dexter.Dexter;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.itextpdf.text.Annotation.FILE;
import static com.itextpdf.text.Element.ALIGN_LEFT;

/**
 * Author   : Chidambaram P G
 * Date     : 04-06-2018
 */

public class UserTransactionAdapter extends RecyclerView.Adapter<UserTransactionAdapter.MyViewHolder> {

    List<Transaction> transList = new ArrayList<>();
    Context ctx;
    AlertDialog dialog;
    AlertDialog.Builder builder;
    ArrayList<BillingItems> billing_items = new ArrayList<>();
    AddedItemsAdapter adapter;
    static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Date c;

    private static float fontSmall =  7,fontBig = 10;
    private static String gap = "           ";


    public UserTransactionAdapter(List<Transaction> transList, Context ctx) {
        this.transList = transList;
        this.ctx = ctx;
    }

    @Override
    public UserTransactionAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todays_transactions_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserTransactionAdapter.MyViewHolder holder, final int position) {
        holder.nme.setText(transList.get(position).getUname());
        holder.amt.setText("\u20B9"+transList.get(position).getAmount());
        holder.type.setText(transList.get(position).getTransType());
        holder.dt.setText(transList.get(position).getDate());

        if(transList.get(position).getTransType().equals("debit")){
            holder.typeImg.setImageDrawable(ctx.getDrawable(R.drawable.ic_debit_icon));
            holder.typeImg.setColorFilter(R.color.colorBtnGradStrt);
        }

        holder.lyt.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
            builder = new AlertDialog.Builder(new ContextThemeWrapper(ctx, R.style.Theme_Dialog));
            dialog = builder.create();

            LayoutInflater inflater = dialog.getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.transaction_details_view, null);

            RecyclerView addedItems = dialoglayout.findViewById(R.id.itemsList);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(ctx);
            addedItems.setLayoutManager(mLayoutManager);


            TextView amount = dialoglayout.findViewById(R.id.amountTxt);
            TextView date = dialoglayout.findViewById(R.id.dateTxt);
            TextView time = dialoglayout.findViewById(R.id.timeTxt);
            amount.setText("\u20B9" +transList.get(position).getAmount());
            date.setText(transList.get(position).getDate());
            time.setText(transList.get(position).getTime());

            if(transList.get(position).getNote() == null ){
                System.out.println("No items");

                billing_items.clear();
//                adapter = new AddedItemsAdapter(billing_items,ctx);
//                addedItems.setAdapter(adapter);
                System.out.println(billing_items.size());
            }else{
                if(transList.get(position).getNote().equals("")){

                    billing_items.clear();

                }else{

                    System.out.println("Items Present");
                    try{
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<BillingItems>>(){}.getType();
                        billing_items = gson.fromJson(transList.get(position).getNote(), listType);
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                    adapter = new AddedItemsAdapter(billing_items,ctx);
                    addedItems.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }
//                System.out.println(billing_items.size());
            }

            if(!transList.get(position).getBill().isEmpty()){
//                    Picasso.get().load(transList.get(position).getBill()).into(bill);
//                    billMessage.setVisibility(View.GONE);
            }

            builder.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialog.hide();
                }
            });
            builder.setNegativeButton("EDIT BILL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(transList.get(position).getNotified().equals("true")){
                        Toast.makeText(ctx, "Notified bills cannot be edited", Toast.LENGTH_SHORT).show();
                    }else{
                        Intent editIntent = new Intent(ctx,UserBillEditActivity.class);

                        editIntent.putExtra("trnsType",transList.get(position).getTransType());
                        editIntent.putExtra("name",transList.get(position).getUname());
                        editIntent.putExtra("number",transList.get(position).getNumber());
                        editIntent.putExtra("year",transList.get(position).getYear());
                        editIntent.putExtra("month",transList.get(position).getMonth());
                        editIntent.putExtra("date",transList.get(position).getDate());
                        editIntent.putExtra("time",transList.get(position).getTime());
                        editIntent.putExtra("amount",transList.get(position).getAmount());
                        editIntent.putExtra("note",transList.get(position).getNote());
                        editIntent.putExtra("bill",transList.get(position).getBill());
                        ctx.startActivity(editIntent);
                    }
                }
            });
            builder.setNeutralButton("PRINT", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {



                    try {

                        File path = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO) {
                            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                            File file = new File(path, "/" + transList.get(position).getTime()+".pdf");
                            System.out.println("Writing to"+ file.getAbsolutePath());
                            Document document = new Document();
                            PdfWriter.getInstance(document, new FileOutputStream(file));


                            Rectangle one = new Rectangle(216,360);
                            document.setPageSize(one);
                            document.setMargins(20, 20, 5, 20);
                            document.open();

                            addMetaData(document,transList.get(position));
                            addTitlePage(document,transList.get(position));
                            addContent(document,transList.get(position));
                            addBottom(document,transList.get(position));

                            document.close();

                            Toast.makeText(ctx, "File saved to your Documents folder", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            ctx.startActivity(intent);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            builder.setView(dialoglayout);
            builder.show();
            }
        });
    }

    private static void addMetaData(Document document,Transaction transaction) {
        System.out.println("inside metadata");
        document.addTitle("Invoice for "+ transaction.getUname());
        document.addSubject("Invoice from OpenCredit app");
        document.addKeywords("OpenCredit, Invoice, Bill, "+transaction.getUname());
        document.addAuthor("OpenCredit Inc");
        document.addCreator("chidambarampg@gmail.com");
    }

    private static void addTitlePage(Document document,Transaction transaction)
            throws DocumentException {
        Font font5pt = new Font(Font.FontFamily.TIMES_ROMAN, fontBig);
        Font font2pt = new Font(Font.FontFamily.TIMES_ROMAN, fontSmall);

        Paragraph preface = new Paragraph();
//        addEmptyLine(preface, 1);

        Paragraph p1= new Paragraph(" OPEN CREDIT ",font5pt);
        p1.setAlignment(Element.ALIGN_CENTER);
        preface.add(p1);

        Paragraph p5= new Paragraph(" ------------------------------------------------------------- ",font2pt);
        p5.setAlignment(Element.ALIGN_CENTER);
        preface.add(p5);


        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat trnsDt = new SimpleDateFormat("dd-MMM-yyyy");
        final String formattedTrnsDate = trnsDt.format(c);

        SimpleDateFormat trnsDt02 = new SimpleDateFormat("ddMMyyyyhhmm");
        final String formattedTrnsDate02 = trnsDt02.format(c);

        SimpleDateFormat trnsDt03 = new SimpleDateFormat("hh-mm-ss");
        final String formattedTrnsTime = trnsDt03.format(c);

        Chunk glue = new Chunk(new VerticalPositionMark());
        Paragraph p3 = new Paragraph("Customer : "+ transaction.getUname(),font2pt);
        p3.add(new Chunk(glue));
        p3.add("Invoice : INV" + formattedTrnsDate02);
        preface.add(p3);

        Chunk glue2 = new Chunk(new VerticalPositionMark());
        Paragraph p6 = new Paragraph("Date : " + formattedTrnsDate,font2pt);
        p6.add(new Chunk(glue2));
        p6.add("Time : " + formattedTrnsTime);
        preface.add(p6);

        Paragraph p4= new Paragraph(" ------------------------------------------------------------- ",font2pt);
        p4.setAlignment(Element.ALIGN_CENTER);
        preface.add(p4);

//        addEmptyLine(preface, 2);

        document.add(preface);
    }

    private static void addContent(Document document,Transaction transaction) throws DocumentException {

        Font font5pt = new Font(Font.FontFamily.TIMES_ROMAN, fontBig);
        Font font2pt = new Font(Font.FontFamily.TIMES_ROMAN, fontSmall);

        Paragraph subPara = new Paragraph();


        Paragraph p1= new Paragraph();
        p1.add(new Chunk("Item No",font2pt));
        p1.setTabSettings(new TabSettings(20f));
        p1.add(Chunk.TABBING);
        p1.add(new Chunk("Item",font2pt));
        p1.add(Chunk.TABBING);
        p1.add(new Chunk("Price/Unit",font2pt));
        p1.add(Chunk.TABBING);
        p1.add(new Chunk("Qty",font2pt));
        p1.add(Chunk.TABBING);
        p1.add(new Chunk("Total",font2pt));
        subPara.add(p1);

        createTable(subPara,transaction);

        document.add(subPara);
    }

    private static void createTable(Paragraph subCatPart,Transaction transaction)
            throws BadElementException {

        Font font5pt = new Font(Font.FontFamily.TIMES_ROMAN, fontBig);
        Font font2pt = new Font(Font.FontFamily.TIMES_ROMAN, fontSmall);

        if(transaction.getTransType().equals("debit")){

            Paragraph p1= new Paragraph();

            p1.add(new Chunk("01",font2pt));
            p1.setTabSettings(new TabSettings(30f));
            p1.add(Chunk.TABBING);
            p1.add(new Chunk(transaction.getTransType(),font2pt));
            p1.add(Chunk.TABBING);
            p1.add(new Chunk(transaction.getAmount(),font2pt));
            p1.add(Chunk.TABBING);
            p1.add(new Chunk("01",font2pt));
            p1.add(Chunk.TABBING);
            p1.add(new Chunk(transaction.getAmount(),font2pt));

            subCatPart.add(p1);

        }else{

            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<BillingItems>>(){}.getType();
            ArrayList<BillingItems> items = gson.fromJson(transaction.getNote(), listType);

            if(items.size() <1){

                Paragraph p3= new Paragraph(" No Items are added with this purchase ",font2pt);
                p3.setAlignment(Element.ALIGN_CENTER);
                subCatPart.add(p3);

            }else{

                int index = 0;
                for(BillingItems item:items){
                    index++;
                    Paragraph p1= new Paragraph();

                    p1.add(new Chunk(String.valueOf(index),font2pt));
                    p1.setTabSettings(new TabSettings(30f));
                    p1.add(Chunk.TABBING);
                    p1.add(new Chunk(item.getItemName(),font2pt));
                    p1.add(Chunk.TABBING);
                    p1.add(new Chunk(item.getItmUnitPrice(),font2pt));
                    p1.add(Chunk.TABBING);
                    p1.add(new Chunk(item.getItemQty(),font2pt));
                    p1.add(Chunk.TABBING);
                    p1.add(new Chunk(item.getItemPrice(),font2pt));

                    subCatPart.add(p1);
                }

            }

        }


    }

    private static void addBottom(Document document,Transaction transaction) throws DocumentException {

        Font font5pt = new Font(Font.FontFamily.TIMES_ROMAN, fontBig);
        Font font2pt = new Font(Font.FontFamily.TIMES_ROMAN, fontSmall);

        Paragraph bottPara = new Paragraph();

//        addEmptyLine(bottPara, 3);

        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<BillingItems>>(){}.getType();
        ArrayList<BillingItems> items = gson.fromJson(transaction.getNote(), listType);

        Paragraph p5= new Paragraph(" ------------------------------------------------------------- ",font2pt);
        p5.setAlignment(Element.ALIGN_CENTER);
        bottPara.add(p5);

        Paragraph p3= new Paragraph("Total : \u20B9" + transaction.getAmount(),font2pt);
        p3.setAlignment(Element.ALIGN_RIGHT);
        bottPara.add(p3);

        Paragraph p4= new Paragraph(" ------------------------------------------------------------- ",font2pt);
        p4.setAlignment(Element.ALIGN_CENTER);
        bottPara.add(p4);

        addEmptyLine(bottPara, 2);

        document.add(bottPara);

    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    @Override
    public int getItemCount() {
        return transList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nme,amt,dt,type;
        ImageView typeImg;
        RelativeLayout lyt;

        public MyViewHolder(View itemView) {
            super(itemView);
            nme = itemView.findViewById(R.id.monthTxt);
            amt = itemView.findViewById(R.id.balTxt);
            dt = itemView.findViewById(R.id.balTxt2);
            type = itemView.findViewById(R.id.yearTxt);
            typeImg = itemView.findViewById(R.id.typeImg);
            lyt = itemView.findViewById(R.id.todaysTransLyt);

        }
    }




}
