package chidhu.opencredit;

import java.util.ArrayList;

/**
 * Author   : Chidambaram P G
 * Date     : 02-06-2018
 */

public class Transaction {
    private String date;

    private String time;

    private String amount;

    private String transType;

    private String uname;

    private String number;

    private String month;

    private String year;

    private String note;

    private String bill;

    private String notified;

    public Transaction() {
    }

    public Transaction(String date, String time, String amount, String transType, String uname, String number, String month, String year, String note, String bill, String notified) {
        this.date = date;
        this.time = time;
        this.amount = amount;
        this.transType = transType;
        this.uname = uname;
        this.number = number;
        this.month = month;
        this.year = year;
        this.note = note;
        this.bill = bill;
        this.notified = notified;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getBill() {
        return bill;
    }

    public void setBill(String bill) {
        this.bill = bill;
    }

    public String getNotified() {
        return notified;
    }

    public void setNotified(String notified) {
        this.notified = notified;
    }
}
