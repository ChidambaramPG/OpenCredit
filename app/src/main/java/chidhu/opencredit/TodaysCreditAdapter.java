package chidhu.opencredit;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import chidhu.opencredit.databaseclasses.OpenCreditDatabase;

/**
 * Author   : Chidambaram P G
 * Date     : 28-04-2018
 */

public class TodaysCreditAdapter extends RecyclerView.Adapter<TodaysCreditAdapter.MyViewHolder>{
    List<CustomerTransaction> todysTrns;
    Context ctx;
    AlertDialog alertDialog;
    AlertDialog.Builder builder;
    Date c;
    OpenCreditDatabase opDB;
    List<Transaction> notifList = new ArrayList<>();
    int credit = 0;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    AlertDialog dialog2;
    AlertDialog.Builder builder2;

    public TodaysCreditAdapter(List<CustomerTransaction> todysTrns, Context ctx) {
        this.todysTrns = todysTrns;
        this.ctx = ctx;
    }

    @Override
    public TodaysCreditAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todays_credit_recycler_cell,parent,false);
        return new TodaysCreditAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TodaysCreditAdapter.MyViewHolder holder, final int position) {

        opDB = Room.databaseBuilder(ctx,OpenCreditDatabase.class,"OpenCreditDB")
                .fallbackToDestructiveMigration().allowMainThreadQueries().build();
        holder.nme.setText(todysTrns.get(position).getName());
        holder.amt.setText("Out of \u20B9"+todysTrns.get(position).getTotAmount());
        if(todysTrns.get(position).getNumTrans() == 1){
            holder.type.setText(todysTrns.get(position).getNumTrans() + " transaction");
        }else{
            holder.type.setText(todysTrns.get(position).getNumTrans() + " transactions");
        }

        holder.dt.setText("\u20B9"+String.valueOf((Float.valueOf(todysTrns.get(position).getTotAmount()) - Float.valueOf(todysTrns.get(position).getTotPaid()))) + " left ");

        holder.send.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                boolean notified = false;
                c = Calendar.getInstance().getTime();
                SimpleDateFormat trnsDt = new SimpleDateFormat("dd-MMM-yyyy");
                final String formattedTrnsDate = trnsDt.format(c);
                SimpleDateFormat trnsTi = new SimpleDateFormat("hh-mm");
                String formattedTrnsTime = trnsTi.format(c);
                SimpleDateFormat trnsMonth = new SimpleDateFormat("MMM");
                final String formattedTrnsMonth = trnsMonth.format(c);
                SimpleDateFormat trnsYear = new SimpleDateFormat("yyyy");
                final String formattedTrnsYear = trnsYear.format(c);

                final String intro = "Hello "+ todysTrns.get(position).getName() + "\n";
                final String[] subject = {""};
                final String concl = "Report generated on " + formattedTrnsDate;

                final boolean[] notif = {false};

                dbRef.child("TRANSACTIONS").child(user.getUid())
                        .child(todysTrns.get(position).getNumber()).child(formattedTrnsYear).child(formattedTrnsMonth).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        for(DataSnapshot snap: dataSnapshot.getChildren()){
//                            for(DataSnapshot snap1: snap.getChildren()){
//                                for(DataSnapshot snap2: snap1.getChildren()){
//                                    for(DataSnapshot snap3: snap2.getChildren()){
                                    System.out.println("####################" );
                                    System.out.println(snap);
                                    System.out.println("####################" );

                                    Map<String, Object> val = (Map<String, Object>) snap.getValue();
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


                                    System.out.println(uname + " - " + amount);
                                    credit += Float.valueOf(amount);
//
//
                                    if(transType != null && notified !=null && transType.equals("credit") && notified.equals("false")){
                                        subject[0] = subject[0] + " You bought for \u20B9" + amount + " on " + date + " at " + time + "\n"  ;
                                        notifList.add(new Transaction(date,time,amount,transType,uname,number,month,year,note,bill,notified));
                                    }else if(transType != null && notified !=null &&transType.equals("debit") && notified.equals("false")){
                                        subject[0] = subject[0] + " You paid \u20B9" + amount + " on " + date + " at " + time + "\n"  ;
                                        notifList.add(new Transaction(date,time,amount,transType,uname,number,month,year,note,bill,notified));
                                    }

//                                    }
//                                }
//                            }
                        }

                        if(subject[0].isEmpty() && notif[0] == false){
                            Toast.makeText(ctx, "All transactions are notified.", Toast.LENGTH_SHORT).show();
                        }else{
                            notif[0] = true;
                            try{

                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=91"+ todysTrns.get(position).getNumber() +"&text="+intro + subject[0] + concl));
                                ctx.startActivity(intent);

                            }catch (Exception e){

                            }
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }
        });

        holder.mark.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                boolean notified = false;
                c = Calendar.getInstance().getTime();
                SimpleDateFormat trnsDt = new SimpleDateFormat("dd-MMM-yyyy");
                final String formattedTrnsDate = trnsDt.format(c);
                SimpleDateFormat trnsTi = new SimpleDateFormat("hh-mm");
                String formattedTrnsTime = trnsTi.format(c);
                SimpleDateFormat trnsMonth = new SimpleDateFormat("MMM");
                final String formattedTrnsMonth = trnsMonth.format(c);
                SimpleDateFormat trnsYear = new SimpleDateFormat("yyyy");
                final String formattedTrnsYear = trnsYear.format(c);

                final String intro = "Hello "+ todysTrns.get(position).getName() + "\n";
                final String[] subject = {""};
                final String concl = "Report generated on " + formattedTrnsDate;

                final boolean[] notif = {false};

                dbRef.child("TRANSACTIONS").child(user.getUid())
                        .child(todysTrns.get(position).getNumber()).child(formattedTrnsYear).child(formattedTrnsMonth).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        for(DataSnapshot snap: dataSnapshot.getChildren()){
//                            for(DataSnapshot snap1: snap.getChildren()){
//                                for(DataSnapshot snap2: snap1.getChildren()){
//                                    for(DataSnapshot snap3: snap2.getChildren()){
                            System.out.println("####################" );
                            System.out.println(snap);
                            System.out.println("####################" );

                            Map<String, Object> val = (Map<String, Object>) snap.getValue();
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


                            System.out.println(uname + " - " + amount);
                            credit += Float.valueOf(amount);
//
//
                            if( notified !=null && notified.equals("false")){
                                notif[0] = true;
                                notifList.add(new Transaction(date,time,amount,transType,uname,number,month,year,note,bill,notified));
//                                break;
                            }
                        }

                        if( notif[0] == false){
                            Toast.makeText(ctx, "All transactions are notified.", Toast.LENGTH_SHORT).show();
                        }else{
                            builder2 = new AlertDialog.Builder(new ContextThemeWrapper(ctx, R.style.Theme_Dialog));
                            dialog2 = builder2.create();
                            LayoutInflater inflater = dialog2.getLayoutInflater();
                            View dialoglayout = inflater.inflate(R.layout.ask_user_layout, null);
                            builder2.setPositiveButton("Mark As Notified", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    System.out.println("Marked as notified");
                                    c = Calendar.getInstance().getTime();
                                    SimpleDateFormat trnsDt = new SimpleDateFormat("dd-MMM-yyyy");
                                    final String formattedTrnsDate = trnsDt.format(c);
                                    SimpleDateFormat trnsTi = new SimpleDateFormat("hh-mm");
                                    final String formattedTrnsTime = trnsTi.format(c);
                                    SimpleDateFormat trnsMonth = new SimpleDateFormat("MMM");
                                    final String formattedTrnsMonth = trnsMonth.format(c);
                                    SimpleDateFormat trnsYear = new SimpleDateFormat("yyyy");
                                    final String formattedTrnsYear = trnsYear.format(c);


                                    for(Transaction tr:notifList){
                                        dbRef.child("TRANSACTIONS").child(user.getUid()).child(tr.getNumber()).child(tr.getYear())
                                                .child(tr.getMonth()).child(tr.getDate()).child(tr.getTime()).child("notified").setValue("true");
                                    }
                                }
                            });
                            builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialog2.hide();
                                }
                            });
                            builder2.setView(dialoglayout);
                            builder2.show();
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return todysTrns.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nme,amt,dt,type;
        ImageView typeImg;
        RelativeLayout lyt;
        Button send,mark;
        public MyViewHolder(View itemView) {
            super(itemView);
            nme = itemView.findViewById(R.id.monthTxt);
            amt = itemView.findViewById(R.id.balTxt);
            dt = itemView.findViewById(R.id.balTxt2);
            type = itemView.findViewById(R.id.yearTxt);
            typeImg = itemView.findViewById(R.id.typeImg);
            lyt = itemView.findViewById(R.id.user_details_lyt);
            send = itemView.findViewById(R.id.sendBtn);
            mark = itemView.findViewById(R.id.markBtn);
        }
    }
}
