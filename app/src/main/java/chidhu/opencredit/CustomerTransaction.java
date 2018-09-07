package chidhu.opencredit;

/**
 * Author   : Chidambaram P G
 * Date     : 19-04-2018
 */

public class CustomerTransaction {
    String number, name, totAmount, totPaid;
    int numTrans;

    public CustomerTransaction(String number, String name, String totAmount, String totPaid, int numsTrans) {
        this.number = number;
        this.name = name;
        this.totAmount = totAmount;
        this.totPaid = totPaid;
        this.numTrans = numsTrans;
    }

    public int getNumTrans() {
        return numTrans;
    }

    public void setNumTrans(int numTrans) {
        this.numTrans = numTrans;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTotAmount() {
        return totAmount;
    }

    public void setTotAmount(String totAmount) {
        this.totAmount = totAmount;
    }

    public String getTotPaid() {
        return totPaid;
    }

    public void setTotPaid(String totPaid) {
        this.totPaid = totPaid;
    }
}
