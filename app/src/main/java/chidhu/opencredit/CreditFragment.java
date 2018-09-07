package chidhu.opencredit;

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;


public class CreditFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;
    TotalTransactionFragment totFrag;
    private CreditPagerAdapter mCreditPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    static SharedPreferences.Editor editor;

    public CreditFragment() {
        // Required empty public constructor
    }

    public static CreditFragment newInstance() {
        CreditFragment fragment = new CreditFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity())
                .setActionBarTitle("OVERVIEW");

        editor = getActivity().getSharedPreferences("OC_REM_TAB", MODE_PRIVATE).edit();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_credit, container, false);


        tabLayout = view.findViewById(R.id.tab_layout);
        fragmentManager = getChildFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        mCreditPagerAdapter = new CreditPagerAdapter(fragmentManager,3);

        mViewPager = view.findViewById(R.id.container);
        mViewPager.setAdapter(mCreditPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#ffffff"));
        tabLayout.setSelectedTabIndicatorHeight((int) (5 * getResources().getDisplayMetrics().density));
        tabLayout.setTabTextColors(Color.parseColor("#b3cdef"), Color.parseColor("#ffffff"));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        setCustomFont();

        mViewPager.setCurrentItem(1);

        return view;
    }

    public void setCustomFont() {

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();

        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);

            int tabChildsCount = vgTab.getChildCount();

            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    //Put your font in assests folder
                    //assign name of the font here (Must be case sensitive)
                    ((TextView) tabViewChild).setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Oswald-Regular.ttf"));
                }
            }
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public static class CreditPagerAdapter extends FragmentPagerAdapter {

        private String[] tabTitles = new String[]{"TOTAL","THIS MONTH","TODAY"};

        int tabLayoutIndex;


        public CreditPagerAdapter(FragmentManager fm, int tabLayout) {
            super(fm);
            this.tabLayoutIndex = tabLayout;
        }

        @Override
        public Fragment getItem(int position) {

            Fragment selectedFragment = null;

            switch(position){
                case 0:
                    selectedFragment = TotalCreditFragment.newInstance();
                    break;
                case 1:
                    selectedFragment = ThisMonthCreditFragment.newInstance();
                    break;
                case 2:
                    selectedFragment = TodayCreditFragment.newInstance();
                    break;
                default:
                    return null;
            }

            return selectedFragment;

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return tabLayoutIndex;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity())
                .setActionBarTitle("OVERVIEW");
//        SharedPreferences prefs = getActivity().getSharedPreferences("OC_REM_TAB", MODE_PRIVATE);
//        mViewPager.setCurrentItem(prefs.getInt("tab_id",1));
    }
}
