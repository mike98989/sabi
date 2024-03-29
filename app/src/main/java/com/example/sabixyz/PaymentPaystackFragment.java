package com.example.sabixyz;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sabixyz.helper.Requests;
import com.folioreader.util.ProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static java.lang.Integer.parseInt;

public class PaymentPaystackFragment extends Fragment implements View.OnClickListener {
private TextView mAmountToPay, mBookTitle, mReturnMsg;
private String strAmountToPay, strBookTitle, user_email, strBookID;
private EditText mCardNumber, mExpiryDate, mCvc;
private Button mPayButon;
public static ArrayList<String> listOfPattern;
private static final char space = ' ';
private static final char slash = '/';
private boolean buttonisactive = false;
private Charge charge;
private Card card;
private android.app.ProgressDialog pDialog;
private SharedPreferences userInfoPreference;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_paynow:
                mReturnMsg.setVisibility(View.GONE);
                pDialog = new android.app.ProgressDialog(getContext());
                pDialog.setMessage("Authenticating...");
                pDialog.setCancelable(false);
                pDialog.show();
                //if(buttonisactive) {
                    initiateCard();
                //}
                break;
        }
    }

    private void initiateCard() {
        // This sets up the card and check for validity
        // This is a test card from paystack
        String cardNumber = mCardNumber.getText().toString().trim().replace(" ","");
        String[] splitExpiryDate = mExpiryDate.getText().toString().trim().split("/");
        int expiryMonth = parseInt(splitExpiryDate[0]); //any month in the future
        int expiryYear = parseInt(splitExpiryDate[1]); // any year in the future. '2018' would work also!
        String cvv = mCvc.getText().toString();  // cvv of the test card
        Log.e("expires", expiryMonth+"@"+expiryYear);
        card = new Card(cardNumber, expiryMonth, expiryYear, cvv);
        if (card.isValid()) {
            performCharge();
            Log.e("validity", "Card is valid"+"@"+card.getType());
        } else {
            pDialog.dismiss();
            mReturnMsg.setText("Card is not valid ");
            mReturnMsg.setVisibility(View.VISIBLE);
           Log.e("validity", "Card is not valid");
        }

    }

    static class Bootstrap {
        public static void setPaystackKey(String publicKey) {
            PaystackSdk.setPublicKey(publicKey);
        }
    }

    public PaymentPaystackFragment() {
        // Required empty public constructor
    }


    private void performCharge() {
        //create a Charge object
        charge = new Charge();

        //set the card to charge
        charge.setCard(card);

        //call this method if you set a plan
        //charge.setPlan("PLN_yourplan");

        charge.setEmail(user_email); //dummy email address

        charge.setAmount(Integer.parseInt(strAmountToPay+"00")); //test amount

        PaystackSdk.chargeCard(getActivity(), charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                pDialog.dismiss();
                // This is called only after transaction is deemed successful.
                // Retrieve the transaction, and send its reference to your server
                // for verification.

                Log.e("TransactionDetails", transaction.toString());

                final String paymentReference = transaction.getReference();
                Map<String, Object> map = new HashMap<>();
                map.put("reference", paymentReference);
                map.put("bookId", strBookID);
                map.put("userId", userInfoPreference.getString(getString(R.string.user_id), ""));
                map.put("token", userInfoPreference.getString(getString(R.string.user_token), ""));


                Requests.validateTransaction(map, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String res = Objects.requireNonNull(response.body()).string();
                        Log.e("verify", res);

                        try {
                            JSONObject jObject =  new JSONObject(res);
                            String strStatus = jObject.optString("status");
                            String strData = jObject.optString("data");
                            Log.e("status", strStatus+"");
                            if(strStatus.equals("1")){

                                Bundle b = new Bundle();
                                b.putString("reference", paymentReference);
                                PaymentConfirmationFragment fragment = new PaymentConfirmationFragment();
                                fragment.setArguments(b);
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack("paymentcard").commit();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },getContext());


            }

            @Override
            public void beforeValidate(Transaction transaction) {
                pDialog.dismiss();
                Log.e("BeforeTransaction", "Before Transaction: "+transaction.getReference());
                final String paymentReference = transaction.getReference();

                Map<String, Object> map = new HashMap<>();
                map.put("reference", paymentReference);
                map.put("bookId", strBookID);
                map.put("userId", userInfoPreference.getString(getString(R.string.user_id), ""));
                map.put("token", userInfoPreference.getString(getString(R.string.user_token), ""));


                Requests.beforeValidateTransaction(map, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String res = Objects.requireNonNull(response.body()).string();
                        Log.e("beforeverify", res);

                        try {
                            JSONObject jObject =  new JSONObject(res);
                            String strStatus = jObject.optString("status");
                            String strData = jObject.optString("data");

                            Log.e("status", strStatus+"");
                            if(!strStatus.equals("1")){
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },getContext());
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                pDialog.dismiss();
                mReturnMsg.setText("Transaction was not successful! "+error.getMessage());
                mReturnMsg.setVisibility(View.VISIBLE);
                Log.e("ErrorOnTransaction", "Transaction was not: "+error+"@"+transaction.getReference());
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Bootstrap.setPaystackKey("pk_test_3f96c9313eb9fd480d99bbc3231114b6ceb3534b");
        PaystackSdk.initialize(getActivity().getApplicationContext());
        user_email = getArguments().getString("email");
        userInfoPreference = getActivity().getSharedPreferences(getString(R.string.user_sharePreference_Key), MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        listOfPattern=new ArrayList<String>();

        String ptVisa = "^4[0-9]$";

        listOfPattern.add(ptVisa);

        String ptMasterCard = "^5[1-5]$";

        listOfPattern.add(ptMasterCard);

        String ptDiscover = "^6(?:011|5[0-9]{2})$";

        listOfPattern.add(ptDiscover);

        String ptAmeExp = "^3[47]$";

        listOfPattern.add(ptAmeExp);

        String ptVerveCard = "^5[0]$";

        listOfPattern.add(ptVerveCard);

        final Integer[] imageArray = { R.drawable.ic_visacard, R.drawable.ic_mastercard, R.drawable.ic_disnet, R.drawable.ic_ameexp, R.drawable.ic_verve };


        View view =  inflater.inflate(R.layout.fragment_payment_paystack, container, false);
        if (getArguments() != null) {
            strAmountToPay = getArguments().getString("amount");
            strBookTitle = getArguments().getString("title");
            strBookID = getArguments().getString("bookId");
        }


        mAmountToPay = view.findViewById(R.id.tv_amount_to_pay);
        mBookTitle = view.findViewById(R.id.tv_book_title);
        mReturnMsg = view.findViewById(R.id.tv_return_msg);
        mExpiryDate = view.findViewById(R.id.edt_expirydate);
        mAmountToPay.setText(getString(R.string.currency)+strAmountToPay);
        mBookTitle.setText(strBookTitle);
        mCardNumber = view.findViewById(R.id.edt_card_number);
        mPayButon = view.findViewById(R.id.btn_paynow);
        mCvc = view.findViewById(R.id.edt_cvc);
        mPayButon.setOnClickListener(this);
        //mCardNumber.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(16) });

        mExpiryDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                // Remove all spacing char
                int pos = 0;
                while (true) {
                    if (pos >= s.length()) break;
                    if (slash == s.charAt(pos) && (((pos + 1) % 3) != 0 || pos + 1 == s.length())) {
                        s.delete(pos, pos + 1);
                    } else {
                        pos++;
                    }
                }

                // Insert char where needed.
                pos = 2;
                while (true) {
                    if (pos >= s.length()) break;
                    final char c = s.charAt(pos);
                    // Only if its a digit where there should be a space we insert a space
                    if ("0123456789".indexOf(c) >= 0) {
                        s.insert(pos, "" + slash);
                    }
                    pos += 3;
                }

            }
        });


        mCardNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String ccNum = s.toString();
                int len=ccNum.length();
                //char space = ' ';


                if(ccNum.length()>=2)
                {
                    for (int i = 0; i < listOfPattern.size(); i++)
                    {
                        if (ccNum.substring(0, 2).matches(listOfPattern.get(i)))
                        {
                            mCardNumber.setCompoundDrawablesWithIntrinsicBounds(imageArray[i], 0, 0, 0);

                        }
                    }
                }else{
                    mCardNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_credit_card_black_24dp, 0, 0, 0);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Remove all spacing char
                int pos = 0;
                while (true) {
                    if (pos >= s.length()) break;
                    if (space == s.charAt(pos) && (((pos + 1) % 5) != 0 || pos + 1 == s.length())) {
                        s.delete(pos, pos + 1);
                    } else {
                        pos++;
                    }
                }

                // Insert char where needed.
                pos = 4;
                while (true) {
                    if (pos >= s.length()) break;
                    final char c = s.charAt(pos);
                    // Only if its a digit where there should be a space we insert a space
                    if ("0123456789".indexOf(c) >= 0) {
                        s.insert(pos, "" + space);
                    }
                    pos += 5;
                }

            }
        });

        return view;
    }



}
