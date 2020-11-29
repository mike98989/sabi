package com.sabi.sabixyz;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;
import com.sabi.sabixyz.helper.Requests;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class BookDetailsFragment extends Fragment implements View.OnClickListener {
String Title, Description, imageUrl, itemId;
private TextView mTitle, mDescription;
private ImageView mImageView;
private Button mBuyBookButton, mSavebutton;
private SharedPreferences userInfoPreference;
private String filePath,fileTitle;
    public BookDetailsFragment() {
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
        userInfoPreference = getActivity().getSharedPreferences(getString(R.string.user_sharePreference_Key), MODE_PRIVATE);
        View view =  inflater.inflate(R.layout.fragment_book_details, container, false);
        mTitle = view.findViewById(R.id.tv_book_title);
        mDescription = view.findViewById(R.id.tv_book_desc);
        mImageView = view.findViewById(R.id.imgview_book_cover);
        mBuyBookButton = view.findViewById(R.id.btn_buy_book);
        mSavebutton = view.findViewById(R.id.btn_save_for_later);
        mSavebutton.setOnClickListener(this);
        mBuyBookButton.setOnClickListener(this);

        if (getArguments() != null) {
            Title = getArguments().getString("title");
            mTitle.setText(Title);
            //Log.e("desc", getArguments().getString("description"));
            mDescription.setText(getArguments().getString("description").replaceAll("\\n","\n"));
            Picasso.get().load(Constants.LOCAL_PATH+getArguments().getString("imageurl")).into(mImageView);
            if(getArguments().getString("amount").equals("0")){
                mBuyBookButton.setText("Get Book (Free)");
            }else {
                mBuyBookButton.setText("Buy Book " + getString(R.string.currency) + getArguments().getString("amount"));
            }
            //Log.e("Title", Title);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_buy_book:
                if(getArguments().getString("amount").equals("0")) {
                    //mReturnMsg.setVisibility(View.GONE);
                    final ProgressDialog pDialog = new android.app.ProgressDialog(getContext());
                    final String defaultMsg = "Something went wrong, please check your internet connection";
                    pDialog.setMessage("Saving Book...");
                    pDialog.setCancelable(false);
                    pDialog.show();

                    Map<String, Object> map = new HashMap<>();
                    map.put("book_id", getArguments().getString("id"));
                    map.put("DeviceToken", userInfoPreference.getString(getString(R.string.user_token), ""));

                    Requests.updateUserBooksTable(map, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                           getActivity().runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   pDialog.dismiss();
                                   Toast.makeText(getContext(), defaultMsg, Toast.LENGTH_LONG).show();
                               }
                           });

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            pDialog.dismiss();
                            String res = Objects.requireNonNull(response.body()).string();
                            Log.e("saveresponse", res);

                            try {
                                JSONObject jObject = new JSONObject(res);
                                String strStatus = jObject.optString("status");
                                final String strMessage = jObject.optString("message");
                                //if(strStatus.equals("1")){
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), strMessage, Toast.LENGTH_LONG).show();
                                    }
                                });
                                //}

                                if(strStatus.equals("1")){
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                    final ProgressDialog pdialog = new
                                            ProgressDialog(getContext());
                                    pdialog.setMessage("Loading Content... Please wait");
                                    pdialog.setCancelable(false);
                                    pdialog.show();
                                    PRDownloader.initialize(getActivity().getApplicationContext());
                                    filePath = getArguments().getString("filepath");
                                    fileTitle = getArguments().getString("title").replace("","").toLowerCase();
                                    try {
                                        Boolean checkfile = checkIfExist(filePath);
                                        if(!checkfile){
                                            DownloadEpubFile(filePath, pdialog);
                                        }else{
                                            pdialog.dismiss();
                                            getFragmentManager().popBackStack();
                                        }

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                        }
                                    });
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, getContext());


                }else{
                    Bundle b = new Bundle();
                    b.putString("title", getArguments().getString("title"));
                    b.putString("bookId", getArguments().getString("id"));
                    b.putString("amount", getArguments().getString("amount"));
                    b.putString("filePath", getArguments().getString("filepath"));
                    b.putString("email", userInfoPreference.getString(getString(R.string.user_email), ""));

                    Fragment myFragment = new PaymentPaystackFragment();
                    myFragment.setArguments(b);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myFragment).addToBackStack(null).commit();
                }
                break;
            case R.id.btn_save_for_later:
                save_book_for_later(getArguments().getString("id"));
                break;
        }
    }

    private void save_book_for_later(String book_id) {
        final android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(getContext());
        progressDialog.setMessage("Loading Data");
        progressDialog.show();

        Map<String, Object> map = new HashMap<>();
        map.put("book_id", book_id);
        //map.put("user_id", userInfoPreference.getString(getString(R.string.user_id), ""));
        map.put("DeviceToken", userInfoPreference.getString(getString(R.string.user_token), ""));
        Requests.save_book_for_later(map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                progressDialog.dismiss();
                Log.e("response", "Somoething went wrong! please try again");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                progressDialog.dismiss();
                String res = Objects.requireNonNull(response.body()).string();
                Log.e("res", res);

                try {
                    JSONObject object = new JSONObject(res);
                    final String message = object.optString("message");
                    final String status = object.optString("status");

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();
                                if(status.equals("1")) {
                                    Fragment myFragment = new MyBooksFragment();
                                    Bundle argmnts = new Bundle();
                                    argmnts.putString("tabIndex", "2");
                                    myFragment.setArguments(argmnts);
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myFragment).addToBackStack(null).commit();

                                }
                                Log.e("response", message);
                            }
                        });


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },getContext());
    }



    private void DownloadEpubFile(final String filePath, final
    ProgressDialog pdialog) {
        String url = com.sabi.sabixyz.Constants.LOCAL_PATH+ com.sabi.sabixyz.Constants.EPUB_REPO+filePath;
        //String dirPath = "file:///android_asset/";
        String dirPath =
                getActivity().getBaseContext().getFilesDir().getAbsolutePath() + "/";
        // Enabling database for resume support even after the application is killed:
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                //.setReadTimeout(30_000)
                //.setConnectTimeout(30_000)
                .build();
        PRDownloader.initialize(getActivity().getApplicationContext(),
                config);
        int downloadId = PRDownloader.download(url, dirPath,
                filePath)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {
                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {
                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {
                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress)
                    {
                        Log.e("Progress",
                                progress.toString());
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        Log.e("Completed", "file completed");
                        pdialog.dismiss();
                        getFragmentManager().popBackStack();
                    }
                    @Override
                    public void onError(Error error) {
                        Log.e("Error",
                                error.isConnectionError()+"");
                        pdialog.dismiss();
                    }
                });

    }

    private Boolean checkIfExist(String filePath) throws
            IOException {
        String path = getActivity().getBaseContext().getFilesDir().getAbsolutePath() + "/" + filePath;
        File file = new File(path);
        Log.e("fileexist", file.getAbsolutePath());
        return file.exists();
    }

}
