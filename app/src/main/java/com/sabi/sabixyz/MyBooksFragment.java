package com.sabi.sabixyz;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.sabi.sabixyz.adapter.TabAdapter;
import com.sabi.sabixyz.tabs.TabMyBooksFragment;
import com.sabi.sabixyz.tabs.TabSavedBooksFragment;
import com.google.android.material.tabs.TabLayout;

public class MyBooksFragment extends Fragment {
    private TabAdapter tabadapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;


    public MyBooksFragment() {
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

        View view =  inflater.inflate(R.layout.fragment_my_books, container, false);
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);
        tabadapter = new TabAdapter(this.getChildFragmentManager());
        tabadapter.addFragment(new TabMyBooksFragment(), "My Books");
        tabadapter.addFragment(new TabSavedBooksFragment(), "Saved Books");
        viewPager.setAdapter(tabadapter);
        ////////IF TAB INDEX IS SELECTED PRIOR TO PAGE VIEW
        if (getArguments() != null) {
            Integer tabIndex = Integer.valueOf(getArguments().getString("tabIndex"));
            viewPager.setCurrentItem(tabIndex);
        }

        tabLayout.setupWithViewPager(viewPager);

        MainActivity.mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("viewPager",viewPager.getCurrentItem()+"");
                if(viewPager.getCurrentItem()==0) {
                    if (TabMyBooksFragment.ll_search.getVisibility() == View.VISIBLE) {
                        TabMyBooksFragment.ll_search.setVisibility(View.GONE);
                    } else {
                        TabMyBooksFragment.ll_search.setVisibility(View.VISIBLE);
                    }
                }
                if(viewPager.getCurrentItem()==1) {
                    if (TabSavedBooksFragment.ll_search.getVisibility() == View.VISIBLE) {
                        TabSavedBooksFragment.ll_search.setVisibility(View.GONE);
                    } else {
                        TabSavedBooksFragment.ll_search.setVisibility(View.VISIBLE);
                    }
                }


            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("onresume","Got here resumed mybooksfragment");
    }
}
