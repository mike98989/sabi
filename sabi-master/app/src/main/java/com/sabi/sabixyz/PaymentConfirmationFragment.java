package com.sabi.sabixyz;

import android.app.ProgressDialog;
import android.os.Bundle;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;

import java.io.File;
import java.io.IOException;


public class PaymentConfirmationFragment extends Fragment implements View.OnClickListener {
private TextView tv_success_message;
private String paymentReference;
private Button gotoMyBooks;
private String filePath,fileTitle;

    public PaymentConfirmationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.paymentReference = getArguments().getString("reference");
        }

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
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_payment_confirmation, container, false);
        tv_success_message = view.findViewById(R.id.tv_return_success_msg);
        gotoMyBooks = view.findViewById(R.id.btn_goto_my_books);
        gotoMyBooks.setOnClickListener(this);
        tv_success_message.setText("You payment reference is "+paymentReference);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_goto_my_books:
                MainActivity.tv_header_title.setText(R.string.menu_my_books);
                Fragment mybooks = new MyBooksFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, mybooks);
                fragmentTransaction.commit();
                break;
        }
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
