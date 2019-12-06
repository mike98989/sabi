package com.example.sabixyz;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.sabixyz.adapter.TabAdapter;
import com.example.sabixyz.tabs.TabMyBooksFragment;
import com.example.sabixyz.tabs.TabSavedBooksFragment;
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
        tabadapter = new TabAdapter(this.getFragmentManager());
        tabadapter.addFragment(new TabMyBooksFragment(), "My Books");
        tabadapter.addFragment(new TabSavedBooksFragment(), "Saved Books");
        viewPager.setAdapter(tabadapter);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}