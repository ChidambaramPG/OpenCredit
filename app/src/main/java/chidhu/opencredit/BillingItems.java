package chidhu.opencredit;

/**
 * Author   : Chidambaram P G
 * Date     : 20-07-2018
 */
public class BillingItems {

    String itemName,itemHSN,itemPrice,itemQty,itmUnitPrice,itmDiscount,itmTax;

    public BillingItems(String itemName, String itemHSN, String itemPrice, String itemQty, String itmUnitPrice, String itmDiscount, String itmTax) {
        this.itemName = itemName;
        this.itemHSN = itemHSN;
        this.itemPrice = itemPrice;
        this.itemQty = itemQty;
        this.itmUnitPrice = itmUnitPrice;
        this.itmDiscount = itmDiscount;
        this.itmTax = itmTax;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemHSN() {
        return itemHSN;
    }

    public void setItemHSN(String itemHSN) {
        this.itemHSN = itemHSN;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemQty() {
        return itemQty;
    }

    public void setItemQty(String itemQty) {
        this.itemQty = itemQty;
    }

    public String getItmUnitPrice() {
        return itmUnitPrice;
    }

    public void setItmUnitPrice(String itmUnitPrice) {
        this.itmUnitPrice = itmUnitPrice;
    }

    public String getItmDiscount() {
        return itmDiscount;
    }

    public void setItmDiscount(String itmDiscount) {
        this.itmDiscount = itmDiscount;
    }

    public String getItmTax() {
        return itmTax;
    }

    public void setItmTax(String itmTax) {
        this.itmTax = itmTax;
    }
}
