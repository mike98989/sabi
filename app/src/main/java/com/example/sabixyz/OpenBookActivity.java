package com.example.sabixyz;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sabixyz.effects.HorizontalFlip;
import com.example.sabixyz.effects.RotateDown;
import com.example.sabixyz.effects.TabletPageTransformer;
import com.folioreader.Config;
import com.folioreader.Constants;
import com.folioreader.FolioReader;
import com.folioreader.model.locators.ReadLocator;
import com.folioreader.util.ReadLocatorListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class OpenBookActivity extends AppCompatActivity implements FolioReader.OnClosedListener {
private String content, description, imageurl;
private LayoutInflater inflater;
    String[] pageData;
    String[] DataContent;
    public TextView tv_book_content, tv_book_content_parent,pageCounter;
    LinearLayout mControls, mPagerContainer;
    ImageView coverImage, mNightMode, mIncreaseFont, mDecreaseFont;
    ViewPager vp, container;
    View page, parent;
    SparseArray<View> views = new SparseArray<View>();
    float currentFontSize=18;
    int NightMode_, currentItem_;
    //String color="#ffffff";
    String color="#000000";
    String BackgroundColor = "#ffffff";
    String filePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        filePath = getIntent().getStringExtra("filepath");
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_book);
        try {
            Boolean checkfile = checkIfExist(filePath);
            Log.e("checkfile", ""+checkfile);
            if(checkfile){
                openReader(filePath);
            }else{
                DownloadEpubFile(filePath);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void DownloadEpubFile(String filePath) {
        final android.app.ProgressDialog pdialog = new ProgressDialog(this);
        pdialog.setMessage("Loading... Please wait.");
        pdialog.setCancelable(true);
        pdialog.show();
        DownloadTask downLoadTask = new DownloadTask(this, filePath, pdialog);
        DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(com.example.sabixyz.Constants.LOCAL_PATH+ com.example.sabixyz.Constants.EPUB_REPO+filePath));
        long queueid = downloadManager.enqueue(request);
    }

    private void openReader(String filePath) {
        Config config = new Config()
                .setAllowedDirection(Config.AllowedDirection.ONLY_HORIZONTAL)
                .setDirection(Config.Direction.HORIZONTAL)
                .setFont(Constants.FONT_LORA)
                .setFontSize(2)
                .setThemeColorRes(R.color.sabiOrange)
                //.setNightMode(true)
                //.setThemeColor(R.color.app_green)
                .setShowTts(true);
        FolioReader folioReader = FolioReader.get()
                .setOnClosedListener(this)
                .setConfig(config, true);
        folioReader.openBook("file:///android_asset/"+filePath);
        //folioReader.openBook("http://192.168.43.128/sabi/epub/epdf.pub_whats-left-of-me.epub");
    }

    private Boolean checkIfExist(String filePath) throws IOException {
        AssetManager mg = getResources().getAssets();
        InputStream is = null;
        try {
            is = mg.open(filePath);
            return true;
            //File exists so do something with it
        } catch (IOException ex) {
            return false;
            //file does not exist
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }


    @Override
    public void onFolioReaderClosed() {
        this.finish();
    }
}
