package chidhu.opencredit;

/**
 * Author   : Chidambaram P G
 * Date     : 11-07-2018
 */
public class InventoryItems {
    String prodDesc,prodHSN,prodItem,prodCess,prodPurPric,prodSelPric,prodDiscnt,prodQty,prodItemNote,prodType,prodGST,prodUnit;

    public InventoryItems(String prodDesc, String prodHSN, String prodItem, String prodCess, String prodPurPric, String prodSelPric, String prodDiscnt, String prodQty, String prodItemNote, String prodType, String prodGST, String prodUnit) {
        this.prodDesc = prodDesc;
        this.prodHSN = prodHSN;
        this.prodItem = prodItem;
        this.prodCess = prodCess;
        this.prodPurPric = prodPurPric;
        this.prodSelPric = prodSelPric;
        this.prodDiscnt = prodDiscnt;
        this.prodQty = prodQty;
        this.prodItemNote = prodItemNote;
        this.prodType = prodType;
        this.prodGST = prodGST;
        this.prodUnit = prodUnit;
    }

    public String getProdDesc() {
        return prodDesc;
    }

    public void setProdDesc(String prodDesc) {
        this.prodDesc = prodDesc;
    }

    public String getProdHSN() {
        return prodHSN;
    }

    public void setProdHSN(String prodHSN) {
        this.prodHSN = prodHSN;
    }

    public String getProdItem() {
        return prodItem;
    }

    public void setProdItem(String prodItem) {
        this.prodItem = prodItem;
    }

    public String getProdCess() {
        return prodCess;
    }

    public void setProdCess(String prodCess) {
        this.prodCess = prodCess;
    }

    public String getProdPurPric() {
        return prodPurPric;
    }

    public void setProdPurPric(String prodPurPric) {
        this.prodPurPric = prodPurPric;
    }

    public String getProdSelPric() {
        return prodSelPric;
    }

    public void setProdSelPric(String prodSelPric) {
        this.prodSelPric = prodSelPric;
    }

    public String getProdDiscnt() {
        return prodDiscnt;
    }

    public void setProdDiscnt(String prodDiscnt) {
        this.prodDiscnt = prodDiscnt;
    }

    public String getProdQty() {
        return prodQty;
    }

    public void setProdQty(String prodQty) {
        this.prodQty = prodQty;
    }

    public String getProdItemNote() {
        return prodItemNote;
    }

    public void setProdItemNote(String prodItemNote) {
        this.prodItemNote = prodItemNote;
    }

    public String getProdType() {
        return prodType;
    }

    public void setProdType(String prodType) {
        this.prodType = prodType;
    }

    public String getProdGST() {
        return prodGST;
    }

    public void setProdGST(String prodGST) {
        this.prodGST = prodGST;
    }

    public String getProdUnit() {
        return prodUnit;
    }

    public void setProdUnit(String prodUnit) {
        this.prodUnit = prodUnit;
    }
}
