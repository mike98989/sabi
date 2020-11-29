package com.sabi.sabixyz;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sabi.sabixyz.adapter.TabAdapter;
import com.sabi.sabixyz.tabs.index1Fragment;
import com.sabi.sabixyz.tabs.index2Fragment;
import com.sabi.sabixyz.tabs.index3Fragment;
import com.google.android.material.tabs.TabLayout;

import static com.sabi.sabixyz.tabs.index1Fragment.ll_search;


public class LandingScreenFragment extends Fragment {

    private TabAdapter tabadapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public LandingScreenFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_landing_screen, container, false);

        viewPager = view.findViewById(R.id.viewPager);

        tabLayout = view.findViewById(R.id.tabLayout);

        //ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        tabadapter = new TabAdapter(this.getChildFragmentManager());
        //tabadapter = new TabAdapter(this.getFragmentManager());
        tabadapter.addFragment(new index1Fragment(), "All Books");
        tabadapter.addFragment(new index2Fragment(), "Best Sellers");
        tabadapter.addFragment(new index3Fragment(), "Most Viewed");

        //ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

        viewPager.setAdapter(tabadapter);
        tabLayout.setupWithViewPager(viewPager);

        MainActivity.mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("viewPager",viewPager.getCurrentItem()+"");
                if(viewPager.getCurrentItem()==0) {
                    if (ll_search.getVisibility() == View.VISIBLE) {
                        ll_search.setVisibility(View.GONE);
                    } else {
                        ll_search.setVisibility(View.VISIBLE);
                    }
                }
                if(viewPager.getCurrentItem()==1) {
                    if (index2Fragment.ll_search.getVisibility() == View.VISIBLE) {
                        index2Fragment.ll_search.setVisibility(View.GONE);
                    } else {
                        index2Fragment.ll_search.setVisibility(View.VISIBLE);
                    }
                }
                if(viewPager.getCurrentItem()==2) {
                    if (index3Fragment.ll_search.getVisibility() == View.VISIBLE) {
                        index3Fragment.ll_search.setVisibility(View.GONE);
                    } else {
                        index3Fragment.ll_search.setVisibility(View.VISIBLE);
                    }
                }

            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
