package chidhu.opencredit;

/**
 * Author   : Chidambaram P G
 * Date     : 03-06-2018
 */

public class Customer {
    String name,number;

    public Customer(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
