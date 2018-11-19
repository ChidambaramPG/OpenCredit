package chidhu.opencredit.databaseclasses;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Author   : Chidambaram P G
 * Date     : 18-04-2018
 */


@Dao
public interface OpenCreditDAO {

    @Insert
    void addCustomer(Customers customers);

    @Query("select * from customers")
    List<Customers> getUsers();

    @Update
    void updateCustomer(Customers customers);

    @Insert
    void addTransaction(Transactions transactions);

    @Query("select * from transactions")
    List<Transactions> getTransactions();

    @Update
    void updateTransaction(Transactions transactions);

    @Query("select * from transactions where date = :date AND number = :number")
    List<Transactions> getTodaysCredits(String date, String number);

    @Query("select * from transactions where date = :date")
    List<Transactions> getTodaysTransactions(String date);

    @Query("select * from transactions where month = :month AND year = :year")
    List<Transactions> getMonthWiseTransactions(String month, String year);

    @Query("select * from transactions where number = :number AND month = :month AND year = :year ")
    List<Transactions> getMonthlyUserWiseTransactions(String number, String month, String year);

    @Query("DELETE FROM customers WHERE number = :number")
    void deleteByUserContactId(String number);

    @Query("select * FROM customers WHERE number = :number")
    Customers getUserByContactId(String number);

    @Query("select * FROM transactions WHERE number = :number")
    List<Transactions> getUserTransactions(String number);

    @Query("select * FROM transactions WHERE number = :number AND notified = 0")
    List<Transactions> getUserUnnotifiedTransactions(String number);



}

