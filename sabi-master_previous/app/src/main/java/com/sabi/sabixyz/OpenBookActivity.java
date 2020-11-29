package com.sabi.sabixyz;
import android.app.ProgressDialog;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;
import com.folioreader.Config;
import com.folioreader.Constants;
import com.folioreader.FolioReader;
import java.io.File;
import java.io.IOException;
public class OpenBookActivity extends AppCompatActivity
        implements FolioReader.OnClosedListener {
    private String content, description, imageurl;
    private LayoutInflater inflater;
    String[] pageData;
    String[] DataContent;
    public TextView tv_book_content,
            tv_book_content_parent,pageCounter;
    LinearLayout mControls, mPagerContainer;
    ImageView coverImage, mNightMode, mIncreaseFont,
            mDecreaseFont;
    ViewPager vp, container;
    View page, parent;
    SparseArray<View> views = new SparseArray<View>();
    float currentFontSize=18;
    int NightMode_, currentItem_;
    //String color="#ffffff";
    String color="#000000";
    String BackgroundColor = "#ffffff";
    String filePath,fileTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final ProgressDialog pdialog = new
                ProgressDialog(OpenBookActivity.this);
        pdialog.setMessage("Loading Content... Please wait");
        pdialog.setCancelable(false);
        pdialog.show();
        PRDownloader.initialize(getApplicationContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        filePath = getIntent().getStringExtra("filepath");
        fileTitle =
                getIntent().getStringExtra("title").replace("","").toLowerCase();
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
                                        View.SYSTEM_UI_FLAG_IMMERSIVE|
                                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_book);
        try {
            Boolean checkfile = checkIfExist(filePath);
            if(checkfile){
                openReader(filePath, pdialog);
            }else{
                DownloadEpubFile(filePath, pdialog);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void DownloadEpubFile(final String filePath, final
    ProgressDialog pdialog) {
        String url = com.sabi.sabixyz.Constants.LOCAL_PATH+ com.sabi.sabixyz.Constants.EPUB_REPO+filePath;
        //String dirPath = "file:///android_asset/";
        String dirPath =
                getBaseContext().getFilesDir().getAbsolutePath() + "/";
        // Enabling database for resume support even after the application is killed:
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                        .setDatabaseEnabled(true)
                        //.setReadTimeout(30_000)
                        //.setConnectTimeout(30_000)
                        .build();
        PRDownloader.initialize(getApplicationContext(),
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
                        openReader(filePath, pdialog);
                    }
                    @Override
                    public void onError(Error error) {
                        Log.e("Error",
                                error.isConnectionError()+"");
                    }
                });
        Log.e("DownloadId", downloadId+"");
    }
    private void openReader(String filePath, ProgressDialog
            pdialog) {
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

        folioReader.openBook(getBaseContext().getFilesDir().getAbsolutePath() + "/"+filePath);
        pdialog.dismiss();
        //folioReader.openBook("http://192.168.43.128/sabi/epub/epdf.pub_whats-left-of-me.epub");
    }
    private Boolean checkIfExist(String filePath) throws
            IOException {
        String path = getBaseContext().getFilesDir().getAbsolutePath() + "/" + filePath;
        File file = new File(path);
        Log.e("fileexist", file.getAbsolutePath());
        return file.exists();
    }
    @Override
    public void onFolioReaderClosed() {
        this.finish();
    }
}