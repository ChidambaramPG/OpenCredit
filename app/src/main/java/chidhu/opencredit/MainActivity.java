package chidhu.opencredit;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CreditFragment.OnFragmentInteractionListener,TrendsFragment.OnFragmentInteractionListener,
        TransactionFragment.OnFragmentInteractionListener,AccountFragment.OnFragmentInteractionListener,
        CustomersFragment.OnFragmentInteractionListener,TodayCreditFragment.OnFragmentInteractionListener,
        TotalTransactionFragment.OnFragmentInteractionListener,ThisMonthCreditFragment.OnFragmentInteractionListener,
        TotalCreditFragment.OnFragmentInteractionListener,TodayTransactionFragment.OnFragmentInteractionListener,
        ThisMonthTransactionFragment.OnFragmentInteractionListener,InventoryFragment.OnFragmentInteractionListener{

    FirebaseUser user;
    TextView unme,emil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        user = FirebaseAuth.getInstance().getCurrentUser();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        unme = headerView.findViewById(R.id.usernameTxt);
        unme.setText("OPEN CREDIT");
        emil = headerView.findViewById(R.id.userEmailTxt);
        emil.setText(user.getEmail());

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contents, CreditFragment.newInstance());
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment selectedFragment = null;
        switch (item.getItemId()) {
            case R.id.Credit:
                selectedFragment = CreditFragment.newInstance();
                break;
            case R.id.Trends:
                selectedFragment = TrendsFragment.newInstance();
                break;
            case R.id.MyAccount:
                selectedFragment = AccountFragment.newInstance();
                break;
            case R.id.Inventory:
                selectedFragment = InventoryFragment.newInstance();
                break;
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contents, selectedFragment);
        transaction.commit();
        return true;
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void setActionBarTitle(String title) {
        try{
            getSupportActionBar().setTitle(title);
        }catch (Exception e){

        }

    }
}
