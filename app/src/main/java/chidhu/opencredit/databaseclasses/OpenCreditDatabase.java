package chidhu.opencredit.databaseclasses;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Author   : Chidambaram P G
 * Date     : 18-04-2018
 */

@Database(entities = {Customers.class,Transactions.class},version = 7, exportSchema = false)

public abstract class OpenCreditDatabase extends RoomDatabase {
    public abstract OpenCreditDAO openCreditDAO();
}
