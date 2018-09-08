package chidhu.opencredit;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BottomNavActivity extends AppCompatActivity implements
        CreditFragment.OnFragmentInteractionListener,TrendsFragment.OnFragmentInteractionListener,
        TransactionFragment.OnFragmentInteractionListener,AccountFragment.OnFragmentInteractionListener,
        CustomersFragment.OnFragmentInteractionListener,TodayCreditFragment.OnFragmentInteractionListener,
        TotalTransactionFragment.OnFragmentInteractionListener,ThisMonthCreditFragment.OnFragmentInteractionListener,
        TotalCreditFragment.OnFragmentInteractionListener,TodayTransactionFragment.OnFragmentInteractionListener,
        ThisMonthTransactionFragment.OnFragmentInteractionListener,InventoryFragment.OnFragmentInteractionListener{


    BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);

        SpannableString s = new SpannableString("CREDIT");
        s.setSpan(new chidhu.opencredit.TypefaceSpan(getApplicationContext(), "Oswald-Regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);


        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contents, CreditFragment.newInstance());
        transaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_overview:
                    selectedFragment = CreditFragment.newInstance();
                    SpannableString s = new SpannableString("CREDIT");
                    s.setSpan(new chidhu.opencredit.TypefaceSpan(getApplicationContext(), "Oswald-Regular.ttf"), 0, s.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    getSupportActionBar().setTitle(s);
//                    navigation.setSelectedItemId(R.id.navigation_overview);
                    break;
                case R.id.navigation_trends:
                    selectedFragment = TrendsFragment.newInstance();
                    SpannableString s1 = new SpannableString("TRENDS");
                    s1.setSpan(new chidhu.opencredit.TypefaceSpan(getApplicationContext(), "Oswald-Regular.ttf"), 0, s1.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    getSupportActionBar().setTitle(s1);
//                    navigation.setSelectedItemId(R.id.navigation_trends);
                    break;
                case R.id.navigation_inventory:
                    selectedFragment = InventoryFragment.newInstance();
                    SpannableString s2 = new SpannableString("INVENTORY");
                    s2.setSpan(new chidhu.opencredit.TypefaceSpan(getApplicationContext(), "Oswald-Regular.ttf"), 0, s2.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    getSupportActionBar().setTitle(s2);
//                    navigation.setSelectedItemId(R.id.navigation_inventory);
                    break;
                case R.id.navigation_profile:
                    selectedFragment = AccountFragment.newInstance();
                    SpannableString s3 = new SpannableString("PROFILE");
                    s3.setSpan(new chidhu.opencredit.TypefaceSpan(getApplicationContext(), "Oswald-Regular.ttf"), 0, s3.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    getSupportActionBar().setTitle(s3);
//                    navigation.setSelectedItemId(R.id.navigation_profile);
                    break;
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contents, selectedFragment);
            transaction.commit();

            return true;
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }



    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
