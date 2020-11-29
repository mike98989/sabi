package com.sabi.sabixyz;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class PaymentFragment extends Fragment implements View.OnClickListener {
private LinearLayout mCreditDebitkMethod;
    public PaymentFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_payment, container, false);
        mCreditDebitkMethod = view.findViewById(R.id.linearlayout_creditdebitcard_payment);
        mCreditDebitkMethod.setOnClickListener(this);
        return view;

    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.linearlayout_creditdebitcard_payment:
                Toast.makeText(getContext(), "Credit/Debit Card clicked", Toast.LENGTH_LONG).show();
                CreditDebitCardMethod fragment = new CreditDebitCardMethod();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack("paymentmethods").commit();
                //fragmentManager.();
                break;

        }
    }
}
